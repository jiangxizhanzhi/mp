package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.scene.core.bo.FailSceneExportBO;
import cn.cyberpeace.mp.scene.core.bo.SceneExportBO;
import cn.cyberpeace.mp.scene.core.bo.SceneExportConditionBO;
import cn.cyberpeace.mp.scene.repository.SceneInstanceStartHisMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author qiaokang
 * @Date：2023/2/15 19:23
 */
@Slf4j
@Service
public class SceneInstanceStartHisService {

    @Resource
    private SceneInstanceStartHisMapper sceneInstanceStartHisMapper;

    /**
     * 查询场景统计数据
     * @return
     */
    public List<SceneExportBO> selectSceneExportStatistics(SceneExportConditionBO sceneExportConditionBO) {
        List<SceneExportBO> sceneExportBOList = sceneInstanceStartHisMapper.selectSceneStat(sceneExportConditionBO);
        if (CollectionUtils.isEmpty(sceneExportBOList)) {
            sceneExportBOList = new ArrayList<>();
        } else {
            sceneExportBOList.forEach(sceneExportBO -> {
                Long failCount = sceneExportBO.getFailCount();
                Long successCount = sceneExportBO.getSuccessCount();
                // 计算总数
                BigDecimal totalCount = BigDecimal.valueOf(successCount).add(BigDecimal.valueOf(failCount));

                // 计算成功率
                BigDecimal failRate;
                if (totalCount.equals(BigDecimal.ZERO)) {
                    failRate = BigDecimal.ZERO;
                } else {
                    failRate = BigDecimal.valueOf(failCount).divide(totalCount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                }

                // 保留2位小数
                failRate = failRate.setScale(2, RoundingMode.HALF_UP);
                sceneExportBO.setFailRate(failRate.toString() + "%");
            });
        }

        return sceneExportBOList;
    }

    /**
     * 查询失败场景详情数据
     * @return
     */
    public List<FailSceneExportBO> selectFailSceneDetail(SceneExportConditionBO sceneExportConditionBO) {
        List<FailSceneExportBO> failSceneExportBOList = sceneInstanceStartHisMapper.selectFailSceneDetail(sceneExportConditionBO);
        if (CollectionUtils.isEmpty(failSceneExportBOList)) {
            failSceneExportBOList = new ArrayList<>();
        }

        return failSceneExportBOList;
    }

}
