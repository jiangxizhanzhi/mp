package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseSceneAreaNodeConnectionModel extends BaseModel {
    /**
     * 场景
     */
    private String scene;
    /**
     * 源节点id
     */
    private String sourceNodeId;
    /**
     * 目标节点id
     */
    private String targetNodeId;
    /**
     * 源节点
     */
    private String sourceNode;
    /**
     * 目标节点
     */
    private String targetNode;
    /**
     * 连接标识
     */
    @DefaultGen(UuidGenerator.class)
    private String connectionId;
    /**
     * 名称
     */
    @DefaultStr
    private String name;
    /**
     * 显示上下文
     */
    @DefaultStr
    private String displayContext;

    /**
     * 线条类型
     */
    @DefaultStr
    private String type;

    /**
     * 路径点
     */
    @DefaultStr
    private String vertices;

}
