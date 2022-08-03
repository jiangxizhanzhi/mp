package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.ContainerConverter;
import cn.cyberpeace.mp.devops.cluster.converter.bo.ContainerBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.ContainerPageDTO;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.ContainerPageVO;
import cn.cyberpeace.mp.devops.cluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 容器管理类
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/container")
@Api(tags = "计算实例管理-容器")
@RequiredArgsConstructor
public class ContainerController {

    private final ContainerConverter containerConverter;

    private final CsceneSpecialService csceneSpecialService;

    private final UserService userService;

    /**
     * 分页查询容器列表
     * @param request
     * @return
     */
    @ApiOperation("分页查询容器列表")
    @GetMapping("/page")
    public PageResult<ContainerPageVO> page(ContainerPageRequest request){
        BasePageBO<ContainerBO> model = containerConverter.page(new ContainerPageDTO(request));
        List<ContainerPageVO> list = convertBOToPageVO(model);
        return new PageResult<>(list, model.getTotal());
    }

    /**
     * 转换BO to VO
     * @param model
     * @return
     */
    private List<ContainerPageVO> convertBOToPageVO(BasePageBO<ContainerBO> model){
        final List<ContainerBO> modelList = model.getList();
        List<String> nameList = modelList.stream().map(ContainerBO::getName).collect(Collectors.toList());
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(nameList);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();

        List<UserDto> userDtoList = new ArrayList<>();
        if (Objects.nonNull(source)) {
            List<CsceneNodeInfoBO> values = new ArrayList<>(source.values());
            if (CollectionUtils.isNotEmpty(values)) {
                List<String> userIdList = values.stream().map(CsceneNodeInfoBO::getUserId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    userDtoList = userService.listByUserIds(userIdList.toArray(new String[]{}));
                }
            }
        }

        List<UserDto> tmpUserDtoList = userDtoList;
        return modelList.stream().map(containerModel -> {
            CsceneNodeInfoBO csceneNodeInfoBO = new CsceneNodeInfoBO();
            if (Objects.nonNull(source.get(containerModel.getName()))) {
                csceneNodeInfoBO = source.get(containerModel.getName());
            }
            String userName = "";
            String userId = csceneNodeInfoBO.getUserId();
            if (StringUtils.isNotBlank(userId)) {
                Optional<UserDto> optional =
                        tmpUserDtoList.stream().filter(userDto -> userDto.getId().equals(userId)).findFirst();
                if (optional.isPresent()) {
                    userName = optional.get().getUsername();
                }
            }

            return ContainerPageVO.builder().name(containerModel.getName())
                    .sceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName())
                    .image(containerModel.getImage()).node(containerModel.getNode())
                    .status(containerModel.getStatus()).addressList(containerModel.getAddressList())
                    .createdAt(containerModel.getCreatedAt())
                    .userName(userName)
                    .referenceType(csceneNodeInfoBO.getReferenceType())
                    .event(csceneNodeInfoBO.getEvent())
                    .eventStatus(csceneNodeInfoBO.getEventStatus())
                    .sceneStatus(csceneNodeInfoBO.getSceneStatus())
                    .sceneStatusDesc(csceneNodeInfoBO.getSceneStatusDesc())
                    .runningTime(containerModel.getRunningTime())
                    .vlanIdList(containerModel.getVlanIdList()).build();
        }).collect(Collectors.toList());
    }

}
