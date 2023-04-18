package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.cscene.constant.CsceneConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.service
 * @Author: hhuang
 * @CreateTime: 2023/04/18  11:06
 * @Description: 靶标实例分类
 * @Version: 1.0
 */
@Slf4j
@Service
public class CsceneInstanceTypeService {

    public List<String> instanceTypes = new ArrayList<>();

    @Resource(name = "optionServiceImpl")
    private OptionService optionService;


    /**
     * @description: 初始化所有靶标实例分类
     * @throws
     * @return void
     */
    private List<String> initInstanceTypes(){
        if(CollectionUtils.isNotEmpty(instanceTypes)){
            return instanceTypes;
        }
        List<OptionModel> optionModelList = optionService.findByType(CsceneConstant.MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_TYPE);
        instanceTypes.addAll(optionModelList.stream().map(OptionModel::getCode).collect(
            Collectors.toList()));
        return instanceTypes;
    }

    /**
     * @description: 获取初始加载实例
     * @throws
     * @return List<String>
     */
    public List<String> getOptionService() {
        return initInstanceTypes();
    }
}
