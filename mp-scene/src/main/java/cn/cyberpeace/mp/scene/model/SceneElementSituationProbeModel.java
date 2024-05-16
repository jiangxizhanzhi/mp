package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @ApiModelProperty(value = "探针状态 0：未安装 1：运行 2：停止 3：更新中")
    private Integer status;
    @JsonIgnore
    @ApiModelProperty(value = "删除标记")
    private Boolean deleted;

    //创建枚举类 0：未安装 1：运行 2：停止 3：更新中
    public enum ProbeStatus {
        NOT_INSTALL(0),
        RUNNING(1),
        STOPPED(2),
        UPDATING(3);
        private final int value;

        ProbeStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
