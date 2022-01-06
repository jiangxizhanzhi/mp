package cn.cyberpeace.mp.devops.cluster.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 镜像导出请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesExportRequest extends BaseRequest {


    /**
     * 镜像ID
     */
    private String imageId;


}
