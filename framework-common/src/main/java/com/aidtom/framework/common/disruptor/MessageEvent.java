package com.aidtom.framework.common.disruptor;

import lombok.Data;

/**
 * 消息事件
 *
 * @author tanghaihua
 * @date 2021/6/30
 */
@Data
public class MessageEvent <T>{
    private T msg;
}
