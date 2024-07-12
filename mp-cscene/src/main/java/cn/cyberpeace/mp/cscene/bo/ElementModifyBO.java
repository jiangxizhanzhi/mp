 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementModifyBO.java.
 // File size 3043.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName ElementModifyBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneBO
 */
@ApiModel(description = "更新节点数据")
@Data
public class ElementModifyBO extends BaseCsceneBO<CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        ElementModifyBO> {
    @ApiModelProperty(value = "更新的内容")
    private SceneDataBO update;
    @ApiModelProperty(value = "场景上下文属性")
    private CsceneContextBO context;

    /**
     * createModel
     *
     * @return
     */
    @Override
    public CsceneInstanceModel createModel() {
        return new CsceneInstanceModel();
    }

    /**
     * fromModel
     *
     * @param obj
     * @param ignoreFields
     */
    @Override
    public void fromModel(CsceneInstanceModel obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneInstanceModel::getScene)));
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

    /**
     * toModel
     *
     * @param ignoreFields
     * @return
     */
    @Override
    public CsceneInstanceModel toModel(String... ignoreFields) {
        CsceneInstanceModel obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(CsceneInstanceModel::getScene)));
        if (update != null && update.getTopology() != null && update.getTopology().getScene() != null) {
            obj.setScene(update.getTopology().getScene().getEid());
        }
        if (context != null && context.getPassThrough() != null) {
            obj.setPassThrough(JsonCommon.stringify(context.getPassThrough()));
        }
        return obj;
    }
}
