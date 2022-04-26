package com.aidtom.framework.common.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * 消息队列操作工厂类
 *
 * BlockingWaitStrategy 是最低效的策略，但其对 CPU 的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现；
 * SleepingWaitStrategy 的性能表现跟 BlockingWaitStrategy 差不多，对 CPU 的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景；
 * YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于 CPU 逻辑核心数的场景中，推荐使用此策略；例如：CPU 开启超线程的特性。
 *
 * @author tanghaihua
 * @date 2021/6/30
 */
public class MessageQueueFactory<T> {
    /**
     * 点对点消息"的操作队列，即同一事件会被一组消费者其中之一消费
     * @param queueSize       队列大小，2的N次方
     * @param isMoreProducer  是否多个生产者
     * @param consumers       消费者
     * @param <T>
     * @return
     */
    public static <T> MessageQueue<T> getWorkPoolQueue(int queueSize, boolean isMoreProducer, AbstractMessageConsumer<T>... consumers) {
        Disruptor<MessageEvent<T>> disruptor = new Disruptor(new MessageEventFactory(), queueSize, Executors.defaultThreadFactory(), isMoreProducer ? ProducerType.MULTI : ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWithWorkerPool(consumers);
        return new MessageQueue(disruptor);
    }

    /**
     * 发布订阅模式-即同一事件会被多个消费者并行消费
     * @param queueSize       队列大小，2的N次方
     * @param isMoreProducer  是否多个生产者
     * @param consumers       消费者
     * @param <T>
     * @return
     */
    public static <T> MessageQueue<T> getHandleEventsQueue(int queueSize, boolean isMoreProducer, AbstractMessageConsumer<T>... consumers) {
        Disruptor<MessageEvent<T>> disruptor = new Disruptor(new MessageEventFactory(), queueSize, Executors.defaultThreadFactory(), isMoreProducer ? ProducerType.MULTI : ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(consumers);
        return new MessageQueue(disruptor);
    }

    /**
     * 直接通过传入的 Disruptor 对象创建操作队列（如果消费者有依赖关系的话可以用此方法）
     * @param disruptor
     * @param <T>
     * @return
     */
    public static <T> MessageQueue<T> getQueue(Disruptor<MessageEvent<T>> disruptor) {
        return new MessageQueue(disruptor);
    }
}
