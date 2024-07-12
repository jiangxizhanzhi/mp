 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name KafkaScheduleJob.java.
 // File size 2795.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.devopscluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;


/**
 * @ClassName KafkaScheduleJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class KafkaScheduleJob {

	@Autowired
	private SituationAwareConfig situationAwareConfig;

	@Autowired
	private RealEquipmentConverter realEquipmentConverter;

	@Autowired
	private TaskScheduler taskScheduler;

	@PostConstruct
	public void action() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, situationAwareConfig.getStorageKafkaAddress());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "my_group");

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);){
			consumer.subscribe(Collections.singletonList("common_collect_data"));

			var loadJobTime = new Date(System.currentTimeMillis() + 2 * 1000);
			taskScheduler.schedule(() -> {
				while (true) {
					// 拉取消息
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); // 100ms轮询一次

					for (ConsumerRecord<String, String> record : records) {
						try	{
							JSONObject data = JSONObject.parseObject(record.value());
							String ip = data.getString("ip");
							String content = data.getString("content");
							realEquipmentConverter.report(ip, content, RealEquipmentConverter.KAFKA);
						}catch (JSONException e) {
							log.error("message = {}, e = {}", record.value(), ExceptionUtils.getStackTrace(e));
						}

					}
				}
			}, loadJobTime);
		} catch (Exception e) {
			log.error("KafkaScheduleJob error, e = {}", ExceptionUtils.getStackTrace(e));
		}

	}
}
