package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.model.CSceneElementCountModel;
import cn.cyberpeace.mp.cscene.repository.CSceneElementCountMapper;
import cn.cyberpeace.mp.cscene.service.CsceneService;
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
    private BaseAppConfig baseAppConfig;

    @Resource
    private CsceneService csceneService;

    @Resource
    private CSceneElementCountMapper countMapper;

    @Override
    public void run(ApplicationArguments args) {
        // 本地开发环境不运行资源的更新和删除
//        if (!baseAppConfig.isRunInit()) {
//            return;
//        }
        int count = csceneService.getActiveVirtualDeviceCount();
        CSceneElementCountModel countModel = countMapper.first(new HumanCondition());
        if (countModel == null) {
            countModel = new CSceneElementCountModel();
            countModel.setCount(count);
            countMapper.create(countModel);
        } else if (count != countModel.getCount()) {
            countModel.setCount(count);
            countMapper.update(countModel);
        }
    }
}
