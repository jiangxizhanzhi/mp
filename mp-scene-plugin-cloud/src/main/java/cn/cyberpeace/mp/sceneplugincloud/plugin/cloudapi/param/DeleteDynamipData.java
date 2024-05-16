package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteDynamipData implements HttpData {
    private String id;
}

