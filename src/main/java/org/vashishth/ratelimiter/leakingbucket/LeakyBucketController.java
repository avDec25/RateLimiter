package org.vashishth.ratelimiter.leakingbucket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.amqp.core.Message;

import java.util.Date;

@Controller
@RequestMapping("leaky")
public class LeakyBucketController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    Gson gson;

    @PostMapping("execute")
    public ResponseEntity<?> executeProcessWithLeakyBucket(@RequestBody LeakyBucketRequest leakyRequest) {
        JsonObject json = new JsonObject();
        json.addProperty("username", leakyRequest.userName);
        json.addProperty("processingTime", leakyRequest.processingTime);
        json.addProperty("sentTime", new Date().toString());

        Message message = new Message(gson.toJson(json).getBytes());
        try {
            rabbitTemplate.send("leakyexchange", "leakyroutingkey", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Request Registered.");
    }
}
