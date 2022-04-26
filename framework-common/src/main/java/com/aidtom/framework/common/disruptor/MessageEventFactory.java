package com.aidtom.framework.common.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 消息事件工厂
 *
 * Factory 在 RingBuffer 中预创建 Event 的实例。一个 Event 实例实际上被用作一个“数据槽”，发布者发布前，
 * 先从 RingBuffer 获得一个 Event 的实例，然后往 Event 实例中填充数据，之后再发布到 RingBuffer 中，
 * 之后由 Consumer 获得该 Event 实例并从中读取数据
 *
 * @author tom
 * @date 2021/6/30
 */
public class MessageEventFactory <T> implements EventFactory<MessageEvent<T>> {

    @Override
    public MessageEvent<T> newInstance() {
        return new MessageEvent<>();
    }
}
