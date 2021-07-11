package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
public abstract class BaseModelController<M extends BaseModel, Q extends QueryParams, S extends BaseModelService<M, Q>>
        implements BaseModelListController<M, Q, S>, BaseModelGetController<M, S>, BaseModelCreateController<M, S>,
        BaseModelBulkCreateController<M, S>, BaseModelUpdateController<M, S>, BaseModelBulkUpdateController<M, S>,
        BaseModelDeleteController<M, S>, BaseModelBulkDeleteController<M, S> {

    
    private S modelService;

}
