package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.core.bo.FailSceneExportBO;
import cn.cyberpeace.mp.scene.core.bo.SceneExportBO;
import cn.cyberpeace.mp.scene.core.bo.SceneExportConditionBO;
import cn.cyberpeace.mp.scene.model.SceneInstanceStartHisModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SceneInstanceStartHisModel.class)
public interface SceneInstanceStartHisMapper extends BaseModelMapper<SceneInstanceStartHisModel> {

    /**
     * 查询场景统计数据
     * @return
     */
    List<SceneExportBO> selectSceneStat(SceneExportConditionBO sceneExportConditionBO);

    /**
     * 查询失败场景详情数据
     * @return
     */
    List<FailSceneExportBO> selectFailSceneDetail(SceneExportConditionBO sceneExportConditionBO);

}
