package cn.cyberpeace.mp.cscene.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 17:02
 */
@Data
@Builder
public class LogEventType {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "子类别")
    private List<LogEventType> subTypes;

}
