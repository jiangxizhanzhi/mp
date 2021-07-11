package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.DynamipsConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.DynamipsInstancePageRequest;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import cn.cyberpeace.mp.devopscluster.vo.InstancePageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/dynamips")
@Api(tags = "计算实例管理-dynamips")
@RequiredArgsConstructor
public class DynamipsController {

    private final DynamipsConverter dynamipsConverter;

    private final CsceneSpecialService csceneSpecialService;

    private final UserService userService;

    private final DeviceTargetMapper deviceTargetMapper;

    
    @ApiOperation("分页查询dynamips信息")
    @GetMapping("/page")
    public PageResult<InstancePageVO> page(DynamipsInstancePageRequest request) {
        var result = dynamipsConverter.page(request);
        var pageVos = convertBOToPageVO(result);
        return new PageResult<>(pageVos, result.getTotal());
    }

    
    private List<InstancePageVO> convertBOToPageVO(BasePageBO<InstanceBO> model) {
        List<InstanceBO> instances = model.getList();
        List<String> instanceNames = instances.stream().map(InstanceBO::getName).collect(Collectors.toList());
        List<UserBo> userBoList = new ArrayList<>();
        Map<String, String> uuid2UserId = new HashMap<>();
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(instanceNames);
        var devopsResult = deviceTargetMapper.findByUuids(instanceNames);
        POLogoUtils.changeLogoPathToUrl(devopsResult);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();
        if (Objects.nonNull(source)) {
            List<CsceneNodeInfoBO> values = new ArrayList<>(source.values());
            if (CollectionUtils.isNotEmpty(values)) {
                List<String> userIdList = values.stream().map(CsceneNodeInfoBO::getUserId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    userBoList = userService.listByUserIds(userIdList.toArray(new String[]{}));
                }
            }
        }
        
        if (CollectionUtils.isNotEmpty(devopsResult)) {
            List<String> userIdList = devopsResult.stream().filter(deviceTargetPO -> StringUtils.isNotEmpty(deviceTargetPO.getInstanceCreateUser())).map(DeviceTargetPO::getInstanceCreateUser).collect(Collectors.toList());
            uuid2UserId = devopsResult.stream().filter(deviceTargetPO -> StringUtils.isNotEmpty(deviceTargetPO.getInstanceCreateUser())).collect(Collectors.toMap(DeviceTargetPO::getUuid, DeviceTargetPO::getInstanceCreateUser));
            if (CollectionUtils.isNotEmpty(userIdList)) {
                userBoList.addAll(userService.listByUserIds(userIdList.toArray(new String[]{})));
            }
        }
        List<UserBo> tmpUserBoList = userBoList;
        Map<String, String> uuid2User = uuid2UserId;
        return instances.stream().map(instanceModel -> {
            CsceneNodeInfoBO csceneNodeInfoBO = new CsceneNodeInfoBO();
            if (Objects.nonNull(source.get(instanceModel.getName()))) {
                csceneNodeInfoBO = source.get(instanceModel.getName());
            }
            String userName = "";
            String userId = csceneNodeInfoBO.getUserId();
            if (StringUtils.isNotBlank(userId)) {
                Optional<UserBo> optional = tmpUserBoList.stream().filter(userDto -> userDto.getId().equals(userId)).findFirst();
                if (optional.isPresent()) {
                    userName = optional.get().getUsername();
                }
            } else if (StringUtils.isNotEmpty(uuid2User.get(instanceModel.getName()))) {
                String createUser = uuid2User.get(instanceModel.getName());
                Optional<UserBo> optional = tmpUserBoList.stream().filter(userDto -> userDto.getId().equals(createUser)).findFirst();
                if (optional.isPresent()) {
                    userName = optional.get().getUsername();
                }
            }

            return InstancePageVO.builder().name(instanceModel.getName()).sceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName()).id(instanceModel.getId()).node(instanceModel.getNode()).addressList(instanceModel.getAddressList()).userName(userName).referenceType(csceneNodeInfoBO.getReferenceType()).event(csceneNodeInfoBO.getEvent()).eventStatus(csceneNodeInfoBO.getEventStatus()).sceneStatus(csceneNodeInfoBO.getSceneStatus()).sceneStatusDesc(csceneNodeInfoBO.getSceneStatusDesc()).runningTime(instanceModel.getRunningTime()).processMessage(csceneNodeInfoBO.getProcessMessage()).status(instanceModel.getStatus()).createTime(instanceModel.getCreateTime()).build();
        }).collect(Collectors.toList());
    }
}
