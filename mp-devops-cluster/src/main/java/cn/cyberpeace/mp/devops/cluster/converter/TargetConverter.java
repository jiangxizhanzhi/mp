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
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
@RequiredArgsConstructor
public class TargetConverter{

    private final DeviceTargetMapper deviceTargetMapper;

    private final DeviceService deviceService;

    private final CsceneSpecialService csceneSpecialService;

    private final DevopsTargetService devopsTargetService;

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
        dto.setThirdCategory(request.getThirdCategory());
        dto.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
        dto.setName(request.getName());
        dto.setSearchPublicEnum(1);
        dto.setRole(request.getRole());
        dto.setIsHide(request.getIsHide());
        dto.setSystemType(request.getSystemType());
        dto.setSystemArchitecure(request.getSystemArchitecure());
        dto.setQuickCreateSupport(request.getQuickCreateSupport());
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
        dto.setLanguage(request.getLanguage());
        dto.setImageStatus(request.getImageStatus());
        dto.setBaseImageSupport(request.getBaseImageSupport());
        Integer total = devopsTargetService.totalCount(dto);
        TargetPageModel targetPageModel = new TargetPageModel();
        targetPageModel.setTotal(total);
        if (total == 0){
            targetPageModel.setDeviceTargetPOS(Lists.newArrayList());
            return targetPageModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);
        targetPageModel.setDeviceTargetPOS(deviceTargetPOS);
        return targetPageModel;
    }

    /**
     * 查询所有靶标数据
     * @param request
     * @return
     */
    public TargetSceneConvertModel allTarget(TargetSceneRequest request) {
       TargetPageModel targetPageModel = this.page(request);

        List<DeviceTargetPO> deviceTargetPOS = targetPageModel.getDeviceTargetPOS();

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());

        TargetSceneConvertModel sceneConvertModel = new TargetSceneConvertModel();
        sceneConvertModel.setTotal(targetPageModel.getTotal());
        if (CollectionUtils.isEmpty(ids)){
            sceneConvertModel.setDeviceList(Lists.newArrayList());
            return sceneConvertModel;
        }
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            sceneConvertModel.setDeviceList(Lists.newArrayList());
            return sceneConvertModel;
        }
        sceneConvertModel.setDeviceList(model.getList());
        return sceneConvertModel;
    }

    /**
     * 查询常用靶标信息,支持通过名称查询
     * @return
     */
    public List<Device> getCommonTargets(String name) {
        CloudPlatformPageResult<Device> model = deviceService.page("?limit=9999&is_internal=true&name=" + name);
        return model.getList();
    }

    /**
     * 验证靶标是否可删除
     * @param uuids
     */
    public void validTargetCanDelete(List<String> uuids){

        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        String[] imageNames = deviceTargetPOS.stream().map(DeviceTargetPO::getName).toArray(String[]::new);
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
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByIds(ids);
        List<Integer> diffList;
        if (CollectionUtils.isEmpty(deviceTargetPOS)){
            diffList = deviceIds;
        }else{
            List<Integer> targetIds = deviceTargetPOS.stream().map(v -> Integer.parseInt(v.getId())).collect(Collectors.toList());
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
        CloudPlatformPageResult<Device> model = deviceService.page("?offset=" + offset + "&limit=" + limit);
        return model.getList();
    }

}
