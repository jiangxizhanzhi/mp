package cn.cyberpeace.mp.devops.cluster.converter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageConvertModel {

    /**
     * 是否成功
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private boolean success;

    /**
     * 请求ID
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String request_id;

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
}
