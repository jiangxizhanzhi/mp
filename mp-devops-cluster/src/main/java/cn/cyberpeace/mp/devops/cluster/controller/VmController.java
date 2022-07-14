package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.VmConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.InstancePageVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(ControllerConstant.BASE_API_PATH + "/instance")
@Api(tags = "计算实例管理-虚拟机")
@RequiredArgsConstructor
public class VmController {

    private final VmConverter vmConverter;

    private final CsceneSpecialService csceneSpecialService;

    private final UserService userService;

    /**
     * 分页查询虚拟机信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/page")
    public PageResult<InstancePageVO> page(InstancePageRequest request){
        BasePageBO<InstanceBO> model = vmConverter.page(new InstancePageDTO(request));
        List<InstancePageVO> list = convertBOToPageVO(model);
        return new PageResult<>(list, model.getTotal());
    }

    /**
     * 转换BO to VO
     * @param model
     * @return
     */
    private List<InstancePageVO> convertBOToPageVO(BasePageBO<InstanceBO> model){
        List<InstanceBO> instances = model.getList();
        List<String> instanceNames = instances.stream().map(InstanceBO::getName).collect(Collectors.toList());
        List<String> userIdList = instances.stream().map(InstanceBO::getUserId).distinct().collect(Collectors.toList());
        List<UserDto> userDtoList = userService.listByUserIds(userIdList.toArray(new String[]{}));

        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(instanceNames);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();
        return instances.stream().map(instanceModel -> {
            CsceneNodeInfoBO csceneNodeInfoBO = new CsceneNodeInfoBO();
            if (Objects.nonNull(source.get(instanceModel.getName()))) {
                csceneNodeInfoBO = source.get(instanceModel.getName());
            }
            String userName = "";
            Optional<UserDto> optional =
                    userDtoList.stream().filter(userDto -> userDto.getId().equals(instanceModel.getUserId())).findFirst();
            if (optional.isPresent()) {
                userName = optional.get().getUsername();
            }
            return InstancePageVO.builder().name(instanceModel.getName())
                    .sceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName())
                    .node(instanceModel.getNode())
                    .addressList(instanceModel.getAddressList())
                    .userName(userName)
                    .referenceType(csceneNodeInfoBO.getReferenceType())
                    .event(csceneNodeInfoBO.getEvent())
                    .eventStatus(csceneNodeInfoBO.getEventStatus())
                    .runningTime(instanceModel.getRunningTime())
                    .status(instanceModel.getStatus()).createTime(instanceModel.getCreateTime()).build();
        }).collect(Collectors.toList());
    }
}
