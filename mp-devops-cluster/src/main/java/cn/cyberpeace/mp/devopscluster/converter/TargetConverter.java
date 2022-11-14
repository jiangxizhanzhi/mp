package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.bo.DeviceAsyncBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * @param dto
     * @return
     */
    public BasePageBO<DeviceTargetPO> page(ImagePageDTO dto) {
        Integer total = devopsTargetService.totalCount(dto);
        BasePageBO<DeviceTargetPO> targetPageModel = new BasePageBO<>();
        targetPageModel.setTotal(total);
        if (total == 0){
            targetPageModel.setList(Lists.newArrayList());
            return targetPageModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);
        POLogoUtils.changeLogoPathToUrl(deviceTargetPOS);
        targetPageModel.setList(deviceTargetPOS);
        return targetPageModel;
    }

    /**
     * 查询所有靶标数据
     * @param dto
     * @return
     */
    public BasePageBO<Device> allTarget(ImagePageDTO dto) {
        BasePageBO<DeviceTargetPO> targetPageModel = this.page(dto);

        List<DeviceTargetPO> deviceTargetPOS = targetPageModel.getList();

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());

        BasePageBO<Device> sceneConvertModel = new BasePageBO<>();
        sceneConvertModel.setTotal(targetPageModel.getTotal());
        if (CollectionUtils.isEmpty(ids)){
            sceneConvertModel.setList(Lists.newArrayList());
            return sceneConvertModel;
        }
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            sceneConvertModel.setList(Lists.newArrayList());
            return sceneConvertModel;
        }
        POLogoUtils.changeLogoPathToUrl(model.getList());
        sceneConvertModel.setList(model.getList());
        return sceneConvertModel;
    }

    /**
     * 查询匹配的镜像名称
     * @param dto
     * @return
     */
    public List<String> findByImagePageDTO(ImagePageDTO dto){
        BasePageBO<DeviceTargetPO> targetPageModel = this.page(dto);
        return targetPageModel.getList().stream().map(DeviceTargetPO::getName).collect(Collectors.toList());
    }

    /**
     * 查询常用靶标信息,支持通过名称查询
     * @return
     */
    public List<Device> getCommonTargets(String name) {
        CloudPlatformPageResult<Device> model = deviceService.page("?limit=9999&is_internal=true&name=" + name);
        List<Device> deviceList = model.getList();
        POLogoUtils.changeLogoPathToUrl(deviceList);
        return deviceList;
    }

    /**
     * 验证靶标是否可删除
     * @param uuids
     */
    public void validTargetCanDelete(List<String> uuids) {

        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        deviceTargetPOS.forEach(deviceTargetPO -> {
            if (Boolean.TRUE.equals(deviceTargetPO.getIsInit())) {
                throw new MpRuntimeException(DevopsClusterErrorCode.INIT_TARGET_CAN_NOT_DELETE.format(deviceTargetPO.getName()));
            }
        });
        String[] imageNames = deviceTargetPOS.stream().map(DeviceTargetPO::getName).toArray(String[]::new);
        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        List<String> sceneNames = new ArrayList<>();
        String sceneName = StringUtils.EMPTY;

        if (MapUtils.isNotEmpty(referStateMap)) {
            referStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                        .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                sceneName = sceneNames.get(0);
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_REFERENCED_SCENE.format(sceneName));
        }
        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageNames);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)) {
            useStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneInstanceStat -> StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName()))
                        .map(deviceReferCsceneInstanceStat -> deviceReferCsceneInstanceStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                sceneName = sceneNames.get(0);
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_USED_SCENE.format(sceneName));
        }
    }


    /**
     * 获取需同步的靶标ID集合
     * @param deviceIds
     * @return DeviceAsyncBO
     */
    public DeviceAsyncBO getAsyncDeviceIds(List<Integer> deviceIds){
        DeviceAsyncBO bo = new DeviceAsyncBO();
        if (CollectionUtils.isEmpty(deviceIds)){
            return new DeviceAsyncBO();
        }
        List<String> ids = deviceIds.stream().map(String::valueOf).collect(Collectors.toList());
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByIds(ids);
        List<Integer> notExistedList;
        List<Integer> existedList;
        if (CollectionUtils.isEmpty(deviceTargetPOS)){
            notExistedList = deviceIds;
            existedList = Lists.newArrayList();
        }else{
            List<Integer> targetIds = deviceTargetPOS.stream().map(v -> Integer.parseInt(v.getId())).collect(Collectors.toList());
            notExistedList = deviceIds.stream().filter(v -> !targetIds.contains(v)).collect(Collectors.toList());
            existedList = deviceIds.stream().filter(v -> targetIds.contains(v)).collect(Collectors.toList());
        }
        bo.setNotExistedList(notExistedList);
        bo.setExistedList(existedList);
        return bo;
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

    /**
     * 根据UUID查询靶标信息
     * @param uuid
     * @return
     */
    public Device findByUuid(String uuid){
        Device device = deviceService.findByUuid(uuid);
        device.setLogo(DefaultImgUtil.pathToFdfsUrl(device.getLogo()));
        return device;
    }
}
