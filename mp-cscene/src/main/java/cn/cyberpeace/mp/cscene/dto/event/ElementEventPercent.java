package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 16:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementEventPercent {

    @ApiModelProperty("节点ID")
    private String elementId;

    @ApiModelProperty("节点名称")
    private String elementName;

    @ApiModelProperty("事件占比")
    private Double percent;

}
