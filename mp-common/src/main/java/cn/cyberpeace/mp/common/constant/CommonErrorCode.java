package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * common模块的错误码定义
 */
public enum CommonErrorCode implements ErrorCode {
    UPLOAD_FILE_TYPE_ERROR("MP-110800", "upload_file_type_error"),

    /**
     * 将扁平结构转成嵌套结构父编码不存在
     */
    NO_PARENT_CODE_ERROR("MP-110801", "no_parent_code_error");

    CommonErrorCode(String code, String message) {
        setCode(code, "common.error_code." + message);
    }
}
