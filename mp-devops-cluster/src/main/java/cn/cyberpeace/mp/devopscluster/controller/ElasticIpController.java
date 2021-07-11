package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.ElasticIpConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.ElasticIpBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ElasticIpPageDTO;
import cn.cyberpeace.mp.devopscluster.request.ElasticIpPageRequest;
import cn.cyberpeace.mp.devopscluster.vo.ElasticIpVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/elasticip")
@Api(tags = "网络实例管理-弹性IP")
@RequiredArgsConstructor
public class ElasticIpController {

    private final ElasticIpConverter elasticIpConverter;

    
    @ApiOperation("分页查询弹性IP信息")
    @GetMapping("/page")
    public PageResult<ElasticIpVO> page(ElasticIpPageRequest request){
        BasePageBO<ElasticIpBO> model = elasticIpConverter.page(new ElasticIpPageDTO(request));
        List<ElasticIpVO> elasticIpList = convertBOToPageVO(model);

        return new PageResult<>(elasticIpList, model.getTotal());
    }

    
    private List<ElasticIpVO> convertBOToPageVO(BasePageBO<ElasticIpBO> model){
        return model.getList().stream().map(elasticIpModel ->
                ElasticIpVO.builder().floatIpAddress(elasticIpModel.getFloatIpAddress())
                        .fixedIpAddress(elasticIpModel.getFixedIpAddress())
                        .status(elasticIpModel.getStatus())
                        .createdAt(elasticIpModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
    }

}
