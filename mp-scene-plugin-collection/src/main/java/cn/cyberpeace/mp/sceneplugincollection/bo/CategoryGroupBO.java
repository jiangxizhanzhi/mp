package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CategoryGroupBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CategoryGroupBO {
    private int category;
    private List<SceneElementBO> elements;

    public CategoryGroupBO(int category) {
        this.category = category;
        this.elements = new ArrayList<>();
    }
}
