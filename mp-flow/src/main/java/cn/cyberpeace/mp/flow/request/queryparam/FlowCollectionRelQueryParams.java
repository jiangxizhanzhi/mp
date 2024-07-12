 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowCollectionRelQueryParams.java.
 // File size 638.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowCollectionRelQueryParams
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel(value = "流量集合关联通用查询条件")
public class FlowCollectionRelQueryParams extends QueryParams {

    /**
     * 流量集合id
     */
    @ApiModelProperty(value = "流量集合id")
    private String collectionId;

}
