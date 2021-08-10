package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentAddRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.RealEquipmentPageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/real_equipment")
@Api(tags = "实装管理")
public class RealEquipmentController {

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public ListObjServiceResult<RealEquipmentPageResponse> page(RealEquipmentPageRequest request){
        return null;
    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public void add(@RequestBody RealEquipmentAddRequest request){

    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public void delete(){

    }


}
