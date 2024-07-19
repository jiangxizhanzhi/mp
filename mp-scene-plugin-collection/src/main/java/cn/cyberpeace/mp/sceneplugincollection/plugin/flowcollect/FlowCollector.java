 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowCollector.java.
 // File size 4704.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.flowcollect;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Mirror;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateMirrorData;
import cn.cyberpeace.mp.sceneplugincollection.bo.ArkimeCreateResBo;
import cn.cyberpeace.mp.sceneplugincollection.bo.DeleteFlowCollectBo;
import cn.cyberpeace.mp.sceneplugincollection.bo.FlowMirrorCreateRes;
import cn.cyberpeace.mp.sceneplugincollection.client.FlowCollectClient;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneOptionKeyModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneOptionKeyMapper;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @ClassName FlowCollector
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@Component
public class FlowCollector {
    private static final FlowCollectClient flowCollectClient = new FlowCollectClient();
    private static final Random RANDOM = new Random();

    /**
     * createCollector
     *
     * @return
     */
    public ArkimeCreateResBo createCollector() {
        return flowCollectClient.createArkimeInstance();
    }

    /**
     * createFlowMirror
     *
     * @param arkimeContainerId
     * @param sceneId
     * @param totalMirrorParams
     * @return
     */
    public FlowMirrorCreateRes createFlowMirror(String arkimeContainerId,String arkimeNicId, String sceneId, List<CreateMirrorData.Param> totalMirrorParams) {
        CreateMirrorData createData = new CreateMirrorData();
        createData.setSrcParam(totalMirrorParams);

        CreateMirrorData.Param destParam = new CreateMirrorData.Param();
        destParam.setId(arkimeContainerId);
        destParam.setDesId(arkimeNicId);
        destParam.setType(CreateMirrorData.Type.DOCKER);
        createData.setDestParam(destParam);

        CollectionPluginSceneOptionKeyMapper optionKeyMapper = BeanCommon.getBean(CollectionPluginSceneOptionKeyMapper.class);
        List<Integer> excludeOptionKey = optionKeyMapper.list(new HumanCondition())
                .stream()
                .map(CollectionPluginSceneOptionKeyModel::getOptionKey)
                .collect(Collectors.toList());
        int optionKey = this.randomOptionKey(excludeOptionKey);

        CollectionPluginSceneOptionKeyModel optionKeyModel = new CollectionPluginSceneOptionKeyModel();
        optionKeyModel.setScene(sceneId);
        optionKeyModel.setOptionKey(optionKey);
        optionKeyMapper.create(optionKeyModel);

        createData.setName(TextUtils.uuid().substring(0, 10));
        createData.setOptionKey(optionKey);

        Mirror mirror = new Mirror();
        var result = mirror.create(createData);

        return new FlowMirrorCreateRes().setFlowMirrorId(result.getName()).setOptionKey(optionKey);

    }

    /**
     * deleteCollector
     *
     * @param deleteFlowCollectBo
     */
    public void deleteCollector(DeleteFlowCollectBo deleteFlowCollectBo) {
        if (StringUtils.isNotEmpty(deleteFlowCollectBo.getCollectorInstanceId())) {
            flowCollectClient.delArkimeInstance(deleteFlowCollectBo.getCollectorInstanceId());
        }
        if (StringUtils.isNotEmpty(deleteFlowCollectBo.getFlowMirrorId())) {
            flowCollectClient.delFlowMirror(deleteFlowCollectBo.getFlowMirrorId());
        }


        CollectionPluginSceneOptionKeyMapper optionKeyMapper = BeanCommon.getBean(CollectionPluginSceneOptionKeyMapper.class);
        List<CollectionPluginSceneOptionKeyModel> optionKeyModels = optionKeyMapper.list(new HumanCondition()
                .filterEqual(CollectionPluginSceneOptionKeyModel::getScene, deleteFlowCollectBo.getSceneId()));
        if (CollectionUtils.isEmpty(optionKeyModels)) {
            return;
        }
        optionKeyMapper.bulkDelete(optionKeyModels.stream()
                .map(CollectionPluginSceneOptionKeyModel::getId)
                .toArray(String[]::new));
    }


    /**
     * randomOptionKey
     *
     * @param excludeOptionKey
     * @return int
     */
    private int randomOptionKey(List<Integer> excludeOptionKey) {
        int optionKey = 1;
        for (int i = 0; i < 1; i++) {
            optionKey = RANDOM.nextInt(65535);
            if (excludeOptionKey.contains(optionKey)) {
                i--;
            }
        }
        return optionKey;
    }

}
