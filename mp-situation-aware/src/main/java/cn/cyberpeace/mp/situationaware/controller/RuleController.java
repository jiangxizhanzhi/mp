package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.rule.Rule;
import cn.cyberpeace.mp.situationaware.service.wazuh.RuleService;
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
@RequestMapping("/wazuh/rules")
@Api(value = "探针管理", tags = "探针管理")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @GetMapping
    public ListObjServiceResult<Rule> pageRules(BasePageRequest pageRequest) {
        pageRequest.ensure();
        return ruleService.pageRules(pageRequest);
    }

}
