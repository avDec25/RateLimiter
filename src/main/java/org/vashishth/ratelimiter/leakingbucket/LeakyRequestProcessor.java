package org.vashishth.ratelimiter.leakingbucket;

import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LeakyRequestProcessor {
    @SneakyThrows
    @RabbitListener(queues={"leakyrequest"})
    public void processMessage(String data) {
        System.out.println(data);
        Thread.sleep(1000);
    }
}
