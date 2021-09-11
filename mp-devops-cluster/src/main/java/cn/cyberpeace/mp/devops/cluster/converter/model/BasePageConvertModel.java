package cn.cyberpeace.mp.devops.cluster.converter.model;

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
    private boolean success;

    /**
     * 请求ID
     */
    private String request_id;

    /**
     * 总数据大小
     */
    private Integer total = 0;

    /**
     * 消息体
     */
    private String message;

    /**
     * 错误信息
     */
    private String messageZh;

}
