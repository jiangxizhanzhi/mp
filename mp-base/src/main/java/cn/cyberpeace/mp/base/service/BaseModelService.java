package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.common.ModelServiceCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BaseModelService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface BaseModelService<M extends BaseModel, Q> {

    /**
     * list
     *
     * @param queryParams
     * @return
     */
    default ListObjServiceResult<M> list(Q queryParams) {
        return list(queryParams, true);
    }

    /**
     * list
     *
     * @param queryParams
     * @param withTotal
     * @return
     */
    default ListObjServiceResult<M> list(Q queryParams, boolean withTotal) {
        return new ListObjServiceResult<>();
    }

    /**
     * get
     *
     * @param id
     * @param fields
     * @return
     */
    default ObjServiceResult<M> get(String id, String[] fields) {
        return new ObjServiceResult<>();
    }

    /**
     * create
     *
     * @param obj
     * @param fields
     * @return
     */
    default ObjServiceResult<M> create(M obj, String[] fields) {
        return new ObjServiceResult<>();
    }

    /**
     * update
     *
     * @param obj
     * @param fields
     * @return
     */
    default ObjServiceResult<M> update(M obj, String[] fields) {
        return new ObjServiceResult<>();
    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    default ServiceResult<Boolean> delete(String id) {
        return new ServiceResult<>();
    }

    /**
     * bulkCreate
     *
     * @param objs
     * @return
     */
    default ServiceResult<Integer> bulkCreate(List<M> objs) {
        return new ServiceResult<>();
    }

    /**
     * bulkUpdate
     *
     * @param objs
     * @return
     */
    default ServiceResult<Integer> bulkUpdate(List<M> objs) {
        return new ServiceResult<>();
    }

    /**
     * bulkDelete
     *
     * @param ids
     * @return
     */
    default ServiceResult<Integer> bulkDelete(String[] ids) {
        return new ServiceResult<>();
    }

    /**
     * get
     *
     * @param id
     * @return
     */
    default ObjServiceResult<M> get(String id) {
        return get(id, null);
    }

    /**
     * create
     *
     * @param obj
     * @return
     */
    default ObjServiceResult<M> create(M obj) {
        return create(obj, null);
    }

    /**
     * update
     *
     * @param obj
     * @return
     */
    default ObjServiceResult<M> update(M obj) {
        return update(obj, null);
    }


    /**
     * listBO
     *
     * @param queryParams
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ListObjServiceResult<T> listBO(Q queryParams, Class<T> boClass) {
        return listBO(queryParams, true, boClass);
    }

    /**
     * listBO
     *
     * @param queryParams
     * @param withTotal
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ListObjServiceResult<T> listBO(Q queryParams, boolean withTotal, Class<T> boClass) {
        ListObjServiceResult<M> modelResult = list(queryParams, withTotal);
        return ModelServiceCommon.toListBOResult(modelResult, boClass);
    }

    /**
     * getBO
     *
     * @param id
     * @param fields
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ObjServiceResult<T> getBO(String id, String[] fields, Class<T> boClass) {
        ObjServiceResult<M> modelResult = get(id, fields);
        return ModelServiceCommon.toBOResult(modelResult, boClass);
    }

    /**
     * createBO
     *
     * @param bo
     * @param fields
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ObjServiceResult<T> createBO(T bo, String[] fields, Class<T> boClass) {
        ObjServiceResult<M> modelResult = create(bo.toModel(), fields);
        return ModelServiceCommon.toBOResult(modelResult, boClass);
    }

    /**
     * updateBO
     *
     * @param bo
     * @param fields
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ObjServiceResult<T> updateBO(T bo, String[] fields, Class<T> boClass) {
        ObjServiceResult<M> modelResult = update(bo.toModel(), fields);
        return ModelServiceCommon.toBOResult(modelResult, boClass);
    }

    /**
     * deleteBO
     *
     * @param id
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ServiceResult<Boolean> deleteBO(String id, Class<T> boClass) {
        return delete(id);
    }

    /**
     * bulkCreateBO
     *
     * @param bos
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ServiceResult<Integer> bulkCreateBO(List<T> bos, Class<T> boClass) {
        List<M> objs = new ArrayList<>();
        for (T bo : bos) {
            M model = bo.toModel();
            objs.add(model);
        }
        return bulkCreate(objs);
    }

    /**
     * bulkUpdateBO
     *
     * @param bos
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ServiceResult<Integer> bulkUpdateBO(List<T> bos, Class<T> boClass) {
        List<M> objs = new ArrayList<>();
        for (T bo : bos) {
            M model = bo.toModel();
            objs.add(model);
        }
        return bulkUpdate(objs);
    }

    /**
     * bulkDeleteBO
     *
     * @param ids
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ServiceResult<Integer> bulkDeleteBO(String[] ids, Class<T> boClass) {
        return bulkDelete(ids);
    }

    /**
     * getBO
     *
     * @param id
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ObjServiceResult<T> getBO(String id, Class<T> boClass) {
        return getBO(id, null, boClass);
    }

    /**
     * createBO
     *
     * @param bo
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ObjServiceResult<T> createBO(T bo, Class<T> boClass) {
        return createBO(bo, null, boClass);
    }

    /**
     * updateBO
     *
     * @param bo
     * @param boClass
     * @return
     */
    default <T extends ModelBO<M, T>> ObjServiceResult<T> updateBO(T bo, Class<T> boClass) {
        return updateBO(bo, null, boClass);
    }

    /**
     * exportExcel
     *
     * @param queryParams
     * @param os
     * @param textResourceClass
     * @return
     */
    default <T extends ModelBO<M, T>> ServiceResult<Boolean> exportExcel(Q queryParams, OutputStream os,
                                                                         Class<T> textResourceClass) {
        ListObjServiceResult<M> modelResult = list(queryParams, false);
        ModelServiceCommon.exportListExcel(modelResult, os, textResourceClass);
        return new ServiceResult<>(true);
    }

    /**
     * importExcel
     *
     * @param is
     * @param textResourceClass
     * @return
     */
    default <T extends ModelBO<M, T>> ServiceResult<Integer> importExcel(
            InputStream is, Class<T> textResourceClass) {
        List<M> objs = ModelServiceCommon.importListExcel(is, textResourceClass);
        return bulkCreate(objs);
    }

}
