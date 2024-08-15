package org.vashishth.ratelimiter.tokenbucket;

import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;

public class TokenAdder implements Runnable {
    int key;
    int bucketSize;
    int refillRate;
    int timeout;

    RedisTemplate<Integer, Integer> redisTemplate;

    public TokenAdder(int key, int bucketSize, int refillRate, int timeout, RedisTemplate<Integer, Integer> redisTemplate) {
        this.key = key;
        this.bucketSize = bucketSize;
        this.refillRate = refillRate;
        this.timeout = timeout;
        this.redisTemplate = redisTemplate;
    }

    @SneakyThrows
    @Override
    public void run() {
        System.out.printf("\n\tToken Adder Started for key: %d", key);
        while (redisTemplate.hasKey(key)) {
            int availableTokens = redisTemplate.opsForValue().get(key);
            availableTokens += refillRate;
            if (availableTokens > bucketSize) {
                availableTokens = bucketSize;
            }
            redisTemplate.opsForValue().set(key, availableTokens);
            if (availableTokens == bucketSize) {
                System.out.printf("\n\tBucket(%d) is Full with %d tokens", key, availableTokens);
            } else {
                System.out.printf(
                        "\n\tAdded %d tokens to Bucket(%d); Now has %d tokens",
                        refillRate,
                        key,
                        availableTokens
                );
            }
            Thread.sleep(10000);
        }
        System.out.printf("\n\tToken Adder Ended for key: %d", key);
    }
}
