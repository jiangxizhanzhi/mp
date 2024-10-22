package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import lombok.Data;


@Data
public class AssetManagementPageRequest extends PageRequest {

    //场景id
    private String sceneId;

    private String name;
}
