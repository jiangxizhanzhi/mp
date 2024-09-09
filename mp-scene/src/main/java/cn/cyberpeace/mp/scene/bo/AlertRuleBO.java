package cn.cyberpeace.mp.scene.bo;

import cn.cyberpeace.mp.scene.model.AlarmConditionModel;
import cn.cyberpeace.mp.scene.model.AlertRuleModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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


}
