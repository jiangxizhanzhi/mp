package cn.cyberpeace.mp.cscene.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneTypeVO {
    private String sceneInstanceId;
    private String sceneId;
    private Integer cloudType;
}
