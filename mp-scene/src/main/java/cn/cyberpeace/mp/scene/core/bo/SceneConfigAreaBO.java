package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class SceneConfigAreaBO extends BaseSceneAreaBO<SceneConfigAreaModel, SceneConfigAreaBO> {

    @Override
    public SceneConfigAreaModel createModel() {
        return new SceneConfigAreaModel();
    }

    @Override
    public <E extends BaseSceneElementBO<?, ?, ?>> E convertSceneAreaToElement(List<BaseSceneAreaNodeConnectionBO> areaNodeConnectionBOS) {
        SceneConfigElementBO sceneConfigElementBO = new SceneConfigElementBO();
        sceneConfigElementBO.setId(this.getId());
        sceneConfigElementBO.setArea(this.getArea());
        sceneConfigElementBO.setName(this.getName());
        sceneConfigElementBO.setEid(this.getEid());
        sceneConfigElementBO.setIcon2(this.getIcon2());
        sceneConfigElementBO.setIcon3(this.getIcon3());
        sceneConfigElementBO.setOuterPosition2(this.getOuterPosition2());
        sceneConfigElementBO.setType(BaseSceneElementModel.Type.LAYER);
        sceneConfigElementBO.setPosition2(this.getPosition2());

        sceneConfigElementBO.setPosition3(this.getPosition3());
        sceneConfigElementBO.setDescription(this.getDescription());
        sceneConfigElementBO.setDisplayContext(this.getDisplayContext());

        List<SceneConfigElementPortBO> ports = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(areaNodeConnectionBOS)) {
            areaNodeConnectionBOS.forEach(nodeConnection -> {
                if (nodeConnection.getSourceNodeId().equals(sceneConfigElementBO.getId())) {
                    SceneConfigElementPortBO sceneConfigElementPortBO = new SceneConfigElementPortBO();
                    sceneConfigElementPortBO.setElement(sceneConfigElementBO);
                    sceneConfigElementPortBO.setEid(TextUtils.uuid());
                    sceneConfigElementPortBO.setId(nodeConnection.getSource());
                    sceneConfigElementPortBO.setName(nodeConnection.getSourceNodeConnectPortName());
                    ports.add(sceneConfigElementPortBO);
                } else if (nodeConnection.getTargetNodeId().equals(sceneConfigElementBO.getId())) {
                    SceneConfigElementPortBO sceneConfigElementPortBO = new SceneConfigElementPortBO();
                    sceneConfigElementPortBO.setElement(sceneConfigElementBO);
                    sceneConfigElementPortBO.setEid(TextUtils.uuid());
                    sceneConfigElementPortBO.setId(nodeConnection.getTarget());
                    sceneConfigElementPortBO.setName(nodeConnection.getTargetNodeConnectPortName());
                    ports.add(sceneConfigElementPortBO);
                }
            });
        }
        sceneConfigElementBO.setPorts(ports);
        return (E) sceneConfigElementBO;
    }
}
