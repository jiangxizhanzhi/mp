package cn.cyberpeace.mp.base.util.fdfsclient;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: FastDFS错误码
 * @Author qiaokang
 * @Date：2021/6/21 15:01
 */
public enum FastDfsErrorCode implements ErrorCode {

    FILE_PATH_ISNULL("MP-110100", "file_path_isnull"),

    FILE_ISNULL("MP-110101", "file_isnull"),

    FILE_UPLOAD_FAILED("MP-110102", "file_upload_failed"),

    FILE_NOT_EXIST("MP-110103", "file_not_exist"),

    FILE_DOWNLOAD_FAILED("MP-110104", "file_download_failed"),

    FILE_DELETE_FAILED("MP-110105", "file_delete_failed"),

    FILE_SERVER_CONNECTION_FAILED("MP-110106", "file_server_connection_failed"),

    FILE_OUT_SIZE("MP-110107", "file_server_connection_failed"),

    FILE_TYPE_ERROR_IMAGE("MP-110108", "type.image"),

    FILE_TYPE_ERROR_DOC("MP-110109", "type.doc"),

    FILE_TYPE_ERROR_VIDEO("MP-110110", "type.video"),

    FILE_TYPE_ERROR_COMPRESS("MP-110111", "type.compress"),

    FILE_SERVER_INIT_FAILED("MP-110112", "file_server_init_failed");

    FastDfsErrorCode(String code, String message) {
        setCode(code, "fastdfs.error_code." + message);
    }

}
