package org.vashishth.ratelimiter.tokenbucket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TokenBucketConfig {
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
        jedisConFactory.setHostName("localhost");
        jedisConFactory.setPort(6379);
        jedisConFactory.setPassword("redispass");
        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<Integer, Integer> redisTemplate() {
        RedisTemplate<Integer, Integer> template = new RedisTemplate<Integer, Integer>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }

}
