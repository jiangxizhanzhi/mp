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
public class ServiceResult<T> extends BaseServiceResult<T, T> {

    /**
     * builder
     *
     * @return
     */
    public static <T1> ServiceResultBuilder<T1> builder() {
        return new ServiceResultBuilder<>();
    }

    /**
     * ServiceResult
     *
     * @param source
     */
    public ServiceResult(T source) {
        this();
        setSource(source);
    }

    /**
     * ServiceResult
     *
     * @param source
     * @param code
     */
    public ServiceResult(T source, ServiceCode code) {
        this(source);
        setServiceCode(code);
    }

    /**
     * ServiceResult
     *
     * @param source
     * @param code
     * @param detail
     */
    public ServiceResult(T source, ServiceCode code, String detail) {
        this(source, code);
        setDetail(detail);
    }

    /**
     * asResult
     *
     * @return
     */
    @Override
    public ServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

    public static class ServiceResultBuilder<T> {
        private T source;
        private ServiceCode serviceCode = BaseErrorCode.SUCCESS;
        private String detail;

        /**
         * source
         *
         * @param source
         * @return
         */
        public ServiceResultBuilder<T> source(T source) {
            this.source = source;
            return this;
        }

        /**
         * serviceCode
         *
         * @param serviceCode
         * @return
         */
        public ServiceResultBuilder<T> serviceCode(ServiceCode serviceCode) {
            this.serviceCode = serviceCode;
            return this;
        }

        /**
         * detail
         *
         * @param detail
         * @return
         */
        public ServiceResultBuilder<T> detail(String detail) {
            this.detail = detail;
            return this;
        }

        /**
         * build
         *
         * @return
         */
        public ServiceResult<T> build() {
            return new ServiceResult<>(source, serviceCode, detail);
        }
    }
}
