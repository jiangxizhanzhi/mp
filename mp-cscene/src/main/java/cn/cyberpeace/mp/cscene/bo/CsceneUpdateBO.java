package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@ApiModel(description = "场景模板数据")
@Data
public class CsceneUpdateBO extends BaseCsceneBO<CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO, CsceneUpdateBO> {
    @ApiModelProperty(value = "场景实例id")
    private String csceneInstanceId;
    @ApiModelProperty(value = "场景配置")
    private SceneDataBO updateScene;
    @ApiModelProperty(value = "场景上下文属性")
    private CsceneContextBO context;

    @Override
    public CsceneInstanceModel createModel() {
        return new CsceneInstanceModel();
    }

    @Override
    public void fromModel(CsceneInstanceModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneModel::getSceneConfig)));
        context = new CsceneContextBO();
        context.setPassThrough(StringUtils.isEmpty(obj.getPassThrough()) ? new HashMap<>() :
                JsonCommon.parse(obj.getPassThrough(), new TypeReference<Map<String, Object>>() {
                }));
        if (SysContextHolder.getPassThroughMap() == null || SysContextHolder.getPassThroughMap().isEmpty()) {
            if (context.getPassThrough() != null && !context.getPassThrough().isEmpty()) {
                SysContextHolder.setPassThroughMap(context.getPassThrough());
            }
        }
    }

    @Override
    public CsceneInstanceModel toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneModel::getSceneConfig)));
        if (updateScene != null && updateScene.getTopology() != null && updateScene.getTopology().getScene() != null) {
            obj.setScene(updateScene.getTopology().getScene().getEid());
        }
        return obj;
    }

    @Override
    public CsceneUpdateBO export() {
        var csceneBO = super.export();
        if (updateScene != null) {
            csceneBO.setUpdateScene(updateScene.export());
        }
        return this;
    }

    public CsceneInstanceBO exportToCsceneInstance() {
        var csceneInstance = export(CsceneInstanceBO.class, CsceneInstaneRoleBO.class, CsceneUpdateBO::getUpdateScene);
        if (updateScene != null) {
            csceneInstance.setScene(updateScene.export());
        }
        return csceneInstance;
    }

}
