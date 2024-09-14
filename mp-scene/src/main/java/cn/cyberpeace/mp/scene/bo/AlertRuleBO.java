package cn.cyberpeace.mp.scene.bo;

import cn.cyberpeace.mp.scene.model.AlarmConditionModel;
import cn.cyberpeace.mp.scene.model.AlertRuleModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class AlertRuleBO extends AlertRuleModel {
    @ApiModelProperty("告警条件对象集合")
    private List<AlarmConditionModel> alarmConditionList;

    @ApiModelProperty("主机对象集合")
    private List<String> hostList;

    //    虚拟机资源id集合
    private List<String> vmResourceIdList;

    // 解析方法
    public void parseAlertConditions() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (getAlarmConditions() != null && !getAlarmConditions().isEmpty()) {
                alarmConditionList = objectMapper.readValue(getAlarmConditions(), objectMapper.getTypeFactory().constructCollectionType(List.class, AlarmConditionModel.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseHosts() {
        String hosts = getHosts();
        if (hosts != null && !hosts.isEmpty()) {
            hostList = List.of(hosts.split(","));
        }
    }

    public enum Grade {

        HIGH(10, 0, "高"),
        MIDDLE(5, 1, "中"),
        LOW(0, 2, "低");

        @Getter
        private final Integer code;
        @Getter
        private final Integer azureCode;

        @Getter
        private final String desc;

        Grade(Integer code, Integer azureCode, String desc) {
            this.code = code;
            this.azureCode = azureCode;
            this.desc = desc;
        }

        public static Integer getAzureCodeByCode(Integer code) {
            for (Grade grade : values()) {
                if (grade.getCode().equals(code)) {
                    return grade.getAzureCode();
                }
            }
            return LOW.azureCode;
        }

        public static Integer getCodeByAzureCode(Integer azureCode) {
            for (Grade grade : values()) {
                if (grade.getAzureCode().equals(azureCode)) {
                    return grade.getCode();
                }
            }
            return LOW.code;
        }

    }


}
