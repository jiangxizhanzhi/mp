package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TargetConverter implements BaseConverter{

    private ImageMapper imageMapper;

    private DeviceService deviceService;

    private CsceneSpecialService csceneSpecialService;


    @Autowired
    public TargetConverter(ImageMapper imageMapper, DeviceService deviceService,
                               CsceneSpecialService csceneSpecialService) {
        this.imageMapper = imageMapper;
        this.deviceService = deviceService;
        this.csceneSpecialService = csceneSpecialService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return new String[0];
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return new String[0];
    }

    /**
     * 分页查询
     * @param request
     * @return
     */
    public List<TargetModel> page(TargetSceneRequest request) {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setTargetCategory(request.getTargetCategory());
        dto.setMainCategory(request.getCategory());
        dto.setSubCategory(request.getSecondCategory());
        dto.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
        dto.setName(request.getName());
        dto.setSearchPublicEnum(1);
        if (Objects.isNull(request.getIgnoreUser()) || !request.getIgnoreUser()) {
            dto.setOwner(Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : null);
        }
        if (Objects.isNull(request.getImageType())){
            dto.setSearchType(0);
        }
        Integer total = imageMapper.totalCount(dto);
        if (total == 0){
            return Lists.newArrayList();
        }
        return imageMapper.page(dto);
    }

    /**
     * 查询所有靶标数据
     * @param request
     * @return
     */
    public List<Device> allTarget(TargetSceneRequest request) {
        List<TargetModel> targetModels = this.page(request);

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            return Lists.newArrayList();
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return Lists.newArrayList();
        }

        return model.getItems();
    }

    /**
     * 验证靶标是否可删除
     * @param uuids
     */
    public void validTargetCanDelete(List<String> uuids){

        List<TargetModel> targetModels = imageMapper.findByUuids(uuids);
        String[] imageNames = targetModels.stream().map(TargetModel::getName).toArray(String[]::new);
        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(referStateMap)){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_REFERENCED_SCENE);
        }
        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>>  useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageNames);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_USED_SCENE);
        }
    }

}
