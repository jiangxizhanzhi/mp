 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RouterPageDTO.java.
 // File size 897.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.RouterPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName RouterPageDTO
 * @Description 路由分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RouterPageDTO extends BasePageDTO {

    /**
     * 路由名称
     */
    @ApiModelProperty(value = "路由名称")
    private String name;

    public RouterPageDTO(RouterPageRequest request){
        this.name = request.getName();
        super.setLimit(request.getPerPage());
        super.setOffset(request.getPage());
    }
}
