 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseModelBOController.java.
 // File size 1335.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName BaseModelBOController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelListBOController
 * @see BaseModelGetBOController
 * @see BaseModelCreateBOController
 * @see BaseModelBulkCreateBOController
 * @see BaseModelUpdateBOController
 * @see BaseModelBulkUpdateBOController
 * @see BaseModelDeleteBOController
 * @see BaseModelBulkDeleteBOController
 */
@Slf4j
@Data
@Deprecated
public abstract class BaseModelBOController<T extends ModelBO<M, T>, M extends BaseModel, Q extends QueryParams, S extends BaseModelService<M, Q>>
        implements BaseModelListBOController<T, M, Q, S>, BaseModelGetBOController<T, M, S>, BaseModelCreateBOController<T, M, S>,
        BaseModelBulkCreateBOController<T, M, S>, BaseModelUpdateBOController<T, M, S>, BaseModelBulkUpdateBOController<T, M, S>,
        BaseModelDeleteBOController<T, M, S>, BaseModelBulkDeleteBOController<T, M, S> {

    /** 在实现类注入 */
    private S modelService;

}
