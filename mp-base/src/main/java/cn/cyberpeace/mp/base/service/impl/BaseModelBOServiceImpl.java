package cn.cyberpeace.mp.base.service.impl;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.service.BaseModelBOService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.common.ModelServiceCommon;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.service.stereotype.ModelBOClass;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public abstract class BaseModelBOServiceImpl<T extends ModelBO<M, T>, M extends BaseModel, P extends BaseModelMapper<M>,
        F extends BaseModelFields, Q extends QueryParams>
        extends BaseModelServiceImpl<M, P, F, Q> implements BaseModelBOService<T, Q> {

    @SuppressWarnings("unchecked")
    public Class<T> getModelBOClass() {
        var modelBOClassAnnotation = getClass().getAnnotation(ModelBOClass.class);
        return (Class<T>) modelBOClassAnnotation.value();
    }

    @Override
    public ListObjServiceResult<T> listBO(Q queryParams) {
        var modelResult = list(queryParams);
        return ModelServiceCommon.toListBOResult(modelResult, getModelBOClass());
    }

    @Override
    public ObjServiceResult<T> getBO(String id, String[] fields) {
        var modelResult = get(id, fields);
        return ModelServiceCommon.toBOResult(modelResult, getModelBOClass());
    }

    @Override
    public ObjServiceResult<T> createBO(T bo, String[] fields) {
        var modelResult = create(bo.toModel(), fields);
        return ModelServiceCommon.toBOResult(modelResult, getModelBOClass());
    }

    @Override
    public ObjServiceResult<T> updateBO(T bo, String[] fields) {
        var modelResult = update(bo.toModel(), fields);
        return ModelServiceCommon.toBOResult(modelResult, getModelBOClass());
    }

    @Override
    public ServiceResult<Boolean> deleteBO(String id) {
        return delete(id);
    }

    @Override
    public ServiceResult<Integer> bulkCreateBO(List<T> bos) {
        var objs = bos.stream().map(bo -> bo.toModel()).collect(Collectors.toList());
        return bulkCreate(objs);
    }

    @Override
    public ServiceResult<Integer> bulkUpdateBO(List<T> bos) {
        var objs = bos.stream().map(bo -> bo.toModel()).collect(Collectors.toList());
        return bulkUpdate(objs);
    }

    @Override
    public ServiceResult<Integer> bulkDeleteBO(String[] ids) {
        return bulkDelete(ids);
    }

    @Override
    public <T1 extends ModelBO<M, T1>> ServiceResult<Integer> importExcel(InputStream is, Class<T1> tClass) {
        var objs = ModelServiceCommon.importListExcel(is, tClass);
        // simple validate
        objs.forEach(obj -> ClassUtils.createInstance(getModelBOClass()).fromModel(obj));
        return bulkCreate(objs);
    }

}
