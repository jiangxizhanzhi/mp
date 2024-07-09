package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImageRequest
 * @Description 镜像新增请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopyImageRequest {

    private String name;

    /**
     * UUID
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;

}

