 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BizServicePageRequest.java.
 // File size 825.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName BizServicePageRequest
 * @Description 业务监控分页查询
 * @Author qiaokang
 * @Date 2022/7/15 14:56
 * @Version 1.0.0
 * @see BasePageRequest
 * @see Serializable
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BizServicePageRequest extends BasePageRequest implements Serializable {

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String hostName;

    /**
     * 如 +runtime or -runtime
     */
    @ApiModelProperty(value = "排序方式")
    private String sort;

}
