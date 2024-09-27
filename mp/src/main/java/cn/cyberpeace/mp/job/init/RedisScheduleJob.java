 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RedisScheduleJob.java.
 // File size 2678.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

 import cn.cyberpeace.mp.base.util.mptool.Validator;
import cn.cyberpeace.mp.devopscluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

 import java.util.Date;

 /**
 * @ClassName RedisScheduleJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class RedisScheduleJob {

    @Autowired
    private RealEquipmentConverter realEquipmentConverter;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private TaskScheduler taskScheduler;

    private final static String CHANNEL = "combat_log";

    @MpInitiate(description = "redis 连接", id = "init_read_redis")
    public void readRedis() {
        // 连接到Redis服务器
        Jedis jedis = new Jedis(situationAwareConfig.getStorageRedisIp(), situationAwareConfig.getStorageRedisPort());
        try{
            jedis.auth(situationAwareConfig.getStorageRedisPassword());

            var loadJobTime = new Date(System.currentTimeMillis() + 2 * 1000);
            taskScheduler.schedule(() -> {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {

                        log.info("redis schedule job , combat_log:{}", message);
                        String[] content = message.split(" ", 3);
                        if (content.length == 3 && Validator.IPV4.matcher(content[1]).matches()) {
                            realEquipmentConverter.report(content[1], content[2], RealEquipmentConverter.SYSLOG);
                        }
                    }
                }, CHANNEL);
            }, loadJobTime);

        } catch (Exception e) {
            log.error("RedisScheduleJob error, e = {}", ExceptionUtils.getStackTrace(e));
        }
    }
}
