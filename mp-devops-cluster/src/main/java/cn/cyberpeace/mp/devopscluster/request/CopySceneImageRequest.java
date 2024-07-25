 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CopyImageRequest.java.
 // File size 566.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
  * @ClassName ImageRequest
  * @Description 镜像新增请求信息
  * @Author maojun
  * @Date 2024/07/02
  * @Version 1.0.0
  * @see BaseRequest
  */
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public class CopySceneImageRequest {

     @ApiModelProperty(value = "新靶标名称")
     private String name;

     /**
      * UUID
      */
     @ApiModelProperty(value = "场景id")
     private String id;

     @ApiModelProperty(value = "场景节点id")
     private String nodeId;

 }

