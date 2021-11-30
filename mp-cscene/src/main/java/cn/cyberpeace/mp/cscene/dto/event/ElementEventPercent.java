package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 16:40
 */
@Data
public class ElementEventPercent {

    @ApiModelProperty("节点ID")
    private String elementId;

    @ApiModelProperty("节点名称")
    private String elementName;

    @ApiModelProperty("事件占比")
    private Double percent;

}
