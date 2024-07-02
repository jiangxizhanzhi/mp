package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.Arrays;

/**
 * @ClassName BaseModelListControllerBase
 * @Description 通用的查询列表的controller base
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Deprecated
public interface BaseModelListControllerBase {

    /**
     * 判断查询方法是否不限制分页大小
     *
     * @param listFuncName 查询方法名称
     * @return boolean
     */
    default boolean isUnlimitPagination(String listFuncName) {
        UnlimitPagination unlimitPaginationAnnotation = getClass().getAnnotation(UnlimitPagination.class);
        if (unlimitPaginationAnnotation == null) {
            return false;
        }

        return Arrays.asList(unlimitPaginationAnnotation.value()).contains(listFuncName);
    }

    /**
     * 处理有效的查询参数
     *
     * @param listFuncName 查询方法名称
     * @param queryParams  查询参数
     */
    default void handleQueryParams(String listFuncName, QueryParams queryParams) {
        ControllerCommon.convertValidQueryParams(queryParams, isUnlimitPagination(listFuncName));
    }

}
