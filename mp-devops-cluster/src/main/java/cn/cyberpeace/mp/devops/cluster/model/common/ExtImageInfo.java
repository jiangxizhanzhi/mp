package cn.cyberpeace.mp.devops.cluster.model.common;

import cn.cyberpeace.mp.devops.cluster.enums.DiskFormatEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtImageInfo {

    private String baseImage;

    private Boolean baseImageSupport;

    private DiskFormatEnum diskFormat;

    private String metaData;

    private Integer imageStatus;

    private String error;
}
