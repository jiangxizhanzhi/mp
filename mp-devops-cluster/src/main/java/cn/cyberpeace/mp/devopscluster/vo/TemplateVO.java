 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TemplateVO.java.
 // File size 1044.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtGateway;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImagesPageVO
 * @Description 镜像分页返回信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "凌云的靶标信息")
public class TemplateVO {
    private String name;
    private String resourceId;
}
