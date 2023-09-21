package cn.cyberpeace.mp.collection.model.log;

import cn.cyberpeace.mp.collection.constant.DeviceLogConstant;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


/**
 * 普通日志数据，只包含原始文本
 */
@Data
@Document(indexName = DeviceLogConstant.COMMON_INDEX)
public class CommonDeviceLog extends DeviceLog{
    /**
     * 原始日志文本
     */
    @Field(type = FieldType.Text)
    private Object rawData;
}
