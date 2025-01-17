 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ServiceResult.java.
 // File size 1725.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
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
}
