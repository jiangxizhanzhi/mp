 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetUploadVO.java.
 // File size 690.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName TargetUploadVO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetUploadVO {

    /**
     * 临时文件UUID
     */
    @ApiModelProperty("临时文件UUID")
    private String tmpdirUuid;

    /**
     * 上传云平台生成的文件名
     */
    @ApiModelProperty("上传云平台生成的文件名")
    private List<String> fileNames;
}
