package com.example.bookstore.auth;


import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.annotation.Id;
import lombok.*;

import java.util.Date;

@RedisHash("InvalidatedToken")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class InvalidatedToken {

    @Id
    String id;
    
    @TimeToLive
    Long timeToLive;
}
