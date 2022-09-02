package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.common.ModelServiceCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseModelService<M extends BaseModel, Q> {

    default ListObjServiceResult<M> list(Q queryParams) {
        return list(queryParams, true);
    }

    default ListObjServiceResult<M> list(Q queryParams, boolean withTotal) {
        return new ListObjServiceResult<>();
    }

    default ObjServiceResult<M> get(String id, String[] fields) {
        return new ObjServiceResult<>();
    }

    default ObjServiceResult<M> create(M obj, String[] fields) {
        return new ObjServiceResult<>();
    }

    default ObjServiceResult<M> update(M obj, String[] fields) {
        return new ObjServiceResult<>();
    }

    default ServiceResult<Boolean> delete(String id) {
        return new ServiceResult<>();
    }

    default ServiceResult<Integer> bulkCreate(List<M> objs) {
        return new ServiceResult<>();
    }

    default ServiceResult<Integer> bulkUpdate(List<M> objs) {
        return new ServiceResult<>();
    }

    default ServiceResult<Integer> bulkDelete(String[] ids) {
        return new ServiceResult<>();
    }

    default ObjServiceResult<M> get(String id) {
        return get(id, null);
    }

    default ObjServiceResult<M> create(M obj) {
        return create(obj, null);
    }

    default ObjServiceResult<M> update(M obj) {
        return update(obj, null);
    }


    default <T extends ModelBO<M, T>> ListObjServiceResult<T> listBO(Q queryParams, Class<T> boClass) {
        return listBO(queryParams, true, boClass);
    }

    default <T extends ModelBO<M, T>> ListObjServiceResult<T> listBO(Q queryParams, boolean withTotal, Class<T> boClass) {
        var modelResult = list(queryParams, withTotal);
        return ModelServiceCommon.toListBOResult(modelResult, boClass);
    }

    default <T extends ModelBO<M, T>> ObjServiceResult<T> getBO(String id, String[] fields, Class<T> boClass) {
        var modelResult = get(id, fields);
        return ModelServiceCommon.toBOResult(modelResult, boClass);
    }

    default <T extends ModelBO<M, T>> ObjServiceResult<T> createBO(T bo, String[] fields, Class<T> boClass) {
        var modelResult = create(bo.toModel(), fields);
        return ModelServiceCommon.toBOResult(modelResult, boClass);
    }

    default <T extends ModelBO<M, T>> ObjServiceResult<T> updateBO(T bo, String[] fields, Class<T> boClass) {
        var modelResult = update(bo.toModel(), fields);
        return ModelServiceCommon.toBOResult(modelResult, boClass);
    }

    default <T extends ModelBO<M, T>> ServiceResult<Boolean> deleteBO(String id, Class<T> boClass) {
        return delete(id);
    }

    default <T extends ModelBO<M, T>> ServiceResult<Integer> bulkCreateBO(List<T> bos, Class<T> boClass) {
        var objs = bos.stream().map(bo -> bo.toModel()).collect(Collectors.toList());
        return bulkCreate(objs);
    }

    default <T extends ModelBO<M, T>> ServiceResult<Integer> bulkUpdateBO(List<T> bos, Class<T> boClass) {
        var objs = bos.stream().map(bo -> bo.toModel()).collect(Collectors.toList());
        return bulkUpdate(objs);
    }

    default <T extends ModelBO<M, T>> ServiceResult<Integer> bulkDeleteBO(String[] ids, Class<T> boClass) {
        return bulkDelete(ids);
    }

    default <T extends ModelBO<M, T>> ObjServiceResult<T> getBO(String id, Class<T> boClass) {
        return getBO(id, null, boClass);
    }

    default <T extends ModelBO<M, T>> ObjServiceResult<T> createBO(T bo, Class<T> boClass) {
        return createBO(bo, null, boClass);
    }

    default <T extends ModelBO<M, T>> ObjServiceResult<T> updateBO(T bo, Class<T> boClass) {
        return updateBO(bo, null, boClass);
    }

    default <T extends ModelBO<M, T>> ServiceResult<Boolean> exportExcel(Q queryParams, OutputStream os,
                                                                         Class<T> textResourceClass) {
        var modelResult = list(queryParams, false);
        ModelServiceCommon.exportListExcel(modelResult, os, textResourceClass);
        return new ServiceResult<>(true);
    }

    default <T extends ModelBO<M, T>> ServiceResult<Integer> importExcel(
            InputStream is, Class<T> textResourceClass) {
        var objs = ModelServiceCommon.importListExcel(is, textResourceClass);
        return bulkCreate(objs);
    }

}
