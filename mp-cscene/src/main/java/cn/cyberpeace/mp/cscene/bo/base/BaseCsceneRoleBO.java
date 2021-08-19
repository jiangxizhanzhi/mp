package cn.cyberpeace.mp.cscene.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseCsceneRoleBO<M extends BaseCsceneRoleModel> extends ModelBO<M> {
    private String parent;
    private String name;
    private List<String> assetNodes;
    private List<String> visibleNodes;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getAssetNodes),
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getVisibleNodes)));
        assetNodes = StringUtils.isEmpty(obj.getAssetNodes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAssetNodes(), new TypeReference<List<String>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getAssetNodes),
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getVisibleNodes)));
        if (assetNodes != null) {
            obj.setAssetNodes(JsonUtils.toJsonString(assetNodes));
        }
        if (visibleNodes != null) {
            obj.setVisibleNodes(JsonUtils.toJsonString(visibleNodes));
        }
        return obj;
    }
}
