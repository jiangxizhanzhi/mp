package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.SnmpConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpParams {

    private Integer version;

    private String ip;

    private String countOid;

    private String oid;

    private SnmpConfig auth;

    private String dataType;
}
