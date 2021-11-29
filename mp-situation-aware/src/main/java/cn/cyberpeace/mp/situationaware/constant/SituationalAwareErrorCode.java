package cn.cyberpeace.mp.situationaware.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
  *@Desciption 
  *@Author zhangjin
  *@Date 9/3/21 10:22 上午
**/
public enum SituationalAwareErrorCode implements ErrorCode {
    PROBE_DELETE_FAIL("MP-110401", "probe delete fail"),
    PROBE_MODIFY_FAIL("MP-110402", "probe update fail"),
    PROBE_MODIFY_PARAM_ERROR("MP-110403", "probe update param error"),
    PROBE_MANAGER_ADD_ERROR("MP-110404", "probe manager add script error"),
    PROBE_NAME_EXIST("MP-110405", "probe name already exit"),
    PROBE_MANAGER_ES_QUERY_ERROR("MP-110406", "probe es query error"),
    PROBE_MANAGER_DELETE_PARAM_ERROR("MP-110407", "probe delete param is empty"),
    ARKIME_ADD_ERROR("MP-110408", "arkime instance add error"),
    ARKIME_DELETE_ERROR("MP-110409", "delete instance add error"),
    ADD_FLOWTABLE_ERROR("MP-110410", "add flow table error"),
    DELETE_FLOWTABLE_ERROR("MP-110411", "delete flow table error"),
    BINDING_MIRROR_ERROR("MP-110412", "binding flow mirror error");

    SituationalAwareErrorCode(String code, String message) {
        setCode(code, message);
    }
}
