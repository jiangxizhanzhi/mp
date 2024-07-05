package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.devopscluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
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
	public void	readRedis() {
		// 连接到Redis服务器
		Jedis jedis = new Jedis(situationAwareConfig.getStorageRedisIp(), situationAwareConfig.getStorageRedisPort());
		jedis.auth(situationAwareConfig.getStorageRedisPassword());

		var loadJobTime = new Date(System.currentTimeMillis() + 2 * 1000);
		taskScheduler.schedule(() -> {

			jedis.subscribe(new JedisPubSub() {
				@Override
				public void onMessage(String channel, String message) {
					log.info("redis schedule job , combat_log:{}", message);
					try	{
						message = message.substring(message.indexOf("{"));
						JSONObject data = JSONObject.parseObject(message);
						String ip = data.getString("ip");
						String content = data.getString("content");
						realEquipmentConverter.report(ip, content, RealEquipmentConverter.SYSLOG);
					}catch (JSONException e) {
						log.error("message = {}, e = {}", message, ExceptionUtils.getStackTrace(e));
					}
				}
			}, CHANNEL);

		}, loadJobTime);
	}
}
