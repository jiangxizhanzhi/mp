package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;

public interface BaseModelService<Model> {

    default ListObjResult<Model> list(Integer page, Integer pageSize, String[] fields) {
        return new ListObjResult<>();
    }

    default ObjResult<Model> get(String id, String[] fields) {
        return null;
    }

    default ObjResult<Model> create(Model model, String[] fields) {
        return null;
    }

    default ObjResult<Model> update(String id, Model model, String[] fields) {
        return null;
    }

    default Boolean delete(String id) {
        return false;
    }
}
