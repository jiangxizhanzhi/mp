package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImagesCreateInstanceCloudPlatform
 * @Description 镜像构建请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
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
