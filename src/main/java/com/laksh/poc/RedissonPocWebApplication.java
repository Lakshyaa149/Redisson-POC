package com.laksh.poc;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


@Slf4j
@SpringBootApplication
public class RedissonPocWebApplication {

    @Autowired
    private RedissonClient rClient;


    public static void main(String[] args) throws IOException {
        SpringApplication.run(RedissonPocWebApplication.class, args);

    }

}



