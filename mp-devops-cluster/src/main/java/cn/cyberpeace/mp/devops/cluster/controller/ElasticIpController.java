package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.ElasticIpResponse;
import cn.cyberpeace.mp.devops.cluster.service.ElasticIpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 弹性IP管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/elasticip")
@Api(tags = "网络实例管理-弹性IP")
public class ElasticIpController {

    private ElasticIpService elasticIpService;

    @Autowired
    public ElasticIpController(ElasticIpService elasticIpService) {
        this.elasticIpService = elasticIpService;
    }

    /**
     * 分页查询弹性IP信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询弹性IP信息")
    @GetMapping("/page")
    public ListObjServiceResult<ElasticIpResponse> page(ElasticIpPageRequest request){
        ElasticIpCloudPlatformModel model = elasticIpService.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getFloatingips())){
            return new ListObjServiceResult<>();
        }
        List<ElasticIpCloudPlatformModel.ElasticIpModel> floatingips = model.getFloatingips();
        List<ElasticIpResponse> list = floatingips.stream().map(elasticIpModel ->
              ElasticIpResponse.builder().floatIpAddress(elasticIpModel.getFloating_ip_address())
                      .fixedIpAddress(elasticIpModel.getFixed_ip_address())
                      .status(CloudPlatformEnum.fromValue(elasticIpModel.getStatus()))
               .createdAt(elasticIpModel.getCreated_at()).build()
        ).collect(Collectors.toList());
        return new ListObjServiceResult(list, model.getTotal());
    }

}
