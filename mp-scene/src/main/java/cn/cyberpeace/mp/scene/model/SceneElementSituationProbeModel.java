package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_scene_element_situation_probe")
public class SceneElementSituationProbeModel extends BaseModel {
    @ApiModelProperty(value = "场景实例id")
    private String scene;
    @ApiModelProperty(value = "节点id")
    private String sceneElement;
    @ApiModelProperty(value = "探针id")
    private String probeId;
    @ApiModelProperty(value = "探针状态 0：未安装 1：运行 2：停止")
    private Integer status;
}
