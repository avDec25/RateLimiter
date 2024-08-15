package org.vashishth.ratelimiter.tokenbucket;

public class RegisterUserRequest {
    public String email;
    public String password;
    public Integer bucketSize;
    public Integer refillRate;
}
