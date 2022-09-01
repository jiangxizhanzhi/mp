package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;

public interface BaseModelBOService<T, Q> {

    default ListObjServiceResult<T> listBO(Q queryParams) {
        return new ListObjServiceResult<>();
    }

    default ObjServiceResult<T> getBO(String id, String[] fields) {
        return new ObjServiceResult<>();
    }

    default ObjServiceResult<T> createBO(T bo, String[] fields) {
        return new ObjServiceResult<>();
    }

    default ObjServiceResult<T> updateBO(T bo, String[] fields) {
        return new ObjServiceResult<>();
    }

    default ServiceResult<Boolean> deleteBO(String id) {
        return new ServiceResult<>();
    }

    default ServiceResult<Integer> bulkCreateBO(List<T> bos) {
        return new ServiceResult<>();
    }

    default ServiceResult<Integer> bulkUpdateBO(List<T> bos) {
        return new ServiceResult<>();
    }

    default ServiceResult<Integer> bulkDeleteBO(String[] ids) {
        return new ServiceResult<>();
    }

    default ObjServiceResult<T> getBO(String id) {
        return getBO(id, null);
    }

    default ObjServiceResult<T> createBO(T obj) {
        return createBO(obj, null);
    }

    default ObjServiceResult<T> updateBO(T obj) {
        return updateBO(obj, null);
    }
}
