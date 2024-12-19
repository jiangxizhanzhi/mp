package cn.cyberpeace.mp.cscene.dto.flow;

import cn.cyberpeace.mp.situationaware.model.FlowClassificationFromJsonModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
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
        flowClassificationFromJsonModel.getClassifications().forEach(classification -> {
            String attackType = getValueByLocal(locale, classification.getType_en(), classification.getType_cn());

            FlowClassificationDto flowClassificationDto = new FlowClassificationDto().setAttackType(attackType);
            List<FlowEvent> flowEvents = classification.getFlow_events().stream().map(event -> {
                String flowEventType = getValueByLocal(locale, event.getEn_desc(), event.getZh_desc());

                return new FlowEvent()
                        .setFlowEventType(flowEventType)
                        .setSeverityDesc(event.getSeverity());
            }).collect(Collectors.toList());
            flowClassificationDto.setFlowEvents(flowEvents);
            classificationDtoList.add(flowClassificationDto);
        });
        return classificationDtoList;
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
