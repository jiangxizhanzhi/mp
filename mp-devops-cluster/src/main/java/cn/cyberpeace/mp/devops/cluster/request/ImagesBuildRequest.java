package cn.cyberpeace.mp.devops.cluster.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 镜像构建请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesBuildRequest extends BaseRequest {


    /**
     * 名称
     */
    private String name;

    /**
     * 镜像名称
     */
    private String imageName;

    /**
     * 服务ID集合
     */
    private List<String> serviceIds;


}
