package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.decoder.Decoder;
import cn.cyberpeace.mp.situationaware.service.wazuh.DecoderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wazuh/decoders")
@Api(value = "探针采集解析器管理", tags = "探针采集解析器管理")
public class DecoderController {

    @Autowired
    private DecoderService decoderService;

    @ApiOperation(value = "分页查询解析器信息", notes = "")
    @GetMapping
    public ListObjServiceResult<Decoder> pageDecoders(BasePageRequest pageRequest) {
        pageRequest.ensure();
        return decoderService.pageDecoders(pageRequest);
    }

}
