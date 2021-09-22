package cn.cyberpeace.mp.devops.cluster.model;

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

    private Object auth;

    private String dataType;
}
