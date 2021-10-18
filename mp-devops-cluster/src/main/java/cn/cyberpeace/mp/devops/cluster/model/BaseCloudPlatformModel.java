package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 基础模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseCloudPlatformModel extends BaseModel {

    /**
     * 是否成功
     */
    @ApiModelProperty(hidden = true)
    private boolean success;

    /**
     * 请求ID
     */
    @ApiModelProperty(hidden = true)
    private String requestId;

    /**
     * 总数据大小
     */
    @ApiModelProperty(hidden = true)
    private Integer total = 0;

    /**
     * 消息体
     */
    @ApiModelProperty(hidden = true)
    private String message;

    /**
     *  中文提示
     */
    @ApiModelProperty(hidden = true)
    private String messageZh;

    /**
     * 错误信息
     */
    @ApiModelProperty(hidden = true)
    private String errorMessage;

    /**
     * code
     */
    @ApiModelProperty(hidden = true)
    private String code;

    /**
     * 临时文件UUID
     */
    @ApiModelProperty(hidden = true)
    private String tmpdirUuid;

    /**
     * 上传云平台生成的文件名
     */
    @ApiModelProperty(hidden = true)
    private List<String> fileNames;

    @ApiModelProperty(hidden = true)
    private Object detail;
}
