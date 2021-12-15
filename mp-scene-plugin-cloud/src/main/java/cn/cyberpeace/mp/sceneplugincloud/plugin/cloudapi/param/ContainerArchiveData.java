package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerArchiveData implements CloudHttpData {

    private String dstPath;

    private File file;

    @Override
    public boolean multipart() {
        return true;
    }

}
