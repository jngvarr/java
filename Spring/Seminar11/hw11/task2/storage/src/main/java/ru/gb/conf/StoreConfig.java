package ru.gb.conf;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {
    //    @Bean
//    public Counter count(MeterRegistry meterRegistry) {
//        return Counter.builder("hw11task_request_counter")
//                .description("Counts request times")
//                .register(meterRegistry);
//    }
    public StoreConfig(MeterRegistry meterRegistry) {
        Counter counter = Counter.builder("hw11task_request_counter")
                .description("Counts request times")
                .register(meterRegistry);
    }
}
