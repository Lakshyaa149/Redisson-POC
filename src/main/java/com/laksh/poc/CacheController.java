package com.laksh.poc;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class CacheController {

    @Autowired
    private RedissonClient rClient;

    @GetMapping
    public void validateLru(){
     /*   RMapCache<String, String> map = rClient.getMapCache("anyMap");

        map.setMaxSize(4);

        map.put("1","a");
        map.put("2","b");
        map.put("3","c");
        map.put("4","d");
        map.put("5","e");

        System.out.println(map.keySet().contains("1"));
        System.out.println(map.keySet());
*/

        RMapCache<String, String> map1 = rClient.getMapCache("anyMap2");

        map1.setMaxSize(3);

        map1.put("1","a",1800000, TimeUnit.MINUTES);
        map1.put("2","b",1800000, TimeUnit.MINUTES);
        map1.get("1");
        map1.put("3","c",1800000, TimeUnit.MINUTES);
        map1.put("4","d",1800000, TimeUnit.MINUTES);
        System.out.println(map1.keySet().contains("1"));
        System.out.println(map1.keySet());

    }




}
