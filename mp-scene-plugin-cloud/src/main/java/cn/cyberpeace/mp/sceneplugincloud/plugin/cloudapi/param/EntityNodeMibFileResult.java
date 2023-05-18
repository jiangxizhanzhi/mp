package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param
 * @Author: hhuang
 * @CreateTime: 2023/05/15  15:31
 * @Description: 实装snmp mib文件信息解析响应
 * @Version: 1.0
 */
@Data
public class EntityNodeMibFileResult {

    private String name;
    private String oid;
    private String syntax;
    private String maxAccess;
    private String status;
    private String parent;

}
