package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 19:52
 */
@Data
public class BaseSceneElementRequest {

    @ApiModelProperty("场景id,多个id用逗号分隔")
    @NotNull
    private String sceneId;

    @ApiModelProperty("节点id")
    private List<String> elementIds;

    @ApiModelProperty("用于查询的ip列表")
    private List<String> ips;

}
