 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePageBO.java.
 // File size 1476.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.bo.common;

import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BasePageBO
 * @Description 基础模型，对应云平台接口返回信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ListResult
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageBO<T> extends ListResult<T> {

    /**
     * 是否成功
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private boolean success;

    /**
     * 总数据大小
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer total = 0;

    /**
     * 消息体
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String message;

    /**
     * 错误信息
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String messageZh;

    /**
     * 错误信息
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String errorMessage;

    /**
     * 消息体
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String msg;

    /**
     * 消息体
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String msgZh;
}
