package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.util.Arrays;


public interface BaseModelListControllerBase {

    
    default boolean isUnlimitPagination(String listFuncName) {
        var unlimitPaginationAnnotation = getClass().getAnnotation(UnlimitPagination.class);
        if (unlimitPaginationAnnotation == null) {
            return false;
        }

        return Arrays.asList(unlimitPaginationAnnotation.value()).contains(listFuncName);
    }

    
    default void handleQueryParams(String listFuncName, QueryParams queryParams) {
        ControllerCommon.convertValidQueryParams(queryParams, isUnlimitPagination(listFuncName));
    }

}
