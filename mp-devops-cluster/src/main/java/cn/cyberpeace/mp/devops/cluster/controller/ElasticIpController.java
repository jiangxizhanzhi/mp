package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.ElasticIpResponse;
import cn.cyberpeace.mp.devops.cluster.service.ElasticIpService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 弹性IP管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/elasticip")
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
    @PostMapping("/page")
    public ListObjServiceResult<ElasticIpResponse> page(@RequestBody ElasticIpPageRequest request){
        ElasticIpOpenStackModel model = elasticIpService.page(request);
        List<ElasticIpOpenStackModel.ElasticIpModel> floatingips = model.getFloatingips();
        if (CollectionUtils.isEmpty(floatingips)){
            return new ListObjServiceResult<>();
        }
        List<ElasticIpResponse> list = floatingips.stream().map(elasticIpModel ->
              ElasticIpResponse.builder().floatIpAddress(elasticIpModel.getFloating_ip_address())
                      .fixedIpAddress(elasticIpModel.getFixed_ip_address()).status(elasticIpModel.getStatus())
               .createdAt(elasticIpModel.getCreated_at()).build()
        ).collect(Collectors.toList());
        return new ListObjServiceResult(new ListObjResult(list, model.getTotal()).asResult()).asResult();
    }

}
