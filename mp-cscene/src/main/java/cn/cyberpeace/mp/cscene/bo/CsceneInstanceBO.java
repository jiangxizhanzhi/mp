package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@ApiModel(description = "综合场景实例数据")
@Data
public class CsceneInstanceBO extends BaseCsceneBO<CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        CsceneInstanceBO> {
    @ApiModelProperty(value = "所属综合场景id")
    private String cscene;
    @ApiModelProperty(value = "综合场景实例类型")
    private Integer type;
    @ApiModelProperty(value = "综合场景实例引用类型")
    private Integer referenceType;
    @ApiModelProperty(value = "基础场景实例")
    private SceneDataBO scene;
    @ApiModelProperty(value = "综合场景实例存活时间")
    private Integer sceneTtl;
    @ApiModelProperty(value = "综合场景实例属性")
    private String sceneAttr;

    @Override
    public CsceneInstanceModel createModel() {
        return new CsceneInstanceModel();
    }

    @Override
    public void fromModel(CsceneInstanceModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneInstanceModel::getScene)));
    }

    @Override
    public CsceneInstanceModel toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneInstanceModel::getScene)));
        if (scene != null) {
            obj.setScene(scene.getTopology().getScene().getEid());
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
