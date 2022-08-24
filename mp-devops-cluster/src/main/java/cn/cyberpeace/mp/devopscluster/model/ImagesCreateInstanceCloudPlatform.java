package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 镜像构建请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesCreateInstanceCloudPlatform extends BaseRequest {


    /**
     * uuid
     */
    private String deviceUuid;

    /**
     * 用户名
     */
    private String requestUser;

}
