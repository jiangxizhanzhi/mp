package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "基础模型，对应云平台接口返回信息")
public class BaseCloudPlatformModel extends BaseModel {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 总数据大小
     */
    private Integer total = 0;

    /**
     * 消息体
     */
    private String message;

    /**
     *  中文提示
     */
    private String messageZh;

    /**
     * 错误信息
     */
    private String errorMessage;

}
