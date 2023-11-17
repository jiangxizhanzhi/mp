package cn.cyberpeace.mp.collection.model.log;

import cn.cyberpeace.mp.collection.constant.DeviceLogConstant;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * @ClassName NetworkAssessmentLogPo
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 19:08
 **/
@Data
@Accessors(chain = true)
@Document(indexName = DeviceLogConstant.NETWORK_LOG)
@TypeAlias(DeviceLogConstant.NETWORK_LOG)
public class NetworkAssessmentLogPo extends Assessment {

    @Id
    private String id;

    @Field(name = "mimic_router", type = FieldType.Object)
    private Assessment mimicRouter;

    @Field(name = "mimic_web", type = FieldType.Object)
    private Assessment mimicWeb;

    @Field(name = "mimic_cloud", type = FieldType.Object)
    private Assessment mimicCloud;

    @Field(name = "mimic_dns", type = FieldType.Object)
    private Assessment mimicDns;

    @Field(name = "mimic_storage", type = FieldType.Object)
    private Assessment mimicStorage;

    @Field(value = "report_time", type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime reportTime;



}
