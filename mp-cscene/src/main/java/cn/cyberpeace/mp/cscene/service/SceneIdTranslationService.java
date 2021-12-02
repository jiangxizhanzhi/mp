package cn.cyberpeace.mp.cscene.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/2 14:05
 */
@Slf4j
@Service
public class SceneIdTranslationService {

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    public List<String> translateSceneIds(List<String> sceneIds) {
        List<String> translatedScendIds = new ArrayList<>();
        for (String sceneId : sceneIds) {
            String translatedSceneId = csceneInstanceService.getSceneId(sceneId);
            translatedScendIds.add(translatedSceneId);
        }
        return translatedScendIds;
    }

}
