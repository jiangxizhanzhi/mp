package cn.cyberpeace.mp.collection.model.log;

import cn.cyberpeace.mp.collection.constant.DeviceLogConstant;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@Document(indexName = DeviceLogConstant.DEVICE_LOG)
@Setting(replicas = 0)
@TypeAlias("device_log")
public class DeviceLog {
    @ApiModelProperty(value = "文档索引ID，自动生成")
    @Id
    private String id;

    @ApiModelProperty(value = "日志接收时间")
    @JSONField(name = "@timestamp")
    @Field(name = "@timestamp", type = FieldType.Long)
    private Long timestamp;

    @ApiModelProperty(value = "日志来源设备类型")
    @JSONField(name = "device_type")
    @Field(value = "device_type", type = FieldType.Keyword)
    private String type;

    @ApiModelProperty(value = "日志来源设备IP")
    @JSONField(name = "device_ip")
    @Field(value = "device_ip", type = FieldType.Keyword)
    private String ip;

    @ApiModelProperty(value = "日志数据所属业务类型")
    @JSONField(name = "log_type")
    @Field(value = "log_type", type = FieldType.Keyword)
    @JsonProperty("log_type")
    private String logType;

    @ApiModelProperty(value = "日志生成时间")
    @JSONField(name = "log_time")
    @Field(value = "log_time", type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
    private String time;

    /**
     * 原始日志文本
     */
    @ApiModelProperty(value = "原始日志数据")
    @Field(type = FieldType.Text)
    private Object message;
}
