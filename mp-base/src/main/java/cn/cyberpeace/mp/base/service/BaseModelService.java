package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;

public interface BaseModelService<Model> {

    default ServiceResult list(Integer page, Integer pageSize, String[] fields) {
        return new ServiceResult();
    }

    default ServiceResult get(String id, String[] fields) {
        return new ServiceResult();
    }

    default ServiceResult create(Model obj, String[] fields) {
        return new ServiceResult();
    }

    default ServiceResult update(Model obj, String[] fields) {
        return new ServiceResult();
    }

    default ServiceResult delete(String id) {
        return new ServiceResult();
    }

    default ServiceResult bulkCreate(List<Model> objs) {
        return new ServiceResult();
    }

    default ServiceResult bulkUpdate(List<Model> objs) {
        return new ServiceResult();
    }

    default ServiceResult bulkDelete(String[] ids) {
        return new ServiceResult();
    }
}
