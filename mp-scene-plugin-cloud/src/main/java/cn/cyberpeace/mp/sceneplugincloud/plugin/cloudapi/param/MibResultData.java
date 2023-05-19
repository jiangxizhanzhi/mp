package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param
 * @Author: hhuang
 * @CreateTime: 2023/05/12  17:49
 * @Description: mib节点结果
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MibResultData {

    private String oid;
    private String value;
    private String type;
    private String address;

}
