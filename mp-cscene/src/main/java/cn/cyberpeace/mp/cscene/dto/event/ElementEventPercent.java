package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    @ApiModelProperty("事件数量")
    private Long sum;

    @ApiModelProperty("事件类型 0:所有 1:进程 2:端口 3:网络连接 4:文件 5:用户")
    private Integer type;

}
