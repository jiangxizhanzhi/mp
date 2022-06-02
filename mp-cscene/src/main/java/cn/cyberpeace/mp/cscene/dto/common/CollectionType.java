package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 17:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CollectionType {

    public static final Integer TYPE_CHANGE_EVENT_FILE = 0;
    public static final Integer TYPE_CHANGE_EVENT_REGISTRY = 1;
    public static final Integer TYPE_LOG_FILE = 2;
    public static final Integer TYPE_LOG_EVENT = 3;
    public static final Integer TYPE_CUSTOM_COLLECT = 4;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "0:文件变更 1:注册表变更 2:原始日志 3:日志事件 4:自定义采集")
    private Integer type;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "子类别")
    private List<CollectionType> children;

}
