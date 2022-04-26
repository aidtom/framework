package com.aidtom.framework.kafka.puller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.InterruptException;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * kafka消费者
 *
 * @author tom
 * @date 2020/11/25 17:35
 */
@Data
@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class KafkaPuller<K, V> {

    private final String name;

    private final Consumer<K, V> consumer;

    private final Duration pullTimeout;

    private final AtomicLong pending = new AtomicLong(0);

    private final AtomicBoolean active = new AtomicBoolean(false);

    private Disposable puller;

    private FluxSink<KafkaPuller<K, V>> pullSink;

    private FluxSink<ConsumerRecords<K, V>> recordSink;

    private CountFilter countFilter;

    public Flux<ConsumerRecords<K, V>> pull() {
        return initialize(pulledCount -> Math.min(1, pulledCount), it -> it);
    }

    public Flux<ConsumerRecord<K, V>> pullAndFlat() {
        return initialize(pulledCount -> pulledCount, recordsFlux -> recordsFlux
                .concatMap(Flux::fromIterable, Integer.MAX_VALUE));
    }

    private <T> Flux<T> initialize(CountFilter countFilter, Function<Flux<ConsumerRecords<K, V>>, Flux<T>> transformer) {

        if (!active.compareAndSet(false, true)) {
            throw new RuntimeException("KafkaPuller is already initialized.");
        }

        pending.set(0);

        this.countFilter = countFilter;

        final EmitterProcessor<KafkaPuller<K, V>> pullerEmitter = EmitterProcessor.create();

        pullSink = pullerEmitter.sink(FluxSink.OverflowStrategy.BUFFER);

        puller = pullerEmitter
                .publishOn(Schedulers.newSingle(name + "-puller"))
                .subscribe(KafkaPuller::doPull);

        final EmitterProcessor<ConsumerRecords<K, V>> recordEmitter = EmitterProcessor.create();

        recordSink = recordEmitter.sink();

        final Flux<ConsumerRecords<K, V>> recordsFlux = recordEmitter
                .publishOn(Schedulers.newSingle(name + "-worker"))
                .doAfterTerminate(this::close)
                .doOnCancel(this::close);

        return transformer.apply(recordsFlux).doOnRequest(r -> {
            if (active.get() && addPending(r) > 0) {
                pullSink.next(this);
            }
        });

    }

    private void doPull() {

        if (pending.get() <= 0) {
            return;
        }

        int pulledCount = 0;
        try {
            final ConsumerRecords<K, V> records = consumer.poll(pullTimeout);
            pulledCount = records.count();
            if (pulledCount > 0) {
                recordSink.next(records);
            }
        } catch (Throwable e) {
            if (e instanceof InterruptException && !active.get()) {
                log.warn("{} poll & process records interrupted.", name);
                return;
            }
            log.warn("{} poll & process records form kafka error.", name, e);
        } finally {
            log.trace("{} polled. pulled={} pending={}", name, pulledCount, pending);
            if (active.get() && subPending(countFilter.apply(pulledCount)) > 0) {
                pullSink.next(this);
            }
        }

    }

    private long addPending(long request) {
        long newPending;
        long origin;
        do {
            origin = pending.get();
            newPending = origin + request;
            if (origin > 0 && newPending < 0) {
                newPending = Long.MAX_VALUE;
            }
        } while (!pending.compareAndSet(origin, newPending));
        return newPending;
    }

    private long subPending(long count) {
        return pending.addAndGet(-count);
    }

    private void close() {
        puller.dispose();
        active.compareAndSet(true, false);
    }

    private interface CountFilter {
        long apply(long pulledCount);
    }
}
