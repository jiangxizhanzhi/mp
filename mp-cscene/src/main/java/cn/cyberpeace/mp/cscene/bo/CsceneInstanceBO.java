package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApiModel(description = "场景实例数据")
@Data
public class CsceneInstanceBO extends BaseCsceneBO<CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        CsceneInstanceBO> {
    @ApiModelProperty(value = "所属场景模板id")
    private String cscene;
    @ApiModelProperty(value = "场景实例类型")
    private Integer type;
    @ApiModelProperty(value = "场景实例引用类型")
    private Integer referenceType;
    @ApiModelProperty(value = "场景实例引用元数据")
    private String referenceMetaData;
    @ApiModelProperty(value = "场景实例拓扑数据")
    private SceneDataBO scene;
    @ApiModelProperty(value = "场景实例存活时间")
    private Integer sceneTtl;
    @ApiModelProperty(value = "场景实例属性")
    private Integer sceneAttr;
    @ApiModelProperty(value = "场景实例创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "透传字段")
    private Map<String, Object> passThrough;

    @Override
    public CsceneInstanceModel createModel() {
        return new CsceneInstanceModel();
    }

    @Override
    public void fromModel(CsceneInstanceModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneInstanceModel::getScene)));
        passThrough = StringUtils.isEmpty(obj.getPassThrough()) ? new HashMap<>() : JsonCommon.parse(obj.getPassThrough(), new TypeReference<Map<String, Object>>() {
        });
        if (SysContextHolder.getPassThroughMap() == null || SysContextHolder.getPassThroughMap().isEmpty()) {
            if (passThrough != null && !passThrough.isEmpty()) {
                SysContextHolder.setPassThroughMap(passThrough);
            }
        }
    }

    @Override
    public CsceneInstanceModel toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneInstanceModel::getScene)));
        if (scene != null && scene.getTopology() != null && scene.getTopology().getScene() != null) {
            obj.setScene(scene.getTopology().getScene().getEid());
        }
        if (passThrough != null) {
            obj.setPassThrough(JsonCommon.stringify(passThrough));
        }
        return obj;
    }

    public CsceneBO exportToCscene() {
        var csceneBO = export(CsceneBO.class, CsceneRoleBO.class, CsceneInstanceBO::getType,
                CsceneInstanceBO::getCscene, CsceneInstanceBO::getScene);
        if (scene != null) {
            csceneBO.setSceneConfig(scene.exportToSceneConfig());
        }
        return csceneBO;
    }

}
