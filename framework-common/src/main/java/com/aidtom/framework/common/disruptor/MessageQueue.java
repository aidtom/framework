package com.aidtom.framework.common.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Iterator;
import java.util.List;

/**
 * 消息队列操作
 *
 * @author tom
 * @date 2021/6/30
 */
public class MessageQueue<T> {
    private Disruptor<MessageEvent<T>> disruptor;
    private RingBuffer<MessageEvent<T>> ringBuffer;

    public MessageQueue(Disruptor<MessageEvent<T>> disruptor) {
        this.disruptor = disruptor;
        this.ringBuffer = disruptor.getRingBuffer();
        this.disruptor.start();
    }

    /**
     * 单条消息添加
     *
     * @param t
     */
    public void add(T t) {
        if (t != null) {
            long sequence = this.ringBuffer.next();
            try {
                MessageEvent<T> event = this.ringBuffer.get(sequence);
                event.setMsg(t);
            } finally {
                this.ringBuffer.publish(sequence);
            }
        }
    }

    /**
     * 集合消息添加
     *
     * @param list
     */
    public void addList(List<T> list) {
        if (list != null && list.size() > 0) {
            Iterator<T> obj = list.iterator();

            while (obj.hasNext()) {
                T t = obj.next();
                if (t != null) {
                    this.add(t);
                }
            }
        }
    }

    public Disruptor<MessageEvent<T>> getDisruptor() {
        return this.disruptor;
    }

    public void setDisruptor(Disruptor<MessageEvent<T>> disruptor) {
        this.disruptor = disruptor;
    }

    public RingBuffer<MessageEvent<T>> getRingBuffer() {
        return this.ringBuffer;
    }

    public void setRingBuffer(RingBuffer<MessageEvent<T>> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
}
