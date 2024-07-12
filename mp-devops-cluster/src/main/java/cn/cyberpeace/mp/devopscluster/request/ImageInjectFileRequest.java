 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageInjectFileRequest.java.
 // File size 1012.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName ImageInjectFileRequest
 * @Description 镜像文件注入请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageInjectFileRequest extends BaseRequest {

    @NotBlank
    @ApiModelProperty(value = "设备ID")
    private String deviceUuid;

    @NotBlank
    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    @NotBlank
    @ApiModelProperty(value = "文件名")
    private String fileName;

    @NotNull
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;
}
