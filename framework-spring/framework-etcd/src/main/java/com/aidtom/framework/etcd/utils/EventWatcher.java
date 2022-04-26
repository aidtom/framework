package com.aidtom.framework.etcd.utils;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.WatchOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * KeyPrefix 监听 watcher
 * @author tom
 */
@Slf4j
@RequiredArgsConstructor
public class EventWatcher<K, V> {

    private final Client client;
    @Getter
    private final String key;
    private final boolean isPrefix;
    private final KeyParser<K> keyParser;
    private final Function<KeyValue, V> valueConvertor;
    private Watch.Watcher watcher;
    private Map<K, EventValue<K,V>> prevCache = new ConcurrentHashMap<>();

    public EventWatcher<K, V> watch(EventListener<K, V> el) {
        if (watcher != null) {
            throw new RuntimeException("keyPrefix: " + key + " has been watched");
        }
        synchronized (this) {
            try {
                final ByteSequence keyPrefix = ByteSequence.from(this.key, StandardCharsets.UTF_8);
                WatchOption.Builder builder = WatchOption.newBuilder();
                if (isPrefix) {
                    builder.withPrefix(keyPrefix);
                }
                WatchOption watchOption = builder.build();
                watcher = client.getWatchClient().watch(keyPrefix, watchOption, response -> {
                    response.getEvents().forEach(event -> {
                        EventTypeEnum eventTypeEnum = null;
                        try {
                            K k = keyParser.parser(this.key, event.getKeyValue().getKey());
                            switch (event.getEventType()) {
                                case PUT:
                                    eventTypeEnum = EventTypeEnum.PUT;
                                    EventValue<K, V> newVal = new EventValue<>();
                                    KeyValue newKv = event.getKeyValue();
                                    newVal.setKey(new String(event.getKeyValue().getKey().getBytes()))
                                            .setLease(newKv.getLease())
                                            .setCreateVersion(newKv.getCreateRevision())
                                            .setModVersion(newKv.getModRevision())
                                            .setVersion(newKv.getVersion())
                                            .setKeyObj(k)
                                            .setValue(valueConvertor.apply(newKv));
                                    AtomicReference<EventValue<K, V>> oldReference = new AtomicReference<>();
                                    prevCache.compute(k, (key, old) -> {
                                        oldReference.set(old);
                                        return newVal;
                                    });
                                    if (el != null) {
                                        el.onChange(eventTypeEnum, oldReference.get(), newVal, null);
                                    }
                                    break;
                                case DELETE:
                                    eventTypeEnum = EventTypeEnum.DELETE;
                                    EventValue<K, V> prevVal = prevCache.get(k);
                                    prevCache.remove(k);
                                    if (el != null) {
                                        el.onChange(eventTypeEnum, prevVal, null, null);
                                    }
                                    break;
                                default:
                                    log.warn("unknown event type:{}", event.getEventType());
                            }
                        } catch (Exception e) {
                            log.error("keyPrefix={}, process events error.", keyPrefix, e);
                            if (el != null) {
                                el.onChange(eventTypeEnum, null, null, e);
                            }
                        }
                    });
                });
            } catch (Exception e) {
                throw new RuntimeException("init watcher error.", e);
            }
        }

        return this;
    }
}
