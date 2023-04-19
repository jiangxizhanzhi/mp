package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param
 * @Author: hhuang
 * @CreateTime: 2023/04/06  16:45
 * @Description: 调用云平台删除连接请求参数
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuacamoleDeleteConnectionUrlData implements CloudHttpData {
    private String connectionId;
}
