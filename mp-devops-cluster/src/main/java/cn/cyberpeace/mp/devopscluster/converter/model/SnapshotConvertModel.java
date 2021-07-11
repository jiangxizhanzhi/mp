package cn.cyberpeace.mp.devopscluster.converter.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.QuickCreateParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("快照历史信息")
public class SnapshotConvertModel extends BasePageConvertModel{

    
    @ApiModelProperty("UUID")
    private String uuid;

    
    @ApiModelProperty("真实快照名称")
    private String name;

    
    @ApiModelProperty("用户设置的快照名称")
    private String userSnapshotName;

    
    @ApiModelProperty("镜像UUID")
    private String deviceUuid;

    
    @ApiModelProperty("描述")
    private String description;

    
    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;

    
    @ApiModelProperty("父快照名称")
    private String imageName;

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

    @ApiModelProperty("场景源模板id")
    private String csceneId;

    @ApiModelProperty("场景业务实例id")
    private String csceneInstaceId;

    @ApiModelProperty("快速构建参数")
    private List<QuickCreateParam> quickCreateParams;

    public SnapshotConvertModel(String uuid, String name, String userSnapshotName, String deviceUuid, String description, LocalDateTime createdAt) {
        this.uuid = uuid;
        this.name = name;
        this.userSnapshotName = userSnapshotName;
        this.deviceUuid = deviceUuid;
        this.description = description;
        this.createdAt = createdAt;
    }
}
