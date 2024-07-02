package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName DeleteDynamipData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see HttpData
 */
@Data
@Builder
public class DeleteDynamipData implements HttpData {
    private String id;
}

