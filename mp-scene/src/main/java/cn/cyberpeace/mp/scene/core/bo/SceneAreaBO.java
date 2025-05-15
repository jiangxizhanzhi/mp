package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class SceneAreaBO extends BaseSceneAreaBO<SceneAreaModel, SceneAreaBO> {

    @Override
    public SceneAreaModel createModel() {
        return new SceneAreaModel();
    }

    @Override
    public <E extends BaseSceneElementBO<?, ?, ?>> E convertSceneAreaToElement(List<BaseSceneAreaNodeConnectionBO> areaNodeConnectionBOS) {
        SceneElementBO sceneElementBO = new SceneElementBO();
        sceneElementBO.setId(this.getId());
        sceneElementBO.setArea(this.getArea());
        sceneElementBO.setName(this.getName());
        sceneElementBO.setEid(this.getEid());
        sceneElementBO.setIcon2(this.getIcon2());
        sceneElementBO.setIcon3(this.getIcon3());
        sceneElementBO.setOuterPosition2(this.getOuterPosition2());
        sceneElementBO.setType(BaseSceneElementModel.Type.LAYER);
        sceneElementBO.setPosition2(this.getPosition2());

        sceneElementBO.setPosition3(this.getPosition3());
        sceneElementBO.setDescription(this.getDescription());
        sceneElementBO.setDisplayContext(this.getDisplayContext());

        List<SceneElementPortBO> ports = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(areaNodeConnectionBOS)) {
            areaNodeConnectionBOS.forEach(nodeConnection -> {
                if (nodeConnection.getSourceNodeId().equals(sceneElementBO.getId())) {
                    SceneElementPortBO sceneElementPortBO = new SceneElementPortBO();
                    sceneElementPortBO.setElement(sceneElementBO);
                    sceneElementPortBO.setEid(TextUtils.uuid());
                    sceneElementPortBO.setId(nodeConnection.getSource());
                    sceneElementPortBO.setName(nodeConnection.getSourceNodeConnectPortName());
                    ports.add(sceneElementPortBO);
                } else if (nodeConnection.getTargetNodeId().equals(sceneElementBO.getId())) {
                    SceneElementPortBO sceneElementPortBO = new SceneElementPortBO();
                    sceneElementPortBO.setElement(sceneElementBO);
                    sceneElementPortBO.setEid(TextUtils.uuid());
                    sceneElementPortBO.setId(nodeConnection.getTarget());
                    sceneElementPortBO.setName(nodeConnection.getTargetNodeConnectPortName());
                    ports.add(sceneElementPortBO);
                }
            });
        }
        sceneElementBO.setPorts(ports);
        return (E) sceneElementBO;
    }
}
