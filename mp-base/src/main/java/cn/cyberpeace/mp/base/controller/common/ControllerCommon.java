package cn.cyberpeace.mp.base.controller.common;

import cn.cyberpeace.mp.base.constant.BaseSingleton;
import cn.cyberpeace.mp.base.controller.stereotype.ModelBOClass;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.param.*;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ControllerCommon {

    public static String[] convertValidFields(String[] fields) {
        if (fields == null) {
            return null;
        }

        if (Boolean.TRUE.equals(BaseSingleton.baseAppConfig.getSnakeApiMode())) {
            return TextUtils.lineToHump(fields);
        }

        return fields;
    }

    public static <Q> void convertValidQueryParams(Q queryParams, boolean unlimitPagination) {
        if (queryParams instanceof FieldsQueryParams) {
            ((FieldsQueryParams) queryParams).setFields(ControllerCommon.convertValidFields(((FieldsQueryParams) queryParams).getFields()));
        }
        if (queryParams instanceof SearchQueryParams) {
            ((SearchQueryParams) queryParams).setSearchFields(ControllerCommon.convertValidFields(((SearchQueryParams) queryParams).getSearchFields()));
        }
        if (queryParams instanceof OrderQueryParams) {
            ((OrderQueryParams) queryParams).setOrders(ControllerCommon.convertValidFields(((OrderQueryParams) queryParams).getOrders()));
        }
        if (queryParams instanceof CompatibleOrderQueryParams) {
            ((CompatibleOrderQueryParams) queryParams).setSort(ControllerCommon.convertValidFields(((CompatibleOrderQueryParams) queryParams).getSort()));
        }
        if (queryParams instanceof CompatibleQueryParams) {
            ((CompatibleQueryParams) queryParams).ensure();
        }

        if (queryParams instanceof PageQueryParams) {
            int page = ((PageQueryParams) queryParams).getPage() == null ? 1 : ((PageQueryParams) queryParams).getPage();
            int pageSize = ((PageQueryParams) queryParams).getPageSize() == null ? 0 : ((PageQueryParams) queryParams).getPageSize();
            if (pageSize <= 0) {
                pageSize = BaseSingleton.baseAppConfig.getDefaultPageSize();
            } else if (pageSize > BaseSingleton.baseAppConfig.getMaxPageSize() && !unlimitPagination) {
                pageSize = BaseSingleton.baseAppConfig.getMaxPageSize();
            }
            ((PageQueryParams) queryParams).setPage(page);
            ((PageQueryParams) queryParams).setPageSize(pageSize);
        }
    }

    public static <S extends BaseModelService<M, Q>, M extends BaseModel, Q, T extends ModelBO<M, T>> void exportExcel(
            S service, Q queryParams,  Class<T> textResourceClass,
            String filename, HttpServletResponse response) throws IOException {
        if (StringUtils.isEmpty(filename)) {
            filename = "export.xlsx";
        }

        response.reset();
        
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20").replace("%2B", "+");
        response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\"", encodedFilename));
        response.setContentType("application/octet-stream;charset=utf-8");
        var os = response.getOutputStream();
        service.exportExcel(queryParams, os, textResourceClass);
    }

    public static <S extends BaseModelService<M, ?>, M extends BaseModel, T extends ModelBO<M, T>> ServiceResult<Integer> importExcel(
            MultipartFile file,
            S service, Class<T> textResourceClass) throws IOException {
        var is = file.getInputStream();
        return service.importExcel(is, textResourceClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getModelBOClass(Class<?> controllerClass) {
        var modelBOClassAnnotation = controllerClass.getAnnotation(ModelBOClass.class);
        return (Class<T>) modelBOClassAnnotation.value();
    }

}
