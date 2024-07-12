 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhFileEventResponse.java.
 // File size 605.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 事件查询返回结果
 * @Author qiaokang
 * @Date：2021/6/22 17:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WazuhFileEventResponse {

    @ApiModelProperty("事件集合")
    private List<FileEventModel> fileEventModelList;

    @ApiModelProperty("事件总数")
    private int total;

}
