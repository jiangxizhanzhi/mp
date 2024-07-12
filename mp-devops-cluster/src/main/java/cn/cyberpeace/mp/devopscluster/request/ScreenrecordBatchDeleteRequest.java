 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenrecordBatchDeleteRequest.java.
 // File size 709.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName ScreenrecordBatchDeleteRequest
 * @Description 批量删除录屏文件请求
 * @Author qiaokang
 * @Date 2021/9/14 10:50
 * @Version 1.0.0
 */
@Data
@ApiModel("批量删除录屏文件请求")
public class ScreenrecordBatchDeleteRequest {

    /**
     * 录屏文件名称集合
     */
    @ApiModelProperty("录屏文件名称集合")
    @NotNull(message = "recordNames can not be empty")
    private List<String> recordNames;

}
