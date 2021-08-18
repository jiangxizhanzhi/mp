package cn.cyberpeace.mp.devops.cluster.converter.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtGateway {

    private Integer portConfigs;

    private String portName;

    private String portIps;
}
