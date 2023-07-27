package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.model.RoleOperateConfigModel;
import cn.cyberpeace.mp.aggregation.repository.RoleOperateConfigMapper;
import cn.cyberpeace.mp.aggregation.request.EventTrackRequest;
import cn.cyberpeace.mp.aggregation.service.EventTrackingService;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.validation.stereotype.Length;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "操作权限查询", tags = "操作权限查询")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/operate")
public class RoleOpearteConfigController {

    @Autowired
    private RoleOperateConfigMapper roleOperateConfigMapper;

    @ApiOperation("操作权限查询")
    @GetMapping("/query")
    public ServiceResult<List<RoleOperateConfigModel>> page(@Param("module") String module) {
        var result = roleOperateConfigMapper.list(new HumanCondition().filterEqual(RoleOperateConfigModel::getModule, module));
        return new ServiceResult<>(result).asResult();
    }
}