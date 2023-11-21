package cn.cyberpeace.mp.collectionapi.bo;

import cn.cyberpeace.mp.collection.model.log.Assessment;
import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import cn.cyberpeace.mp.collectionapi.request.NetworkLogQuery;
import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 *
 * 日志墙展示的网络波动评分数据
 * @ClassName NetworkAssessmentLogScreenDto
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/17 14:36
 **/
@Data
public class NetworkAssessmentLogScreenDto {

    private Assessment systematizeScenarios;

    private Assessment mimicRouter;

    private Assessment mimicWeb;

    private Assessment mimicCloud;

    private Assessment mimicDns;

    private Assessment mimicStorage;

    private String reportTime;

    public static NetworkAssessmentLogScreenDto getFromPo(NetworkAssessmentLogPo networkAssessmentLogPo) {
        NetworkAssessmentLogScreenDto networkAssessmentLogScreenDto = new NetworkAssessmentLogScreenDto();
        BeanUtils.copyProperties(networkAssessmentLogPo, networkAssessmentLogScreenDto);
        networkAssessmentLogScreenDto.setReportTime(LocalDateTimeUtil.format(networkAssessmentLogPo.getReportTime(), NetworkLogQuery.dateTimeFormatter));
        return networkAssessmentLogScreenDto;
    }
}
