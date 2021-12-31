package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SNMP参数信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpParams {

    private Integer version;

    private String ip;

    private String countOid;

    private String oid;

    private Object auth;

    private String dataType;
}
