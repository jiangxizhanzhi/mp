package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TargetAggregationService {

    private CsceneSpecialService csceneSpecialService;

    private TargetConverter targetConverter;

    private ImageMapper imageMapper;

    @Autowired
    public TargetAggregationService(CsceneSpecialService csceneSpecialService, TargetConverter targetConverter,
                                    ImageMapper imageMapper) {
        this.csceneSpecialService = csceneSpecialService;
        this.targetConverter = targetConverter;
        this.imageMapper = imageMapper;
    }

    /**
     * 更新实装的引用以及使用状态
     */
    public void updateReferenceAndUseStatus(){
        TargetSceneRequest request = new TargetSceneRequest();
        request.setImageType(ImageTypeEnum.ENTITY);
        request.setIgnoreUser(Boolean.TRUE);
        List<TargetModel> targetModels = targetConverter.page(request);
        if (CollectionUtils.isEmpty(targetModels)){
            return;
        }
        String[] imageNames = targetModels.stream().map(TargetModel::getName).toArray(String[]::new);

        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>>  useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageNames);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        String name;
        List<TargetModel> modelList = new ArrayList<>();
        List<DeviceReferCsceneStat> referStateList = new ArrayList<>();
        List<DeviceReferCsceneInstanceStat> useStateList = new ArrayList<>();
        for (TargetModel targetModel : targetModels){
            name = targetModel.getName();
            if (MapUtils.isNotEmpty(referStateMap)){
                referStateList = referStateMap.get(name);
            }else{
                referStateList = Lists.newArrayList();
            }
            if (MapUtils.isNotEmpty(useStateMap)){
                useStateList = useStateMap.get(name);
            }else{
                useStateList = Lists.newArrayList();
            }
            TargetModel model = new TargetModel();
            model.setUuid(targetModel.getUuid());
            model.setReferenceStatus(CollectionUtils.isEmpty(referStateList) ? 0 : referStateList.size());
            model.setUseStatus(CollectionUtils.isEmpty(useStateList) ? StatusEnum.NO : StatusEnum.YES);
            modelList.add(model);
        }
        if (CollectionUtils.isEmpty(modelList)){
            return;
        }
        imageMapper.updateBatch(modelList);
    }
}
