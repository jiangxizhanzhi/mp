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

import java.util.HashMap;
import java.util.Map;

@ApiModel(description = "更新节点数据")
@Data
public class ElementModifyBO extends BaseCsceneBO<CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        ElementModifyBO> {
    @ApiModelProperty(value = "更新的内容")
    private SceneDataBO updateElement;
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
        if (updateElement != null && updateElement.getTopology() != null && updateElement.getTopology().getScene() != null) {
            obj.setScene(updateElement.getTopology().getScene().getEid());
        }
        if (passThrough != null) {
            obj.setPassThrough(JsonCommon.stringify(passThrough));
        }
        return obj;
    }
}
