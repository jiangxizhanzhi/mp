 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResultUtils.java.
 // File size 2004.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName ResultUtils
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
public class ResultUtils {

    /**
     * objList2ListObjServiceResult
     *
     * @param list
     * @param total
     * @return
     */
    public static ListObjServiceResult objList2ListObjServiceResult(List<Objects> list, Integer total) {
        return new ListObjServiceResult(new ListObjResult(list, total).asResult()).asResult();
    }


    /**
     * pageInfo2ObjServiceResult
     *
     * @param pageInfo
     * @return
     */
    public static ListObjServiceResult pageInfo2ObjServiceResult(PageInfo pageInfo) {
        return new ListObjServiceResult(new ListObjResult(pageInfo.getList(), Long.valueOf(pageInfo.getTotal()).intValue()).asResult()).asResult();
    }

    /**
     * obj2ObjServiceResult
     *
     * @param obj
     * @return
     */
    public static ObjServiceResult obj2ObjServiceResult(Object obj) {
        return new ObjServiceResult(new ObjResult(obj)).asResult();
    }


    /**
     * throwExceptionIfError
     *
     * @param result
     * @param errorMsg
     */
    @Deprecated
    public static void throwExceptionIfError(ObjServiceResult result, String errorMsg) {
        if (StringUtils.isBlank(errorMsg)) {
            errorMsg = "execute error!";
        }
        if (Objects.isNull(result.getSource())) {
            throw new MpRuntimeException(errorMsg);
        }
    }



}
