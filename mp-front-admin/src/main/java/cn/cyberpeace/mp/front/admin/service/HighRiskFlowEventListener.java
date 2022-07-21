package cn.cyberpeace.mp.front.admin.service;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.front.admin.constant.MailConstant;
import cn.cyberpeace.mp.front.admin.dto.CsceneHighRiskFlowStatisticDto;
import cn.cyberpeace.mp.front.admin.event.HighRiskAttachEvent;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/18 14:57
 */
@Slf4j
@Service
public class HighRiskFlowEventListener {

    @Autowired
    private HighRiskFlowNoticeService noticeService;

    @EventListener
    public void onEvent(HighRiskAttachEvent highRiskAttachEvent) {
        log.info("event:{}", highRiskAttachEvent);
        String jsonStr = (String) highRiskAttachEvent.getSource();
        List<CsceneHighRiskFlowStatisticDto> dtos = JSONObject.parseObject(jsonStr, new TypeReference<List<CsceneHighRiskFlowStatisticDto>>(){});
        if (CollectionUtil.isNotEmpty(dtos)) {
            String content = processNoticeContent(dtos);
            noticeService.sendNotice(content);
        }
    }


    private String processNoticeContent(List<CsceneHighRiskFlowStatisticDto> dtos) {
        List<String> columns = new ArrayList<>();
        for (CsceneHighRiskFlowStatisticDto dto : dtos) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("name", dto.getName());
            paramMap.put("id", dto.getId());
            paramMap.put("highRiskCount", dto.getHighRiskCount().toString());
            paramMap.put("startTime", TimeUtils.localDateTimeToString(dto.getStartTime()));
            paramMap.put("endTime",TimeUtils.localDateTimeToString(dto.getEndTime()));
            String column = processTemplate(MailConstant.COLUMN_TEMPLATE, paramMap);
            columns.add(column);
        }
        String body = StringUtils.join(columns, StringUtils.EMPTY);
        return MailConstant.CONTENT_HEADER_TEMPLATE + body + MailConstant.CONTENT_TAIL_TEMPLATE;
    }

    private String processTemplate(String template, Map<String, String> params) {
        if (template == null || params == null)
            return null;
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\$\\{\\w+\\}").matcher(template);
        while (m.find()) {
            String param = m.group();
            String value = params.get(param.substring(2, param.length() - 1));
            m.appendReplacement(sb, value == null ? "" : value);
        }
        m.appendTail(sb);
        return sb.toString();
    }


}
