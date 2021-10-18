package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.InjectFile;
import lombok.Data;

import java.util.List;

@Data
public class InjectFileCloudPlatformModel extends BaseCloudPlatformModel{

    /**
     * 文件注入列表
     */
    private List<InjectFile> items;

}
