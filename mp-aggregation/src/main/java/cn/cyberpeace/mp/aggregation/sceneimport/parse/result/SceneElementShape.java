package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName SceneElementShape
 * @Description
 * @Author maojun
 * @Date 2022/8/29
 * @Version 1.0.0
 */
@Getter
@Setter
public class SceneElementShape {
    private String id;
    private String name;
    private String position;
    private Double length;
    private Double width;
    private List<String> nodeIds;
    private String displayContext;
}
