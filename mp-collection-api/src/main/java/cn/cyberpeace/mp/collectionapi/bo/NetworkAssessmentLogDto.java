package cn.cyberpeace.mp.collectionapi.bo;

import cn.cyberpeace.mp.collection.model.log.Assessment;
import cn.cyberpeace.mp.collection.model.log.NetworkAssessmentLogPo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @ClassName NetworkAssessmentLog
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 18:35
 **/
@Data
public class NetworkAssessmentLogDto {

    private Assessment systematizeScenarios;

    private Assessment mimicRouter;

    private Assessment mimicWeb;

    private Assessment mimicCloud;

    private Assessment mimicDns;

    private Assessment mimicStorage;

    public static NetworkAssessmentLogPo convertToPo(NetworkAssessmentLogDto networkAssessmentLogDto) {
        NetworkAssessmentLogPo networkAssessmentLogPo = new NetworkAssessmentLogPo();
        BeanUtils.copyProperties(networkAssessmentLogDto, networkAssessmentLogPo);
        networkAssessmentLogPo.setReportTime(LocalDateTime.now());
        return networkAssessmentLogPo;
    }

    public static NetworkAssessmentLogDto getFromPo(NetworkAssessmentLogPo networkAssessmentLogPo) {
        NetworkAssessmentLogDto networkAssessmentLogDto = new NetworkAssessmentLogDto();
        BeanUtils.copyProperties(networkAssessmentLogPo, networkAssessmentLogDto);
        return networkAssessmentLogDto;
    }

}
