package cn.cyberpeace.mp.log.service.impl;

import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.log.constant.LogConstant;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: cn.cyberpeace.mp.log.service.impl
 * @Author: hhuang
 * @CreateTime: 2023/04/18  10:28
 * @Description: 日志分类
 * @Version: 1.0
 */
@Slf4j
@Service
public class OpModuleService {

    public List<Integer> OpModules = new ArrayList<>();

    @Resource(name = "optionServiceImpl")
    private OptionService optionService;

    /**
     * @description: 初始化所有操作模块
     * @throws
     * @return void
     */
    private List<Integer> initOpModules(){
        if(CollectionUtils.isNotEmpty(OpModules)){
            return OpModules;
        }
        List<OptionModel> optionModelList = optionService.findByType(LogConstant.MP_COMMON_OPTION_TYPE_LOG_OP_MODULE);
        if(CollectionUtils.isEmpty(optionModelList)){
            OpModules.addAll(Arrays.stream(OpModuleEnums.values()).map(m -> m.ordinal()).collect(
                Collectors.toList()));
            return OpModules;
        }
        OpModules.addAll(optionModelList.stream().map(optionModel -> Integer.valueOf(optionModel.getCode())).collect(Collectors.toList()));
        return OpModules;
    }

    public List<Integer> getOpModules(){
        return initOpModules();
    }
}
