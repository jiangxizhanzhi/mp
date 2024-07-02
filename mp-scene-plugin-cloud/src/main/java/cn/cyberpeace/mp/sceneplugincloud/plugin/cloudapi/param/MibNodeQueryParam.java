package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName MibNodeQueryParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MibNodeQueryParam implements CloudHttpParam {

    private String oid;
    //snmp版本
    private Integer version;
    //团体字，一般为public
    private String communityName;
    //ip地址
    private String ip;
    //端口
    private Integer port;
    /**
     * snmp版本为v3时，会出现以下三个参数
     *  用户名
     */
    private String username;
    private String authKey;
    private String privKey;

}
