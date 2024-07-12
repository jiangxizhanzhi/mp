 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowCollectionResponse.java.
 // File size 1206.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName FlowCollectionResponse
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see FlowCollectionModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量集合返回信息")
public class FlowCollectionResponse extends FlowCollectionModel {

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量，3 流量集合
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量，3 流量集合")
    private Integer interactiveType = 3;

    /**
     * 描述(兼容前端修改)
     */
    @ApiModelProperty(value = "描述")
    private String introduction;

    /**
     * 流量信息
     */
    @ApiModelProperty(value = "流量信息")
    private List<FlowInfoModel> flowInfoList;

}
