package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param
 * @Author: hhuang
 * @CreateTime: 2023/05/10  10:36
 * @Description: mib上传数据
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityNodeMibUploadData implements CloudHttpData {

    private File file;

    @Override
    public boolean multipart() {
        return true;
    }
}
