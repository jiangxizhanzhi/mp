package cn.cyberpeace.mp.front.admin.service;

import cn.cyberpeace.mp.front.admin.dto.CsceneHighRiskFlowStatisticDto;
import cn.cyberpeace.mp.front.admin.event.HighRiskAttachEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/18 14:57
 */
@Slf4j
@Service
public class HighRiskFlowEventListener {


    @EventListener
    public void onEvent(HighRiskAttachEvent highRiskAttachEvent) {
        log.info("event:{}", highRiskAttachEvent);
        List<CsceneHighRiskFlowStatisticDto> dtos = (List<CsceneHighRiskFlowStatisticDto>) highRiskAttachEvent.getSource();
    }


    private void sendNotice(List<CsceneHighRiskFlowStatisticDto> dtos) {

    }


}
