package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaNodeConnectionModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基础场景节点连接bo
 *
 * @author 何韦
 * @date 2023/05/05
 */
@Data
public abstract class BaseSceneAreaNodeConnectionBO<M extends BaseSceneAreaNodeConnectionModel,
        T extends BaseSceneAreaNodeConnectionBO<M, T>> extends EidBO<M, T> {
    /**
     * 连接id
     */
    @ApiModelProperty(value = "连接id")
    private String id;
    /**
     * 源节点id
     */
    @ApiModelProperty(value = "源节点id")
    private String source;
    /**
     * 目标节点id
     */
    @ApiModelProperty(value = "目标节点id")
    private String target;
    /**
     * 连接名称
     */
    @ApiModelProperty(value = "连接名称")
    private String name;
    /**
     * 显示参数
     */
    @ApiModelProperty(value = "显示参数")
    private String displayContext;

    /**
     * 从模型加载
     *
     * @param obj          obj
     * @param ignoreFields 忽略字段
     * @return {@link T}
     */
    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        setId(obj.getConnectionId());
        setSource(obj.getSourceNodeId());
        setTarget(obj.getTargetNodeId());
    }

    /**
     * 转化为模型
     *
     * @param ignoreFields 忽略字段
     * @return {@link M}
     */
    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        obj.setConnectionId(id);
        obj.setSourceNodeId(source);
        obj.setTargetNodeId(target);
        return obj;
    }

    public String getSourceNodeId() {
        return parseNodeId(source);
    }

    public String getTargetNodeId() {
        return parseNodeId(target);
    }

    private String parseNodeId(String input){
        String regex = "port_(.+)_layer_[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String extracted = matcher.group(1);
            return extracted;
        }
        return input;
    }
}
