package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@ApiModel("录屏结果")
public class ScreenrecordVO extends BaseModel {

    
    @ApiModelProperty(value = "录屏文件地址")
    @DefaultStr
    private String file;

    
    @ApiModelProperty(value = "录屏文件名称")
    @DefaultStr
    private String fileName;

    
    @ApiModelProperty(value = "录屏文件大小，单位:字节(B)")
    private Long fileSize;

    
    @ApiModelProperty(value = "场景名称")
    @DefaultStr
    private String sceneName;

    
    @ApiModelProperty(value = "用户id")
    @DefaultStr
    private String userId;

    
    @ApiModelProperty(value = "用户名称")
    @DefaultStr
    private String userName;

    @ApiModelProperty(value = "用户头像")
    @DefaultStr
    private String userAvatar;

    
    @ApiModelProperty(value = "组织名称")
    @DefaultStr
    private String userOrgName;

    
    @ApiModelProperty(value = "场景id")
    @DefaultStr
    private String scene;

    
    @ApiModelProperty(value = "节点id")
    @DefaultStr
    private String nodeId;

    
    @ApiModelProperty(value = "录屏时长")
    private Double recortime;


    @ApiModelProperty(value = "录屏结束时间")
    private LocalDateTime endTime;
    
    @ApiModelProperty(value = "节点名称")
    private String host;

    @ApiModelProperty(value = "录屏播放地址")
    private String playUrl;

    
    public void parseResult(ScreenrecordPageVO.Record record) {
        this.setFileName(record.getRecordingFile());
        this.setFileSize(record.getFileSize());
        this.setSceneName(record.getScene());
        
        String org = record.getDepartment();
        if (StringUtils.isNotBlank(org) && org.contains("|")) {
            
            int start = org.indexOf("|");
            String userName = org.substring(0, start);
            String orgName = org.substring(start + 1);
            this.setUserName(userName);
            this.setUserOrgName(orgName);
        } else {
            this.setUserName(record.getUsername());
            this.setUserOrgName(org);
        }
        this.setPlayUrl(CloudSingleton.cloudAppConfig.getCpcloudServer() + String.format(CloudUrlConstant.RECORDING_PLAY_URL, this.getFileName()));
        this.setUserId(record.getUsername());
        this.setRecortime(record.getRecortime());
        this.setCreateTime(TimeUtils.utcToLocalDateTime(record.getRecordate()));
        this.setEndTime(this.getCreateTime().plusSeconds(this.getRecortime().intValue()));
        this.setHost(record.getHost());
        this.setNodeId(record.getHostId());
    }

}
