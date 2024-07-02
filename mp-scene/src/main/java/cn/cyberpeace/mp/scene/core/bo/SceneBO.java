package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.event.SceneStageRecorder;
import cn.cyberpeace.mp.scene.core.event.base.BaseSceneEventModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SceneBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneBO
 * @see BaseSceneEventModel
 */
@Data
public class SceneBO extends BaseSceneBO<SceneModel, SceneBO> implements BaseSceneEventModel {
    @ApiModelProperty(value = "事件")
    private Integer event;
    @ApiModelProperty(value = "事件状态")
    private Integer eventStatus;
    @ApiModelProperty(value = "事件进程")
    private String process;
    @ApiModelProperty(value = "事件进程状态")
    private Integer processStatus;
    @ApiModelProperty(value = "事件进程描述信息")
    private String processMessage;
    @ApiModelProperty(value = "事件进度百分比")
    private Integer progressPercentage;
    @ApiModelProperty(value = "创建消耗时间(秒)")
    private Long createConsumeSeconds;
    @ApiModelProperty(value = "当前场景的阶段")
    private String stage;
    @ApiModelProperty(value = "当前场景的阶段名称")
    private String stageName;
    @ApiModelProperty(value = "当前阶段执行的百分比")
    private Integer stagePercentage;
    @ApiModelProperty(value = "阶段列表")
    private List<SceneStageRecorder.SceneStageRecord> stageList;

    @Override
    public SceneModel createModel() {
        return new SceneModel();
    }

    @Override
    public String genTarget() {
        return getEid();
    }

    @Override
    public void fromModel(SceneModel obj, String... ignoreFields) {
        super.fromModel(obj,
                ArrayUtils.addAll(ignoreFields, BeanUtils.getBeanGetterFieldName(SceneModel::getStageList))
        );
        if (stageList == null) {
            stageList = StringUtils.isEmpty(obj.getStageList()) ? new ArrayList<>()
                    : JsonCommon.parse(obj.getStageList(), new TypeReference<List<SceneStageRecorder.SceneStageRecord>>() {
            });
        }
    }

    @Override
    public SceneModel toModel(String... ignoreFields) {
        SceneModel obj = super.toModel(
                ArrayUtils.addAll(ignoreFields, BeanUtils.getBeanGetterFieldName(SceneModel::getStageList)
                ));
        if (stageList != null) {
            obj.setStageList(JsonCommon.stringify(stageList));
        }
        return obj;
    }
}
