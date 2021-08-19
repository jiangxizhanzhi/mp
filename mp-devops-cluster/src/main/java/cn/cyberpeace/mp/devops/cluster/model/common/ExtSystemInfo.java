package cn.cyberpeace.mp.devops.cluster.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtSystemInfo {

    /**
     * ID
     */
    @JsonIgnore
    private Integer id;

    /**
     * UUID
     */
    @JsonIgnore
    private String uuid;

    /**
     * 设备UUID
     */
    @JsonIgnore
    private String deviceUuid;

    /**
     * 操作系统
     */
    private String systemType;

    /**
     * 系统架构
     */
    private String systemArchitecture;

    /**
     * 语言
     */
    private String language;
}
