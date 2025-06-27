package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.bo.base
 * @Author: hhuang
 * @CreateTime: 2025/03/19  14:54
 * @Description: vlan对应端口信息
 * @Version: 1.0
 */
@Data
@ApiModel("vlan对应端口信息")
public class VlanPortBO {

    private String id;

    private String name;
}
