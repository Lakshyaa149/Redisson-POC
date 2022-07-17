

package com.laksh.poc;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

@Slf4j
@Configuration
public class RedisConfig {

    public static final String CONFIG_REDISSON_PREFIX = "/config/redisson/redisson-";
    public static final String YML_PREFIX = ".yml";

    @Bean(
            destroyMethod = "shutdown"
    )
    public RedissonClient redisson() throws IOException {
        String configFileName = StringUtils.join(new String[]{"/config/redisson/redisson-", "dev", ".yml"});

        try {
            ClassPathResource resource = new ClassPathResource(configFileName);
            Config config = Config.fromYAML(resource.getInputStream());
            config.setCodec(new GZipCodec());
            RedissonClient rc =Redisson.create(config);
            /*List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15).parallelStream().forEach(a->validateSrcTransId(rc,a,true));
            for (int i = 0; i < 5; i++) {*/
              // validateSrcTransId(rc,i,true);
            //
            scheduleRequestForAMinute(rc,"lakshay");
            return rc;
        } catch (FileNotFoundException var4) {
            return null;
        }
    }



    private void validateSrcTransId(RedissonClient rClient,long  no, boolean isRefundRequest) {
        long start = System.currentTimeMillis();
        /*log.info("Start Time to validate srcTransId {} ", start);
          System.out.println("rclient"+rClient);*/
        RLock lock = rClient.getLock("mykey");
        if (lock.tryLock()) {
            try {

                System.out.println("Hi");
            } catch (Exception ex) {
                log.error("processPendingAutoRenewals exception occurred ", ex);
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        }
        else{
            System.out.println("No lock");
        }
    }
    public Object scheduleRequestForAMinute(RedissonClient client,String userId){
/**
 * "redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1] - 60 * 1000) \n" +
 */

       // String lua="\"redis.call('zadd', KEYS[1], KEYS[2], KEYS[3]);\";";
      //  String lua="return redis.call('ZCARD', KEYS[1])";
        String luaScript= "if redis.call('ZCARD', KEYS[1]) < 5" +
                "then" +
                "redis.call('ZADD', KEYS[1], KEYS[2], KEYS[3])" +
                "return 'pass'" +
                "else" +
                "treturn 'exceeded limit'" +
                "end;";


        long time=Calendar.getInstance().getTimeInMillis()   ;
        return client.getScript().eval(RScript.Mode.READ_WRITE,luaScript, RScript.ReturnType.VALUE, Arrays.<Object>asList(userId, time,String.valueOf(time)));
    }
}





