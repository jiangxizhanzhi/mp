 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowCollectionRelModel.java.
 // File size 964.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName FlowCollectionRelModel
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_collection_rel")
public class FlowCollectionRelModel extends BaseModel {

    /**
     * 流量集合id
     */
    @ApiModelProperty(value = "流量集合id")
    @NotBlank(message = "collectionId can not be empty")
    private String collectionId;

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    @NotBlank(message = "flowId can not be empty")
    private String flowId;

}
