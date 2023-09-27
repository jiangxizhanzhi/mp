package cn.cyberpeace.mp.collection.model.log;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class DeviceLog {
    @ApiModelProperty(value = "文档索引ID，自动生成")
    @Id
    private String id;

    @ApiModelProperty(value = "日志来源设备类型")
    @Field(type = FieldType.Keyword)
    private String type;

    @ApiModelProperty(value = "日志来源设备IP")
    @Field(type = FieldType.Keyword)
    private String ip;

    @ApiModelProperty(value = "日志数据所属业务类型")
    @Field(type = FieldType.Keyword)
    private String logType;

    @ApiModelProperty(value = "日志生成时间")
    @Field(type = FieldType.Keyword)
    private String time;
}
