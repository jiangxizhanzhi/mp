package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.request.InstallerRequest;
import cn.cyberpeace.mp.devops.cluster.request.ServiceManageDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ServiceManagePageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 服务管理
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/service")
@Api(tags = "靶标管理-服务管理")
public class InstallerController {

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public void page(ServiceManagePageRequest request){

    }

    /**
     * 新增
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public void add(@RequestBody InstallerRequest request){

    }

    /**
     * 更新
     */
    @ApiOperation("更新")
    @PutMapping("/update")
    public void update(@RequestBody InstallerRequest request){

    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public void delete(@RequestBody ServiceManageDeleteRequest request){

    }
}
