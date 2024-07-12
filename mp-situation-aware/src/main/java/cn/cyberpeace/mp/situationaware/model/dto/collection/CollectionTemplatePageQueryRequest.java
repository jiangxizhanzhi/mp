 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionTemplatePageQueryRequest.java.
 // File size 1783.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("采集模板查询请求体")
/**
 * 收集模板分页查询请求类
 * 继承自BasePageRequest，用于指定分页查询的参数以及页码和每页数量等信息。
 * 本类用于细化查询条件，针对收集模板的特定属性进行查询。
 */
public class CollectionTemplatePageQueryRequest extends BasePageRequest {

    /**
     * 收集模板ID数组
     * 用于指定查询的收集模板ID列表，支持多ID查询。
     */
    @ApiModelProperty(value = "采集模板ids")
    private String[] ids;

    /**
     * 收集模板名称
     * 用于指定查询的收集模板名称，支持模糊匹配。
     */
    @ApiModelProperty(value = "采集模板名称，模糊匹配")
    private String name;

    /**
     * 操作系统类型数组
     * 用于指定查询的收集模板支持的操作系统类型，支持多类型查询。
     * 取值说明：null表示全部，0表示Windows，1表示Unix，2表示Linux，3表示BSD，4表示Other。
     */
    @ApiModelProperty(value = "null:全部, 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private Integer[] osTypes;

    /**
     * 创建者用户名
     * 用于指定查询的收集模板的创建者用户名，支持精确查询。
     */
    @ApiModelProperty(value = "创建者用户名")
    private String creator;

}

