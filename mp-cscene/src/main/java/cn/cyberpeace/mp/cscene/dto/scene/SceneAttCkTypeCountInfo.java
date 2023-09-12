package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.sceneplugincollection.bo.AttCkTypeCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneAttCkTypeCountInfo {
    private Long totalTimes;
    private List<AttCkTypeCount> list;
}
