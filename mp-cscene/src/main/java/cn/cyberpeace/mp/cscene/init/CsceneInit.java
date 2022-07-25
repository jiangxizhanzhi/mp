package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.model.CsceneCommonConfigModel;
import cn.cyberpeace.mp.cscene.repository.CsceneCommonConfigMapper;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/11
 */
@Component
public class CsceneInit implements ApplicationRunner {

    @Resource
    private CsceneService csceneService;

    @Resource
    private CsceneCommonConfigMapper commonConfigMapper;

    @Override
    public void run(ApplicationArguments args) {
        int count = csceneService.getActiveVirtualDeviceCount();
        NodeLimitUtils.addLimitedNodeCount(count);

        var list = commonConfigMapper
                .list(new HumanCondition().filterEqual(CsceneCommonConfigModel::getKey, CloudMessageConvertUtil.configKey));
        if (list != null && list.size() > 0) {
            CloudMessageConvertUtil.init(list.get(0).getContent());
        }
    }
}
