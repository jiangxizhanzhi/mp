package cn.cyberpeace.mp.scene.service.impl;

import cn.cyberpeace.mp.scene.domain.SceneConfigDO;
import cn.cyberpeace.mp.scene.repository.SceneConfigMapper;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.vo.SceneConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SceneConfigServiceImpl implements SceneConfigService {

    @Autowired
    private SceneConfigMapper sceneConfigMapper;

    @Override
    public List<SceneConfigVO> findAll() {
        List<SceneConfigVO> result = new ArrayList<>();
        for (SceneConfigDO sceneConfigDO : sceneConfigMapper.findAll()) {
            result.add(new SceneConfigVO(sceneConfigDO));
        }
        return result;
    }
}
