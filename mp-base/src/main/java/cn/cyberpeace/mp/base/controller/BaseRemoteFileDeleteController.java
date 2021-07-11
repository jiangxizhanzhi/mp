package cn.cyberpeace.mp.base.controller;

import cn.cyberpeace.mp.base.controller.request.BaseFileDeleteData;
import cn.cyberpeace.mp.base.service.BaseRemoteFileService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


public interface BaseRemoteFileDeleteController<S extends BaseRemoteFileService> {

    S getRemoteFileService();

    
    @ApiOperation("删除指定id文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件id", dataTypeClass = String.class),
    })
    @DeleteMapping(value = "/file_delete", params = "id")
    default ServiceResult<Boolean> deleteByParam(@RequestParam String id) {
        return getRemoteFileService().delete(id).asResult();
    }

    
    @ApiOperation("删除指定id文件")
    @DeleteMapping("/file_delete")
    default ServiceResult<Boolean> deleteByData(@RequestBody BaseFileDeleteData deleteData) {
        return getRemoteFileService().delete(deleteData.getId()).asResult();
    }

}
