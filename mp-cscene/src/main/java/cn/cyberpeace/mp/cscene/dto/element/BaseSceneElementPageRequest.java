package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.cscene.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/2 20:11
 */
@Data
public class BaseSceneElementPageRequest extends PageRequest {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点id")
    private List<String> elementIds;

    @ApiModelProperty(value = "场景关闭情况下是否仍然查询数据，默认false，表示不查询")
    private Boolean fetchIfSceneInstanceShutdown = false;

}
