package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExtImageInfo {

    private String base_image;

    private Boolean base_image_support;

    private String disk_format;

    private String meta_data;

    private Integer image_status;
}
