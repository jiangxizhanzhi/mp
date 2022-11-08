package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/20 19:23
 */
public class ParamCheck {

    public static void notNull(Object param, String code) {
        if (Objects.isNull(param)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_NULL.format(MessageCommon.get(code)));
        }
    }

    public static void notBlank(String param, String code) {
        if (StringUtils.isBlank(param)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(code)));
        }
    }

    public static void notEmpty(String param, String code) {
        if (StringUtils.isEmpty(param)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(code)));
        }
    }

    public static void notEmpty(Collection<?> collection, String code) {
        if (CollectionUtil.isEmpty(collection)) {
            throw new MpRuntimeException(AuthErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(code)));
        }
    }

}
