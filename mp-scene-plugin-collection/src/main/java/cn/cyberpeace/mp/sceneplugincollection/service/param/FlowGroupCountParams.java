package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/24
 */
@Setter
@Getter
public class FlowGroupCountParams {
    private Integer topSize;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String queryType;
    private List<String> queryAddress;

    public FlowGroupCountParams(String queryType) {
        this.queryType = queryType;
    }
}
