 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstancePageDTO.java.
 // File size 1186.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.InstancePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName InstancePageDTO
 * @Description 实例分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageDTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstancePageDTO extends BasePageDTO {

    /**
     * 主机名称/节点名称
     */
    @ApiModelProperty(value = "主机名称/节点名称")
    private String host;

    @ApiModelProperty(value = "IP地址")
    private String addr;

    public InstancePageDTO(String host) {
        this.host = host;
    }

    public InstancePageDTO(String host, String addr) {
        this.host = host;
        this.addr = addr;
    }

    public InstancePageDTO(InstancePageRequest request){
        this.host = request.getHost();
        this.addr = request.getAddr();
        super.setLimit(request.getPerPage());
        super.setOffset(request.getPage());
    }
}
