 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BatchDeleteFlowTaskRequest.java.
 // File size 758.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName BatchDeleteFlowTaskRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "批量删除流量任务请求")
public class BatchDeleteFlowTaskRequest {

    /**
     * 流量任务id集合
     */
    @NotEmpty(message = "idList can not be empty")
    @ApiModelProperty("流量任务id集合")
    private List<String> idList;

}
