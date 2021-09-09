package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.request.TargetPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 靶标管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/target")
@Api(tags = "靶标管理")
public class DevopsTargetController {

    private RealEquipmentConverter realEquipmentConverter;

    @Autowired
    public DevopsTargetController(RealEquipmentConverter realEquipmentConverter) {
        this.realEquipmentConverter = realEquipmentConverter;
    }

    /**
     * 分页查询靶标信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询靶标信息")
    @GetMapping("/page")
    public Result<List<Device>> page(TargetPageRequest request){
        List<Device> list = realEquipmentConverter.allTarget(request);
        return new Result<>(list);
    }

}
