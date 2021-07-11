package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class BaseModelBOController<T extends ModelBO<M, T>, M extends BaseModel, Q extends QueryParams, S extends BaseModelService<M, Q>>
        implements BaseModelListBOController<T, M, Q, S>, BaseModelGetBOController<T, M, S>, BaseModelCreateBOController<T, M, S>,
        BaseModelBulkCreateBOController<T, M, S>, BaseModelUpdateBOController<T, M, S>, BaseModelBulkUpdateBOController<T, M, S>,
        BaseModelDeleteBOController<T, M, S>, BaseModelBulkDeleteBOController<T, M, S> {

    
    private S modelService;

}
