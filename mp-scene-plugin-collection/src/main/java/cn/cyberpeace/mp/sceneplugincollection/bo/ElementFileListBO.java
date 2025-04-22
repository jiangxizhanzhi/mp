package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.sceneplugincollection.model.ElementFileListModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementFileListBO extends ElementFileListModel {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("文件路径")
    private String path;

    @ApiModelProperty("事件描述")
    private String eventDesc;

    @ApiModelProperty("级别")
    private String levelName;

    @ApiModelProperty("级别编码")
    private Integer level;

    @ApiModelProperty("文件大小")
    private String fileSize;

    @ApiModelProperty("group")
    private String group;

    @ApiModelProperty("变更前md5值")
    private String md5Before;

    @ApiModelProperty("变更后md5值")
    private String md5After;
}
