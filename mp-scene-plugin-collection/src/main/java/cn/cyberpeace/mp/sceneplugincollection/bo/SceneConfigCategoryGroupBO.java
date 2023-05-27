package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SceneConfigCategoryGroupBO {
    private int category;
    private List<SceneConfigElementBO> elements;

    public SceneConfigCategoryGroupBO(int category) {
        this.category = category;
        this.elements = new ArrayList<>();
    }
}
