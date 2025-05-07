package cn.cyberpeace.mp.cscene.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.request
 * @Author: hhuang
 * @CreateTime: 2025/04/30  09:24
 * @Description: 拓扑修改位置请求
 * @Version: 1.0
 */
@Data
public class PositionChangeTopoRequest {

    /**
     * 场景资源id
     */
    @NotEmpty
    private String topoId;


    private List<AreaChangeData> areaDataList;


    private List<ElementChangeData> elementDataList;
}
