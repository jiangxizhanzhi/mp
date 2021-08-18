package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtSystemInfo {

    /**
     * ID
     */
    private Integer id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 设备UUID
     */
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
