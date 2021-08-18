package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtGateway {

    private Integer id;

    private String uuid;

    private String created_at;

    private String updated_at;

    private Boolean deleted;

    private String deleted_at;

    private String user;

    private String project;

    private String device_uuid;

    private Integer port_configs;

    private String port_name;

    private String port_ips;
}
