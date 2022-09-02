package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.common.ControllerCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface BaseModelBulkCreateBOController<T extends ModelBO<M, T>, M extends BaseModel, S extends BaseModelService<M, ?>> {

    S getModelService();

    @ApiOperation("批量创建数据")
    @ApiImplicitParam(name = "bos", value = "创建数据请求体", dataTypeClass = List.class)
    @PostMapping("/bulk_create")
    default ServiceResult<Integer> bulkCreate(@RequestBody List<T> bos) {
        return getModelService().<T>bulkCreateBO(bos, ControllerCommon.getModelBOClass(getClass())).asResult();
    }

}
