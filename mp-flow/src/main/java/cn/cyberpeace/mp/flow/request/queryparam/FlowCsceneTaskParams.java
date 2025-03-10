// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowTaskPageQueryParams.java.
// File size 799.
// File create date Fri Jul 12 12:55:23 CST 2024.
// File update date Fri Jul 12 12:55:23 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @ClassName FlowTaskPageQueryParams
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel("查询流量任务列表请求体")
public class FlowCsceneTaskParams extends QueryParams{
    private String csceneInstanceId;
    private String csceneNodeId;
}
