package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName DeviceReferCsceneStat
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class DeviceReferCsceneStat {
    @ApiModelProperty(value = "靶标uuid")
    private String referenceImageUuid;
    @ApiModelProperty(value = "靶标名称")
    private String referenceImage;
    @ApiModelProperty(value = "场景id")
    private String csceneId;
    @ApiModelProperty(value = "场景名称")
    private String name;
    @ApiModelProperty(value = "创建用户id")
    private String createUser;
    @ApiModelProperty(value = "创建用户名")
    private String createUsername;

    private String sceneId;
    private String elementId;
}
