package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryGroupBO {
    private int category;
    private List<SceneElementBO> elements;

    public CategoryGroupBO(int category) {
        this.category = category;
        this.elements = new ArrayList<>();
    }
}
