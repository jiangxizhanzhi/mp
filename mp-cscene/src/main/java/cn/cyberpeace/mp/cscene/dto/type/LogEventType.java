package cn.cyberpeace.mp.cscene.dto.type;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 16:11
 */
@Data
@Builder
@ApiModel("日志事件类别")
public class LogEventType {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "子类别")
    private List<LogEventType> subTypes;

}
