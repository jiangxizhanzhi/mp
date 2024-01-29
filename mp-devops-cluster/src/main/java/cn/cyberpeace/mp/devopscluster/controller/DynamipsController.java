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

/**
 * 虚拟机管理类
 */
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

    /**
     * 分页查询dynamips信息
     *
     * @param request
     * @return //
     */
    @ApiOperation("分页查询dynamips信息")
    @GetMapping("/page")
    public PageResult<InstancePageVO> page(DynamipsInstancePageRequest request) {
        BasePageBO<InstanceBO> result = dynamipsConverter.page(request);
        List<InstancePageVO> pageVos = convertBOToPageVO(result);
        return new PageResult<>(pageVos, result.getTotal());
    }

    /**
     * 转换BO to VO
     *
     * @param model
     * @return
     */
    private List<InstancePageVO> convertBOToPageVO(BasePageBO<InstanceBO> model) {
        List<InstanceBO> instances = model.getList();
        List<String> instanceNames = new ArrayList<>();
        for (InstanceBO instance : instances) {
            String name = instance.getName();
            instanceNames.add(name);
        }
        List<UserBo> userBoList = new ArrayList<>();
        Map<String, String> uuid2UserId = new HashMap<>();
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(instanceNames);
        List<DeviceTargetPO> devopsResult = deviceTargetMapper.findByUuids(instanceNames);
        POLogoUtils.changeLogoPathToUrl(devopsResult);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();
        if (Objects.nonNull(source)) {
            List<CsceneNodeInfoBO> values = new ArrayList<>(source.values());
            if (CollectionUtils.isNotEmpty(values)) {
                List<String> userIdList = new ArrayList<>();
                for (CsceneNodeInfoBO value : values) {
                    String userId = value.getUserId();
                    userIdList.add(userId);
                }
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    userBoList = userService.listByUserIds(userIdList.toArray(new String[]{}));
                }
            }
        }
        // 镜像管理实例的创建用户
        if (CollectionUtils.isNotEmpty(devopsResult)) {
            List<String> userIdList = new ArrayList<>();
            for (DeviceTargetPO targetPO : devopsResult) {
                if (StringUtils.isNotEmpty(targetPO.getInstanceCreateUser())) {
                    String instanceCreateUser = targetPO.getInstanceCreateUser();
                    userIdList.add(instanceCreateUser);
                }
            }
            Map<String, String> map = new HashMap<>();
            for (DeviceTargetPO deviceTargetPO : devopsResult) {
                if (StringUtils.isNotEmpty(deviceTargetPO.getInstanceCreateUser())) {
                    if (map.put(deviceTargetPO.getUuid(), deviceTargetPO.getInstanceCreateUser()) != null) {
                        throw new IllegalStateException("Duplicate key");
                    }
                }
            }
            uuid2UserId = map;
            if (CollectionUtils.isNotEmpty(userIdList)) {
                userBoList.addAll(userService.listByUserIds(userIdList.toArray(new String[]{})));
            }
        }
        List<UserBo> tmpUserBoList = userBoList;
        Map<String, String> uuid2User = uuid2UserId;
        List<InstancePageVO> list = new ArrayList<>();
        for (InstanceBO instanceModel : instances) {
            InstancePageVO instancePageVO = getInstancePageVO(source, tmpUserBoList, uuid2User, instanceModel);
            list.add(instancePageVO);
        }
        return list;
    }

    private static InstancePageVO getInstancePageVO(Map<String, CsceneNodeInfoBO> source, List<UserBo> tmpUserBoList, Map<String, String> uuid2User, InstanceBO instanceModel) {
        CsceneNodeInfoBO csceneNodeInfoBO = new CsceneNodeInfoBO();
        if (Objects.nonNull(source.get(instanceModel.getName()))) {
            csceneNodeInfoBO = source.get(instanceModel.getName());
        }
        String userName = "";
        String userRealname = "";
        String userId = csceneNodeInfoBO.getUserId();
        if (StringUtils.isNotBlank(userId)) {
            Optional<UserBo> optional = Optional.empty();
            for (UserBo userDto : tmpUserBoList) {
                if (userDto.getId().equals(userId)) {
                    optional = Optional.of(userDto);
                    break;
                }
            }
            if (optional.isPresent()) {
                userName = optional.get().getUsername();
                userRealname = optional.get().getName();
            }
        } else if (StringUtils.isNotEmpty(uuid2User.get(instanceModel.getName()))) {
            String createUser = uuid2User.get(instanceModel.getName());
            Optional<UserBo> optional = Optional.empty();
            for (UserBo userDto : tmpUserBoList) {
                if (userDto.getId().equals(createUser)) {
                    optional = Optional.of(userDto);
                    break;
                }
            }
            if (optional.isPresent()) {
                userName = optional.get().getUsername();
                userRealname = optional.get().getName();
            }
        }

        return InstancePageVO.builder().name(instanceModel.getName()).sceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName()).id(instanceModel.getId()).node(instanceModel.getNode()).addressList(instanceModel.getAddressList()).userName(userName).userRealname(userRealname).referenceType(csceneNodeInfoBO.getReferenceType()).event(csceneNodeInfoBO.getEvent()).eventStatus(csceneNodeInfoBO.getEventStatus()).sceneStatus(csceneNodeInfoBO.getSceneStatus()).sceneStatusDesc(csceneNodeInfoBO.getSceneStatusDesc()).runningTime(instanceModel.getRunningTime()).processMessage(csceneNodeInfoBO.getProcessMessage()).status(instanceModel.getStatus()).createTime(instanceModel.getCreateTime()).build();
    }
}
