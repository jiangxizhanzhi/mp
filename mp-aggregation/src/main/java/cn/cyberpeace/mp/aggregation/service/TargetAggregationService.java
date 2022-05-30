package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.TargetPageModel;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.po.TargetPO;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TargetAggregationService {

    private CsceneSpecialService csceneSpecialService;

    private TargetConverter targetConverter;

    private DeviceTargetMapper deviceTargetMapper;

    @Autowired
    public TargetAggregationService(CsceneSpecialService csceneSpecialService, TargetConverter targetConverter,
                                    DeviceTargetMapper deviceTargetMapper) {
        this.csceneSpecialService = csceneSpecialService;
        this.targetConverter = targetConverter;
        this.deviceTargetMapper = deviceTargetMapper;
    }

    /**
     * 更新实装的引用以及使用状态
     */
    public void updateReferenceAndUseStatus(){
        TargetSceneRequest request = new TargetSceneRequest();
        request.setImageType(ImageTypeEnum.ENTITY);
        request.setIgnoreUser(Boolean.TRUE);
        request.setPerPage(null);
        request.setPage(null);
        request.setIsHide(Boolean.TRUE);
        TargetPageModel targetPageModel = targetConverter.page(request);
        if (Objects.isNull(targetPageModel) || CollectionUtils.isEmpty(targetPageModel.getTargetPOS())){
            return;
        }
        List<TargetPO> targetPOS =  targetPageModel.getTargetPOS();
        String[] imageNames = targetPOS.stream().map(TargetPO::getName).toArray(String[]::new);

        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>>  useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageNames);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        String name;
        List<TargetPO> modelList = new ArrayList<>();
        List<DeviceReferCsceneStat> referStateList = new ArrayList<>();
        List<DeviceReferCsceneInstanceStat> useStateList = new ArrayList<>();
        for (TargetPO targetPO : targetPOS){
            name = targetPO.getName();
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
            TargetPO model = new TargetPO();
            model.setUuid(targetPO.getUuid());
            model.setReferenceStatus(CollectionUtils.isEmpty(referStateList) ? 0 : referStateList.size());
            model.setUseStatus(CollectionUtils.isEmpty(useStateList) ? StatusEnum.NO : StatusEnum.YES);
            modelList.add(model);
        }
        if (CollectionUtils.isEmpty(modelList)){
            return;
        }
        deviceTargetMapper.updateBatch(modelList);
    }
}
