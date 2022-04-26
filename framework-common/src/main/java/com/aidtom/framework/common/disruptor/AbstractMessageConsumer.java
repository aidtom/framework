package com.aidtom.framework.common.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * 消费者 抽象类
 *
 * @author tanghaihua
 * @date 2021/6/30
 */
public abstract class AbstractMessageConsumer<T> implements EventHandler<MessageEvent<T>>, WorkHandler<MessageEvent<T>> {

    /**
     * EventHandler 发布订阅模式
     * @param event
     * @param l
     * @param b
     * @throws Exception
     */
    @Override
    public void onEvent(MessageEvent<T> event, long l, boolean b) throws Exception {
        this.onEvent(event);
    }

    /**
     * WorkHandler 点对点模式
     * @param event
     * @throws Exception
     */
    @Override
    public void onEvent(MessageEvent<T> event) throws Exception {
        this.consume(event.getMsg());
    }

    /**
     * 消费，由具体子类实现
     * @param msg
     */
    public abstract void consume(T msg);
}
