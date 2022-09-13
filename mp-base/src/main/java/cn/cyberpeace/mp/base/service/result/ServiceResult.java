package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一服务返回结果
 * @param <T> 返回结果数据源对象类型
 */
@Data
@AllArgsConstructor
@Deprecated
public class ServiceResult<T> extends BaseServiceResult<T, T> {

    public static <T1> ServiceResultBuilder<T1> builder() {
        return new ServiceResultBuilder<>();
    }

    public ServiceResult(T source) {
        this();
        setSource(source);
    }

    public ServiceResult(T source, ServiceCode code) {
        this(source);
        setServiceCode(code);
    }

    public ServiceResult(T source, ServiceCode code, String detail) {
        this(source, code);
        setDetail(detail);
    }

    @Override
    public ServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

    public static class ServiceResultBuilder<T> {
        private T source;
        private ServiceCode serviceCode = BaseErrorCode.SUCCESS;
        private String detail;

        public ServiceResultBuilder<T> source(T source) {
            this.source = source;
            return this;
        }

        public ServiceResultBuilder<T> serviceCode(ServiceCode serviceCode) {
            this.serviceCode = serviceCode;
            return this;
        }

        public ServiceResultBuilder<T> detail(String detail) {
            this.detail = detail;
            return this;
        }

        public ServiceResult<T> build() {
            return new ServiceResult<>(source, serviceCode, detail);
        }
    }

    /**
     * @Deprecated 不允许直接执行返回asResult, 请在controller层使用ResultCommon.success
     * 返回成功
     * @return
     */
    public static <T1> ServiceResult<T1> successful() {
        return ServiceResult.<T1>builder().build().asResult();
    }

    /**
     * @Deprecated 不允许直接执行返回asResult, 请在controller层使用ResultCommon.success
     * 返回成功，带数据
     * @param source
     * @return
     */
    public static <T1> ServiceResult<T1> success(T1 source) {
        return ServiceResult.<T1>builder().source(source).build().asResult();
    }

    /**
     * @Deprecated 不允许直接执行返回asResult, 请在controller层使用ResultCommon.fail
     * 返回失败
     * @param errorCode
     * @return
     */
    public static <T1> ServiceResult<T1> fail(ServiceCode errorCode) {
        return ServiceResult.<T1>builder().serviceCode(errorCode).build().asResult();
    }

    /**
     * @Deprecated 不允许直接执行返回asResult, 不允许复用错误码, 请消除此类代码
     * 返回通用失败信息
     * @param msg
     * @return
     */
    public static <T1> ServiceResult<T1> fail(String msg) {
        var serviceResult = new ServiceResult<T1>();
        serviceResult.setCode(BaseErrorCode.ERROR.getCode());
        serviceResult.setMessage(msg);
        return serviceResult.asResult();
    }

}
