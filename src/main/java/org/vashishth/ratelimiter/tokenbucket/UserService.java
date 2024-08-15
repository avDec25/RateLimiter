package org.vashishth.ratelimiter.tokenbucket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Service
public class UserService {
    @Value("${redis.key-timeout}")
    String keyTimeout;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Gson gson;

    @Autowired
    RedisTemplate<Integer, Integer> redisTemplate;

    @Transactional
    public String registerUser(RegisterUserRequest request) {
        String sql = String.format(
                "INSERT INTO user (email, password, bucket_size, refill_rate) VALUES ('%s', '%s', %d, %d);",
                request.email, request.password, request.bucketSize, request.refillRate
        );
        jdbcTemplate.update(sql);

        JsonObject response = new JsonObject();
        response.addProperty("message", "Added User");
        return gson.toJson(response);
    }

    public String performBusyWait(Integer uid, long timespan) {
        JsonObject response = new JsonObject();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(uid))) {
            Integer availableTokens = redisTemplate.opsForValue().get(uid);
            if (availableTokens <= 0) {
                System.out.printf("\nEmpty Bucket(%d)", uid);
                response.addProperty("message", "Request Denied");
                return gson.toJson(response);
            } else {
                redisTemplate.opsForValue().set(uid, availableTokens-1);
                redisTemplate.expire(uid, Duration.ofSeconds(Integer.parseInt(keyTimeout)));
                System.out.printf("\nBucket(%d) now has %d tokens", uid, availableTokens-1);
            }
        } else {
            startRateLimiter(uid);
        }
        Thread.ofVirtual().start(new UserProcess(uid, timespan));
        response.addProperty("message", "Request Accepted");
        return gson.toJson(response);
    }

    private void startRateLimiter(Integer uid) {
        String sql = String.format("select bucket_size, refill_rate from user where user_id = %s", uid);
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                redisTemplate.opsForValue().set(uid, rs.getInt("bucket_size"));
                redisTemplate.expire(uid, Duration.ofSeconds(Integer.parseInt(keyTimeout)));
                new Thread(new TokenAdder(
                        uid,
                        rs.getInt("bucket_size"),
                        rs.getInt("refill_rate"),
                        Integer.parseInt(keyTimeout),
                        redisTemplate)
                ).start();
            }
        });
    }
}
