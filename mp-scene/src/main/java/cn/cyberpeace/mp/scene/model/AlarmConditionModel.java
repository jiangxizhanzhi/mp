package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AlarmConditionModel {
    // 报警条件的类型，例如 CPU 使用率、内存使用率等
    @DefaultStr
    private String type;

    // 聚合类型，例如平均值、最大值等
    @DefaultStr
    private String aggregationType;

    @DefaultStr
    private String aggregationTypeName;

    // 运算符，用于比较阈值，例如 >, <, >=, <= 等
    @DefaultStr
    private String operator;

    @DefaultStr
    private String operatorName;

    // 阈值，当聚合后的数据与该阈值的关系满足运算符定义时触发报警
    @DefaultInt
    private double threshold;
}
