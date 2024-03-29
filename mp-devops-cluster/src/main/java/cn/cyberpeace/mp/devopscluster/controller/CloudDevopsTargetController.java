package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.MicrosoftAzureConverter;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cloudTarget")
@Api(tags = "云靶标管理")
@RequiredArgsConstructor
public class CloudDevopsTargetController {

    @Autowired
    private MicrosoftAzureConverter microsoftAzureConverter;

    /**
     * 查询所有镜像管理分类
     */
    @ApiOperation("查询所有镜像管理分类")
    @GetMapping("/imageCategories")
    public ServiceResult<List> imageCategories(){
//        TypeAllVO returnData = microsoftAzureConverter.imageCategories(false);
        List list = microsoftAzureConverter.getImageCategories();
        return new ServiceResult<List>(list, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 查询所有云靶标信息
     * @param jsonObject
     * @return
     */
    @ApiOperation("查询所有信息(带过滤条件)")
    @GetMapping("/getTarget")
    public ServiceResult getTarget(JSONObject jsonObject){
        List list = microsoftAzureConverter.getCloudTargetList(jsonObject);
//        List returnData = microsoftAzureConverter.getTarget(jsonObject);
        return new Result(list).asResult();
    }

    /**
     * 查询所有靶标信息
     * @param request
     * @return
     */
    @ApiOperation("查询常用靶标信息")
    @GetMapping("/getCommonTargets")
    public ServiceResult getCommonTargets(TargetSceneRequest request){
        List<DeviceVO> returnData = microsoftAzureConverter.getCommonTargets(request);
        return new Result(new ListResult(returnData)).asResult();
    }

}
