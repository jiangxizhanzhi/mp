package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
public class FileEventModel {

    /**
     * 文件路径
     */
    @ApiModelProperty("文件路径")
    private String filePath;

    //文件大小
    @ApiModelProperty("文件大小")
    private String size;

    //文件的时间
    @ApiModelProperty("文件时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("用户")
    private String user;

    /**
     * 文件事件类型
     */
    @ApiModelProperty("变更描述")
    private String action;

    /**
     * 文件事件类型
     */
    @ApiModelProperty("文件事件类型")
    private FileEventType fileEventType;

    @ApiModelProperty("macId集合")
    private List<String> macIds;

    @ApiModelProperty("wazuhAgentId")
    private String agentId;

    @ApiModelProperty("wazuh AgentName")
    private String agentName;

}
