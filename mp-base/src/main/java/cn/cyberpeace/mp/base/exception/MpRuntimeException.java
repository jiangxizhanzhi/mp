package cn.cyberpeace.mp.base.exception;

import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.Data;

/**
 * 文件名：Example.java
 * <p>
 * <p>
 * <p>
 * 版本信息：v1.0
 * <p>
 * <p>
 * <p>
 * 日期：2023-04-01
 * <p>
 * <p>
 * <p>
 * 描述：
 * <p>
 * <p>
 * <p>
 * 这是一个示例Java文件，展示了开头注释的标准格式。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 版权信息：
 * <p>
 * <p>
 * <p>
 * © 2023 你的名字或公司
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 修改历史：
 * <p>
 * <p>
 * <p>
 * 2023-04-01 初始版本
 */
@Data
public class MpRuntimeException extends RuntimeException {

    private ServiceCode serviceCode;

    private Exception rawException;

    private String detail;

    /**
     * MpRuntimeException
     *
     * @param message
     */
    public MpRuntimeException(String message) {
        super(message);
    }

    /**
     * MpRuntimeException
     *
     * @param serviceCode
     */
    public MpRuntimeException(ServiceCode serviceCode) {
        super(serviceCode.transMessage());
        this.serviceCode = serviceCode;
    }

    /**
     * MpRuntimeException
     *
     * @param serviceCode
     * @param detail
     */
    public MpRuntimeException(ServiceCode serviceCode, String detail) {
        this(serviceCode);
        this.detail = detail;
    }

    /**
     * MpRuntimeException
     *
     * @param serviceCode
     * @param rawException
     */
    public MpRuntimeException(ServiceCode serviceCode, Exception rawException) {
        this(serviceCode, rawException.getMessage());
    }

    /**
     * e
     *
     * @param rawException
     * @return 文件名：Example.java
     */
    public MpRuntimeException e(Exception rawException) {
        this.rawException = rawException;
        return this;
    }

    /**
     * de
     *
     * @param rawException
     * @return 文件名：Example.java
     */
    public MpRuntimeException de(Exception rawException) {
        this.detail = rawException.getMessage();
        return e(rawException);
    }

}
