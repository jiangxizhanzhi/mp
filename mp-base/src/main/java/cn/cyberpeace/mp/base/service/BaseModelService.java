package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;
import java.util.Map;

public interface BaseModelService<M> {

    default ListObjServiceResult<M> list(Map<String, Object> params, String[] ids, String search, String[] searchFields,
                                         String[] orders, int page, int pageSize, String[] fields) {
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

    default ListObjServiceResult<M> list(Map<String, Object> params, String[] ids, String search, String[] searchFields,
                                         String[] orders, int page, int pageSize) {
        return list(params, ids, search, searchFields, orders, page, pageSize, null);
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
}
