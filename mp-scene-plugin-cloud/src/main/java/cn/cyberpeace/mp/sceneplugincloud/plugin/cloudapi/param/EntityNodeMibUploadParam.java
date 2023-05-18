package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param
 * @Author: hhuang
 * @CreateTime: 2023/05/17  17:08
 * @Description: mib上传参数
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityNodeMibUploadParam implements CloudHttpParam {

    /**
     * 文件名称
     */
    private String name;
}
