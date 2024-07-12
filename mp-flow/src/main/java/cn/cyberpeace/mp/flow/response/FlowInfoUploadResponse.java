 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowInfoUploadResponse.java.
 // File size 1207.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowInfoUploadResponse
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量上传返回信息")
public class FlowInfoUploadResponse {

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型")
    private String fileType;

    /**
     * 文件url
     */
    @ApiModelProperty(value = "文件url")
    private String fileUrl;

    /**
     * 第三方id
     */
    @ApiModelProperty(value = "第三方id")
    private String thirdId;

    /**
     * 第三方文件名称
     */
    @ApiModelProperty(value = "第三方文件名称")
    private String thirdName;

}
