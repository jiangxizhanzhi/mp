 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneElementRequest.java.
 // File size 924.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName BaseSceneElementRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/29 19:52
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseSceneElementRequest {

    @ApiModelProperty("场景id,多个id用逗号分隔")
    @NotNull
    private String sceneId;

    @ApiModelProperty("节点id")
    private List<String> elementIds;

    /**
     * 源IP集合
     */
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

    /**
     * Top数量
     */
    @ApiModelProperty("Top数量")
    private Integer topSize;
}
