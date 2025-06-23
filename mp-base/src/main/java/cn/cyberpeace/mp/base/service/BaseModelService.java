 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelService.java.
 // File size 7860.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;

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


}
