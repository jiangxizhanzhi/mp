package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.flow.constant.EsDataStorageConstant;
import cn.cyberpeace.mp.flow.model.EsCollectionDataStorageModel;
import cn.cyberpeace.mp.flow.service.EsCollectionDataStorageService;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
public class CopyEsCollectionDataScheduleJob {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private EsCollectionDataStorageService esCollectionDataStorageService;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    private static final String ES_INDEX_BEGIN = "wazuh-alerts-4.x-";

    // 通道是否需要进行存储数据
    boolean tidbNeed = true;
    boolean redisNeed = true;
    boolean kafkaNeed = true;

    @MpScheduled(cron = "0 0 */1 * * ?", description = "复制ES中采集数据", id = "copy_es_collection_data")
    public void execute() {
        log.info("CopyEsCollectionData.execute begin.");

        // 获取时间后缀，当前逻辑是同步前一天的数据
        String date = DateUtils.format(org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -1),
                "yyyy.MM.dd");

        // 获取通道存储状态，判断哪个通道需要存储
        syncStorageNeed(date);

        // 没有需要存储的，直接结束
        if (!redisNeed && !kafkaNeed && !tidbNeed) {
            log.info("CopyEsCollectionData.execute end. data is already stored, date=[{}]", date);
            return;
        }

        // 分页从es中获取所有的数据信息，然后存储到redis/kafka/tidb中
        String esIndexName = ES_INDEX_BEGIN + date;
        JSONArray messageArray = getEsData(esIndexName);

        // 数据存储到redis中
        boolean redisResult = saveRedis(date, esIndexName, messageArray);

        // 数据存储到kafka中
        boolean kafkaResult = saveKafka(date, esIndexName, messageArray);

        // 数据存储到tidb中
        boolean tidbResult = saveTidb(date, esIndexName, messageArray);

        log.info("CopyEsCollectionData.execute end. date=[{}], redisResult[{}], kafkaResult[{}], tidbResult[{}]",
                date, redisResult, kafkaResult, tidbResult);
    }

    /**
     * 判断通道是否需要进行进行存储数据操作
     */
    private void syncStorageNeed(String date) {
        List<EsCollectionDataStorageModel> models = esCollectionDataStorageService.listByDate(date);
        for (EsCollectionDataStorageModel model : models) {
            switch (model.getChannel()) {
                case EsDataStorageConstant.CHANNEL_REDIS:
                    if (EsDataStorageConstant.STATUS_SUCCESS.equals(model.getStatus())) {
                        redisNeed = false;
                    }
                    break;
                case EsDataStorageConstant.CHANNEL_KAFKA:
                    if (EsDataStorageConstant.STATUS_SUCCESS.equals(model.getStatus())) {
                        kafkaNeed = false;
                    }
                    break;
                case EsDataStorageConstant.CHANNEL_TIDB:
                    if (EsDataStorageConstant.STATUS_SUCCESS.equals(model.getStatus())) {
                        tidbNeed = false;
                    }
                    break;
            }
        }
    }

    /**
     * 存储到tidb
     */
    private boolean saveTidb(String date, String esIndexName, JSONArray messageArray) {
        // 不需要进行存储
        if (!tidbNeed) {
            return true;
        }

        // 存储
        boolean result = true;
        try {
            // 连接tidb
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setServerName(situationAwareConfig.getStorageTidbIp());
            mysqlDataSource.setPortNumber(situationAwareConfig.getStorageTidbPort());
            mysqlDataSource.setUser(situationAwareConfig.getStorageTidbUser());
            mysqlDataSource.setPassword(situationAwareConfig.getStorageTidbPassword());
            mysqlDataSource.setDatabaseName(situationAwareConfig.getStorageTidbName());

            // 先进行一次标识数据删除，避免重复存储
            Connection connection = mysqlDataSource.getConnection();
            // 设置为不自动提交数据
            connection.setAutoCommit(false);
            PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM collection_data WHERE `key` LIKE ? ");
            deleteStatement.setString(1, esIndexName + "%");
            deleteStatement.execute();

            // 循环批量插入数据
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO collection_data (`key`, content) VALUES (?, ?)");
            boolean existedData = false;
            for (int i = 1; i <= messageArray.size(); i++) {
                preparedStatement.setString(1, esIndexName + "_" + i);
                preparedStatement.setString(2, messageArray.get(i - 1).toString());

                // ”攒“sql
                preparedStatement.addBatch();
                existedData = true;

                // 每1000条提交一次
                if (i % 1000 == 0) {
                    // 执行
                    preparedStatement.executeBatch();
                    // 清空
                    preparedStatement.clearBatch();
                    existedData = false;
                }
            }
            // 执行
            if (existedData) {
                preparedStatement.executeBatch();
            }
            // 提交数据、关闭连接
            connection.commit();
            connection.close();

            updateStorageStatus(result, date, EsDataStorageConstant.CHANNEL_TIDB);
        } catch (Exception e) {
            // 异常信息不记录，但是需要捕获不影响后续逻辑
            log.error("CopyEsCollectionDataScheduleJob error for tidb", e);
            return false;
        }

        return result;
    }

    /**
     * 存储到kafka
     */
    private boolean saveKafka(String date, String esIndexName, JSONArray messageArray) {
        // 不需要进行存储
        if (!kafkaNeed) {
            return true;
        }

        // 存储
        boolean result = true;
        try {
            Properties prop = new Properties();
            prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, situationAwareConfig.getStorageKafkaAddress());
            prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer");
            prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            KafkaProducer<Integer, String> producer = new KafkaProducer<>(prop);
            // 发送信息
            for (int i = 1; i <= messageArray.size(); i++) {
                JSONObject message = new JSONObject();
                message.put(esIndexName + "_" + i, messageArray.get(i - 1));
                producer.send(new ProducerRecord<Integer, String>(situationAwareConfig.getStorageKafkaTopic(), null, message.toJSONString())).get();
            }

            updateStorageStatus(result, date, EsDataStorageConstant.CHANNEL_KAFKA);
        } catch (Exception e) {
            // 异常信息不记录，但是需要捕获不影响后续逻辑
            log.error("CopyEsCollectionDataScheduleJob error for kafka", e);
            return false;
        }

        return result;
    }

    /**
     * 存储到redis
     */
    private boolean saveRedis(String date, String esIndexName, JSONArray messageArray) {
        // 不需要进行存储
        if (!redisNeed) {
            return true;
        }

        // 存储
        try {
            // 连接Redis服务器
            Jedis jedis = new Jedis(situationAwareConfig.getStorageRedisIp(), situationAwareConfig.getStorageRedisPort());
            jedis.auth(situationAwareConfig.getStorageRedisPassword());

            // 先进行一次标识数据删除，避免重复存储
            jedis.del(esIndexName);

            // 存储数据
            jedis.set(esIndexName, messageArray.toJSONString());
            jedis.close();

            updateStorageStatus(true, date, EsDataStorageConstant.CHANNEL_REDIS);
        } catch (Exception e) {
            // 异常信息不记录，但是需要捕获不影响后续逻辑
            log.error("CopyEsCollectionDataScheduleJob error for redis", e);
            return false;
        }

        return true;
    }

    /**
     * 更新存储记录表
     */
    private void updateStorageStatus(boolean result, String date, String channel) {
        EsCollectionDataStorageModel redisStorage = EsCollectionDataStorageModel.builder()
                .status(result ? EsDataStorageConstant.STATUS_SUCCESS : EsDataStorageConstant.STATUS_ONGOING)
                .date(date).channel(channel).build();
        EsCollectionDataStorageModel model = esCollectionDataStorageService.getByDateAndChannel(date, channel);
        if (null == model) {
            esCollectionDataStorageService.create(redisStorage);
        } else {
            model.setStatus(redisStorage.getStatus());
            esCollectionDataStorageService.update(model);
        }
    }

    /**
     * 分页获取es数据，汇总返回
     */
    private JSONArray getEsData(String esIndexName) {
        // 获取es中采集数据，防止数据过大采用分页的方式
        int pageSize = 100;
        int page = 1;
        JSONArray messageArray = new JSONArray();
        for (int pageNumber = 1; pageNumber <= page; pageNumber++) {

            SearchRequest searchRequest = new SearchRequest(esIndexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from((pageNumber - 1) * pageSize);
            sourceBuilder.size(pageSize);
            searchRequest.source(sourceBuilder);
            SearchResponse response;
            try {
                response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                // 异常，不进行处理
                log.error("CopyEsCollectionDataScheduleJob error, get es data fail", e);
                continue;
            }
            if (null == response) {
                log.error("CopyEsCollectionDataScheduleJob error, es response is null, searchRequest=[{}]", searchRequest);
                continue;
            }

            // 更新数据总数和查询页数
            long count = response.getHits().getTotalHits().value;
            page = (int) Math.ceil(count / (float) pageSize);
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                JSONObject sourceJson = JSONObject.parseObject(sourceAsString);
                messageArray.add(sourceJson);
            }
        }
        return messageArray;
    }
}
