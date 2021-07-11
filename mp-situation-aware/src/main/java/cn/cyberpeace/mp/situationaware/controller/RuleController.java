package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.rule.Rule;
import cn.cyberpeace.mp.situationaware.service.wazuh.RuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wazuh/rules")
@Api(value = "探针采集规则管理", tags = "探针采集规则管理")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @ApiOperation(value = "分页查询规则信息", notes = "")
    @GetMapping
    public ListObjServiceResult<Rule> pageRules(BasePageRequest pageRequest) {
        pageRequest.ensure();
        return ruleService.pageRules(pageRequest);
    }

}
