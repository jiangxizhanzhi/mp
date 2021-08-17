package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtGateway {

    private Integer port_configs;

    private String port_name;

    private String port_ips;
}
