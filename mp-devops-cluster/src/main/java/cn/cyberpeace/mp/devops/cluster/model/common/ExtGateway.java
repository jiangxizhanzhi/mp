package cn.cyberpeace.mp.devops.cluster.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtGateway {

    @JsonIgnore
    private Integer id;

    @JsonIgnore
    private String uuid;

    @JsonIgnore
    private String createdAt;

    @JsonIgnore
    private String updatedAt;

    @JsonIgnore
    private Boolean deleted;

    @JsonIgnore
    private String deletedAt;

    private String user;

    private String project;

    @JsonIgnore
    private String deviceUuid;

    private Integer portConfigs;

    private String portName;

    private String portIps;
}
