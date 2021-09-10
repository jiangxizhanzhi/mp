package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtImageInfo {

    private String uuid;

    /**
     * 基础镜像
     */
    private String baseImage;

    /**
     * 是否支持基础镜像
     */
    private Boolean baseImageSupport;

    private String diskFormat;

    private String metaData;

    /**
     * 0：待编辑，1：创建中，2：已创建，3：错误
     */
    private Integer imageStatus;

    private String error;
}
