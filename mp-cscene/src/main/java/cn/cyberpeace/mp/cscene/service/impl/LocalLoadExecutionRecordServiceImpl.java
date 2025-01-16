package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.cscene.model.LocalLoadExecutionRecordModel;
import cn.cyberpeace.mp.cscene.repository.LocalLoadExecutionRecordMapper;
import cn.cyberpeace.mp.cscene.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.LocalLoadExecutionRecordService;
import cn.cyberpeace.mp.cscene.service.filed.LocalLoadExecutionRecordFields;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.utils.LocalDateUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liyahao
 * @description 针对表【mp_local_load_execution_record(本地载荷执行记录表)】的数据库操作Service实现
 * @createDate 2024-12-27 17:28:05
 */

@Slf4j
@Service
public class LocalLoadExecutionRecordServiceImpl extends BaseModelServiceImpl<LocalLoadExecutionRecordModel, LocalLoadExecutionRecordMapper, LocalLoadExecutionRecordFields,
        ScreenshotQueryParams> implements LocalLoadExecutionRecordService {

    public LocalLoadExecutionRecordServiceImpl(LocalLoadExecutionRecordMapper stuMapper, LocalLoadExecutionRecordFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Resource
    private CsceneInstanceService csceneInstanceService;
    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private LocalLoadExecutionRecordMapper localLoadExecutionRecordMapper;

    /**
     * 根据状态处理本地载荷执行记录，并根据流量数据更新记录状态
     *
     * @return 已处理的执行记录列表
     */
    @Override
    public List<LocalLoadExecutionRecordModel> processExecutionRecordsByStatus() {
//        查走完流程的本地载荷执行记录
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterIn(LocalLoadExecutionRecordModel::getStatus,
                new Integer[]{LocalLoadExecutionRecordModel.Status.EXECUTED.getCode(),
                        LocalLoadExecutionRecordModel.Status.NOT_EXECUTED.getCode()});
        List<LocalLoadExecutionRecordModel> list = localLoadExecutionRecordMapper.list(humanCondition);
        ArrayList<LocalLoadExecutionRecordModel> recordModelArrayList = new ArrayList<>();
        LocalDateTime endTime = LocalDateTime.now();
        if (list.size() > 0) {
            for (LocalLoadExecutionRecordModel model : list) {
//	循环查询是否有对应记录的流量数据
                long count = countMatchingFlowLogs(model.getScene(), model.getCreateTime(), endTime, model.getSourceIp(), model.getDestinationIp());
                if (count > 0) {
//	有-》发送态势动效-》修改记录状态为已完成态势
                    model.setStatus(LocalLoadExecutionRecordModel.Status.ANIMATION_SENT.getCode());
                    model.setDemoStatus(LocalLoadExecutionRecordModel.Status.ANIMATION_SENT.getCode());
                    recordModelArrayList.add(model);
                } else if (model.getUpdateTime().plusMinutes(5).isBefore(endTime)) {
//	无-》检查创建时间与查询时间间隔是否大于5分钟，大于-》修改记录状态未未执行载荷动效
                    model.setStatus(LocalLoadExecutionRecordModel.Status.NO_ANIMATION.getCode());
                    model.setDemoStatus(LocalLoadExecutionRecordModel.Status.ANIMATION_SENT.getCode());
                } else {
                    break;
                }
                localLoadExecutionRecordMapper.update(model);
            }
        }
        return recordModelArrayList;
    }

    @Override
    public List<LocalLoadExecutionRecordModel> getDemoExecutionRecords() {
        //        查走完流程的本地载荷执行记录
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(LocalLoadExecutionRecordModel::getDemoStatus,LocalLoadExecutionRecordModel.Status.NOT_EXECUTED.getCode());
        List<LocalLoadExecutionRecordModel> list = localLoadExecutionRecordMapper.list(humanCondition);
        list.stream().forEach(model->model.setDemoStatus(LocalLoadExecutionRecordModel.Status.EXECUTED.getCode()));
        localLoadExecutionRecordMapper.bulkUpdate(list);
        return list;
    }

    @Override
    public LocalLoadExecutionRecordModel getExecutionRecord(String id) {
        LocalLoadExecutionRecordModel localLoadExecutionRecordModel = localLoadExecutionRecordMapper.get(id);
        return localLoadExecutionRecordModel;
    }


    /**
     * 查询指定时间段内，指定源IP和目的IP的流量数据条数
     *
     * @param csceneId
     * @param localDateStartTime
     * @param localDateEndTime
     * @param sourceIp
     * @param destinationIp
     * @return
     */
    public long countMatchingFlowLogs(
            String csceneId, LocalDateTime localDateStartTime, LocalDateTime localDateEndTime, String sourceIp, String destinationIp) {
        LocalDateTime localDateTime = localDateStartTime.minusHours(8);
        List<String> dateStringList = LocalDateUtils.fetchFormatterTime(localDateTime, localDateEndTime);
// 8 小时的毫秒数
        long format = localDateStartTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long format1 = localDateEndTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        log.info("countMatchingFlowLogs startTime:{},endTime:{}", format, format1);
        String sceneId = csceneInstanceService.getSceneId(csceneId);
        if (sceneId == null) {
            return 0;
        }

        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        String flowCollector = pluginHandler.getScene().getFlowCollector();
        if (StringUtils.isEmpty(flowCollector)) {
            return 0;
        }

        List<String> index = dateStringList.stream().map(dateString -> {
                    StringBuilder sessionIndexBuilder = new StringBuilder();
                    sessionIndexBuilder.append(flowCollector).append("_").append("sessions3").append("-")
                            .append(dateString);
                    return sessionIndexBuilder.toString();
                })
                .collect(Collectors.toList());

        SearchRequest searchRequest = new SearchRequest(index.toArray(new String[0]));
//        3缓存中取事件节点查询时间范围内流量数据
        //时间查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp")
                .from(localDateStartTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .to(localDateEndTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        sourceBuilder.size(10000);
        searchRequest.source(sourceBuilder.query(boolQueryBuilder));
        //配置索引为空
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        log.info("elasticsearch JSON query index:{}, DSL:{}", index, searchRequest.source().toString());
        long totalHits = 0;
        List<FlowLogBO> flowLogBOList = new ArrayList<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            totalHits = hits.getTotalHits().value;
            log.info("countMatchingFlowLogs totalHits:{}", totalHits);
            SearchHit[] searchHits = hits.getHits();
            flowLogBOList = Arrays.stream(searchHits).map(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                FlowLogBO flowLogBO = JSONObject.parseObject(hit.getSourceAsString(), FlowLogBO.class);
                flowLogBO.setTimestamp(Long.valueOf(sourceAsMap.get("@timestamp").toString()));
                return flowLogBO;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("es queryFileEvents error: {} ", ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
        }
        if (flowLogBOList.size() > 0) {
            long count = flowLogBOList.stream()
                    .filter(flowLogBO -> (sourceIp.equals(flowLogBO.getSource().getIp())
                            && destinationIp.equals(flowLogBO.getDestination().getIp()))
                            || (sourceIp.equals(flowLogBO.getDestination().getIp())
                            && destinationIp.equals(flowLogBO.getSource().getIp())))
                    .count();
            return count;
        }
        return 0;
    }
}




