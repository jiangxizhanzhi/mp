package cn.cyberpeace.mp.cscene.bo.base;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.base.util.SerializableFunction;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public abstract class BaseCsceneRoleBO<M extends BaseCsceneRoleModel, T extends BaseCsceneRoleBO<M, T>>
        extends ModelBO<M, T> {
    @ApiModelProperty(value = "角色id")
    private String id;
    @ApiModelProperty(value = "角色key")
    private String key;
    @ApiModelProperty(value = "角色名称")
    private String name;
    @ApiModelProperty(value = "角色类型")
    private String type;
    @ApiModelProperty(value = "资产节点id列表")
    private List<String> assetNodes;
    @ApiModelProperty(value = "可见节点id列表")
    private List<String> visibleNodes;
    @ApiModelProperty(value = "包含用户id列表")
    private List<String> users;
    @ApiModelProperty(value = "子角色信息")
    private List<T> children;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getAssetNodes),
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getVisibleNodes),
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getUsers)));
        assetNodes = StringUtils.isEmpty(obj.getAssetNodes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getAssetNodes(), new TypeReference<List<String>>(){});
        visibleNodes = StringUtils.isEmpty(obj.getVisibleNodes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getVisibleNodes(), new TypeReference<List<String>>(){});
        users = StringUtils.isEmpty(obj.getUsers()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getUsers(), new TypeReference<List<String>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getAssetNodes),
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getVisibleNodes),
                BeanUtils.getBeanGetterFieldName(BaseCsceneRoleModel::getUsers)));
        if (assetNodes != null) {
            obj.setAssetNodes(JsonUtils.toJsonString(assetNodes));
        }
        if (visibleNodes != null) {
            obj.setVisibleNodes(JsonUtils.toJsonString(visibleNodes));
        }
        if (users != null) {
            obj.setUsers(JsonUtils.toJsonString(users));
        }
        return obj;
    }

    @Override
    public T export() {
        setId(null);
        if (children != null) {
            setChildren(children.stream().map(BaseCsceneRoleBO::export).collect(Collectors.toList()));
        }
        return (T) this;
    }

    @Override
    public <P1> P1 export(Class<P1> pClass, String... ignoreFields) {
        return super.export(pClass, ArrayUtils.addAll(ignoreFields, "children"));
    }

    @SafeVarargs
    public final <P extends BaseCsceneRoleBO<?, P>> P exportIgnore2(Class<P> pClass,
                                                                    SerializableFunction<T>... ignoreFieldGetters) {
        var pObj = super.exportIgnore(pClass, ignoreFieldGetters);
        if (children != null) {
            pObj.setChildren(children.stream().map(child -> child.exportIgnore2(pClass, ignoreFieldGetters))
                    .collect(Collectors.toList()));
        }
        return pObj;
    }
}
