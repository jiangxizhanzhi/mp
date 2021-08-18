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

    private String createdAt;

    private String updatedAt;

    private Boolean deleted;

    private String deletedAt;

    private String user;

    private String project;

    private String deviceUuid;

    private Integer portConfigs;

    private String portName;

    private String portIps;
}
