package cn.cyberpeace.mp.devops.log.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 基础参数
 */
@Data
@NoArgsConstructor
public class BaseRequest implements Serializable {

    /**
     * 自定义验证参数方法
     */
    public void customValidParams(){

    }
}
