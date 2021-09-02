package cn.cyberpeace.mp.devops.cluster.model.common;

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
     * 操作系统
     */
    private Integer systemType;

    /**
     * 系统架构
     */
    private String systemArchitecture;

    /**
     * 语言
     */
    private String language;
}
