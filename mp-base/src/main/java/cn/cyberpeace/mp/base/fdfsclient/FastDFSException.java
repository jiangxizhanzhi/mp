package cn.cyberpeace.mp.base.fdfsclient;

import lombok.Data;

/**
 * @Description: FastDFS异常信息
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
@Data
public class FastDFSException extends Exception {

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误消息
     */
    private String message;

    public FastDFSException() {
    }

    public FastDFSException(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
