 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceCreateContextBO.java.
 // File size 1097.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceCreateContextBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneInstanceCreateContextBO {

    @ApiModelProperty(value = "场景存活时间")
    @JsonIgnore
    private Integer sceneTtl;

    @ApiModelProperty(value = "场景属性")
    private Integer sceneAttr;

    @ApiModelProperty(value = "场景引用元数据")
    private String referenceMetaData;

    @ApiModelProperty(value = "场景模板引用类型")
    private Integer referenceType;

    @ApiModelProperty(value = "场景名称")
    private String name;

    @ApiModelProperty(value = "场景模板数据")
    private CsceneBO cscene;

    @ApiModelProperty(value = "场景上下文数据")
    private CsceneContextBO context;

}
