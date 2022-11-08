package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.code.ErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public class ResultUtils {

    public static ListObjServiceResult objList2ListObjServiceResult(List<Objects> list, Integer total) {
        return new ListObjServiceResult(new ListObjResult(list, total).asResult()).asResult();
    }


    public static ListObjServiceResult pageInfo2ObjServiceResult(PageInfo pageInfo) {
        return new ListObjServiceResult(new ListObjResult(pageInfo.getList(), Long.valueOf(pageInfo.getTotal()).intValue()).asResult()).asResult();
    }

    public static ObjServiceResult obj2ObjServiceResult(Object obj) {
        return new ObjServiceResult(new ObjResult(obj)).asResult();
    }

    public static void throwExceptionIfError(ObjServiceResult result, ErrorCode errorCode) {
        if (Objects.isNull(result.getSource())) {
            throw new MpRuntimeException(errorCode);
        }
    }

    public static void throwExceptionIfError(ObjServiceResult result, ErrorCode errorCode, String... params) {
        if (Objects.isNull(result.getSource())) {
            throw new MpRuntimeException(errorCode.format(params));
        }
    }


}
