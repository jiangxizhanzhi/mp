package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devops.cluster.converter.model.TargetPageModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.TargetSceneConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
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

/**
 * 靶标
 * @author wanggang
 */
@Service
public class TargetConverter{

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

    /**
     * 分页查询
     * @param request
     * @return
     */
    public TargetPageModel page(TargetSceneRequest request) {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setTargetCategory(request.getTargetCategory());
        dto.setMainCategory(request.getCategory());
        dto.setSubCategory(request.getSecondCategory());
        dto.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
        dto.setName(request.getName());
        dto.setSearchPublicEnum(1);
        if (Objects.nonNull(request.getPage()) && Objects.nonNull(request.getPerPage())) {
            dto.setOffset((request.getPage() - 1) * request.getPerPage());
            dto.setLimit(request.getPerPage());
        }
        if (Objects.isNull(request.getIgnoreUser()) || !request.getIgnoreUser()) {
            dto.setOwner(Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : null);
        }
        if (Objects.isNull(request.getImageType())){
            dto.setSearchType(0);
        }
        if (request.getLikeName() != null) {
            dto.setLikeName(request.getLikeName());
        }
        Integer total = imageMapper.totalCount(dto);
        TargetPageModel targetPageModel = new TargetPageModel();
        targetPageModel.setTotal(total);
        if (total == 0){
            targetPageModel.setTargetModels(Lists.newArrayList());
            return targetPageModel;
        }
        List<TargetModel> targetModels = imageMapper.page(dto);
        targetPageModel.setTargetModels(targetModels);
        return targetPageModel;
    }

    /**
     * 查询所有靶标数据
     * @param request
     * @return
     */
    public TargetSceneConvertModel allTarget(TargetSceneRequest request) {
       TargetPageModel targetPageModel = this.page(request);

        List<TargetModel> targetModels = targetPageModel.getTargetModels();

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());

        TargetSceneConvertModel sceneConvertModel = new TargetSceneConvertModel();
        sceneConvertModel.setTotal(targetPageModel.getTotal());
        if (CollectionUtils.isEmpty(ids)){
            sceneConvertModel.setDeviceList(Lists.newArrayList());
            return sceneConvertModel;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            sceneConvertModel.setDeviceList(Lists.newArrayList());
            return sceneConvertModel;
        }
        sceneConvertModel.setDeviceList(model.getItems());
        return sceneConvertModel;
    }

    /**
     * 查询常用靶标信息,支持通过名称查询
     * @return
     */
    public List<Device> getCommonTargets(String name) {
        DeviceCloudPlatformModel model = deviceService.page("?limit=9999&is_internal=true&name=" + name);
        if (Objects.isNull(model) || !model.isSuccess()){
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

    /**
     * 获取需同步的靶标ID集合
     * @param deviceIds
     * @return
     */
    public List<Integer> getAsyncDeviceIds(List<Integer> deviceIds){
        if (CollectionUtils.isEmpty(deviceIds)){
            return Lists.newArrayList();
        }
        List<String> ids = deviceIds.stream().map(String::valueOf).collect(Collectors.toList());
        List<TargetModel> targetModels = imageMapper.findByIds(ids);
        List<Integer> diffList;
        if (CollectionUtils.isEmpty(targetModels)){
            diffList = deviceIds;
        }else{
            List<Integer> targetIds = targetModels.stream().map(v -> Integer.parseInt(v.getId())).collect(Collectors.toList());
            diffList = deviceIds.stream().filter(v -> !targetIds.contains(v)).collect(Collectors.toList());
        }
        return diffList;
    }

    /**
     * 分页查询靶标数据
     * @param offset
     * @param limit
     * @return
     */
    public List<Device> multiAsync(Integer offset,Integer limit){
        DeviceCloudPlatformModel model = deviceService.page("?offset=" + offset + "&limit=" + limit);
        if (Objects.isNull(model) || !model.isSuccess()){
            return Lists.newArrayList();
        }
        return model.getItems();
    }

}
