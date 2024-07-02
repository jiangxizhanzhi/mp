package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.MibInfoQueryRequest;
import cn.cyberpeace.mp.cscene.service.ProtocolCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ProtocolCollectionController
 * @Description 协议采集相关处理
 * @Author hhuang
 * @Date 2024/07/02
 * @Version 1.0
 */
@Api(value = "协议采集", tags = "协议采集")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/protocol/collection")
@AuthTarget(TargetEnum.CSCENE_PROTOCOL_GROUP)
public class ProtocolCollectionController {

    @Resource
    private ProtocolCollectionService protocolCollectionService;


//    @ApiOperation(value = "mib文件上传并解析")
//    @PostMapping("/mib/import")
//    public ServiceResult importMibFile(MultipartFile file) {
//        return protocolCollectionService.importMibFile(file);
//    }


    /**
     * queryMibNodeInfo
     *
     * @param queryRequest
     * @return 统一服务返回结果
     */
    @ApiOperation(value = "mib节点信息查询")
    @GetMapping("/mib_nodeinfo")
    public ServiceResult queryMibNodeInfo(@Validated MibInfoQueryRequest queryRequest) {
        return protocolCollectionService.queryMibOidInfo(queryRequest);
    }
}
