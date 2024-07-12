 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResultUtils.java.
 // File size 2216.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.utils;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import com.github.pagehelper.PageInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
     * emptyListObjServiceResult
     *
     * @return
     */
    public static ListObjServiceResult emptyListObjServiceResult() {
        return new ListObjServiceResult(new ListObjResult(Collections.emptyList(), 0).asResult()).asResult();
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



//    public static void throwExceptionIfError(ObjServiceResult result, ErrorCode errorCode) {
//        if (Objects.isNull(result.getSource())) {
//            throw new MpRuntimeException(errorCode);
//        }
//    }
//
//    public static void throwExceptionIfError(ObjServiceResult result, ErrorCode errorCode, String... params) {
//        if (Objects.isNull(result.getSource())) {
//            throw new MpRuntimeException(errorCode.format(params));
//        }
//    }


}
