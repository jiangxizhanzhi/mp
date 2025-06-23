 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ServiceResult.java.
 // File size 2641.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName ServiceResult
 * @Description
 * 统一服务返回结果
 * @param <T> 返回结果数据源对象类型
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseServiceResult
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
