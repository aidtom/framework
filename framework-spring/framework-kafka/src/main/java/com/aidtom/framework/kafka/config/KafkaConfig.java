package com.aidtom.framework.kafka.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * kafka
 *
 * @author tanghaihua
 * @date 2020/11/25 17:35
 */
public class KafkaConfig extends HashMap<String, Object> {

    public void putAllAbsent(Map<? extends String, ?> valuesMap) {
        valuesMap.forEach(this::putIfAbsent);
    }

    public void computeIfAbsent(String key, Supplier<?> valueSupplier) {

        if (containsKey(key)) {
            return;
        }

        Optional.ofNullable(valueSupplier.get())
                .ifPresent(value -> put(key, value));

    }

}
