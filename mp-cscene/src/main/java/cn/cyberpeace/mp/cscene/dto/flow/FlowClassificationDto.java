package cn.cyberpeace.mp.cscene.dto.flow;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.code.ErrorCode;
import cn.cyberpeace.mp.base.util.mptool.NumberUtil;
import cn.cyberpeace.mp.common.constant.CommonErrorCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.situationaware.bo.AttackClassificationBO;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationFromJsonModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName FlowClassificationDto
 * @Description 从classification.json取到的流量类型分类数据，提供给外界
 * @Author zhanzhi
 * @Date 2023/9/19 13:54
 **/
@Data
@Accessors(chain = true)
public class FlowClassificationDto {

    private String attackType;

    private List<FlowEvent> flowEvents;

    @Data
    @Accessors(chain = true)
    public static class FlowEvent {
        private String flowEventType;

        private Integer severityDesc;
    }

    public static List<FlowClassificationDto> getDtoListFromModel(FlowClassificationFromJsonModel flowClassificationFromJsonModel) {
        Locale locale = LocaleContextHolder.getLocale();
        List<FlowClassificationDto> classificationDtoList = new ArrayList<>();
        for (FlowClassificationFromJsonModel.FlowType classification : flowClassificationFromJsonModel.getClassifications()) {
            String attackType = getValueByLocal(locale, classification.getType_en(), classification.getType_cn());

            FlowClassificationDto flowClassificationDto = new FlowClassificationDto().setAttackType(attackType);
            List<FlowEvent> flowEvents = new ArrayList<>();
            for (AttackClassificationBO.Classification event : classification.getFlow_events()) {
                FlowEvent flowEvent = getFlowEvent(locale, event);
                flowEvents.add(flowEvent);
            }
            flowClassificationDto.setFlowEvents(flowEvents);
            classificationDtoList.add(flowClassificationDto);
        }
        return classificationDtoList;
    }

    private static FlowEvent getFlowEvent(Locale locale, AttackClassificationBO.Classification event) {
        String flowEventType = getValueByLocal(locale, event.getEn_desc(), event.getZh_desc());

        return new FlowEvent()
                .setFlowEventType(flowEventType)
                .setSeverityDesc(event.getSeverity());
    }

    private static String getValueByLocal(Locale locale, String enValue, String cnValue) {
        boolean chineseLocalFlag = getChineseLocalFlag(locale);
        if (chineseLocalFlag) {
            return cnValue;
        } else {
            return enValue;
        }
    }

    private static boolean getChineseLocalFlag(Locale locale) {
        return Objects.isNull(locale) || StringUtils.isBlank(locale.getLanguage()) || Locale.CHINA.getLanguage().equals(locale.getLanguage());
    }


}
