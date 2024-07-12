 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenshotBatchDeleteRequest.java.
 // File size 662.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName ScreenshotBatchDeleteRequest
 * @Description 批量删除截屏请求
 * @Author qiaokang
 * @Date 2021/9/14 10:50
 * @Version 1.0.0
 */
@Data
@ApiModel("批量删除截屏请求")
public class ScreenshotBatchDeleteRequest {

    /**
     * 截屏文件id集合
     */
    @ApiModelProperty("截屏文件id集合")
    @NotNull(message = "ids can not be empty")
    private List<String> ids;

}
