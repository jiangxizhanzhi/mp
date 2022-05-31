package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ElasticIpConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ElasticIpConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import cn.cyberpeace.mp.devops.cluster.response.ElasticIpVO;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(tags = "网络实例管理-弹性IP")
@RequiredArgsConstructor
public class ElasticIpController {

    private final ElasticIpConverter elasticIpConverter;

    /**
     * 分页查询弹性IP信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询弹性IP信息")
    @GetMapping("/page")
    public PageResult<ElasticIpVO> page(ElasticIpPageRequest request){
        ElasticIpConvertModel model = elasticIpConverter.page(request);
        List<ElasticIpVO> list = model.getModelList().stream().map(elasticIpModel ->
              ElasticIpVO.builder().floatIpAddress(elasticIpModel.getFloatIpAddress())
                      .fixedIpAddress(elasticIpModel.getFixedIpAddress())
                      .status(elasticIpModel.getStatus())
               .createdAt(elasticIpModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        return new PageResult<>(list, model.getTotal());
    }

}
