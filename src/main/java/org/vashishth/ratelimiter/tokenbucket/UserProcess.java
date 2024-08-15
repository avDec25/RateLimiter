package org.vashishth.ratelimiter.tokenbucket;

import lombok.SneakyThrows;

public class UserProcess implements Runnable {

    long runningTime;
    Integer uid;
    long pid;

    public UserProcess(Integer uid, long runningTime) {
        this.uid = uid;
        this.runningTime = runningTime;
        this.pid = System.currentTimeMillis();
    }

    @SneakyThrows
    @Override
    public void run() {
        System.out.printf("\nPid: %d, Processing Request for uid: %d", pid, this.uid);
        long start = System.currentTimeMillis();
        Thread.sleep(this.runningTime);
        System.out.printf(
                "\nPid: %d, Completed Request for uid: %d in %d seconds",
                pid, this.uid, (System.currentTimeMillis() - start) / 1000
        );
    }
}
