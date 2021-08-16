package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemInfo {

    /**
     * UUID
     */
    private String uuid;


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
