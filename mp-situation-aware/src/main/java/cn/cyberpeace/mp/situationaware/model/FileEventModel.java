package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    @ApiModelProperty("变更前md5值")
    private String md5Before;

    @ApiModelProperty("变更后md5值")
    private String md5After;

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

    @ApiModelProperty("rule等级")
    private Integer level;

    @ApiModelProperty("分类")
    private String catagory;

    @ApiModelProperty("rule描述信息")
    private String description;

    @ApiModelProperty("从_source里面抽取的信息，JSONObject")
    private JSONObject content;

    @ApiModelProperty("滚动分页的scroll_id")
    private String scrollId;

}
