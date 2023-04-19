package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.decoder.Decoder;
import cn.cyberpeace.mp.situationaware.service.wazuh.DecoderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/4/18 20:04
 */
@RestController
@RequestMapping("/wazuh/decoders")
@Api(value = "探针管理", tags = "探针管理")
public class DecoderController {

    @Autowired
    private DecoderService decoderService;

    @GetMapping
    public ListObjServiceResult<Decoder> pageDecoders(BasePageRequest pageRequest) {
        pageRequest.ensure();
        return decoderService.pageDecoders(pageRequest);
    }

}
