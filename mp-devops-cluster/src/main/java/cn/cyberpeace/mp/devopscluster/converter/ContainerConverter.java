 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerConverter.java.
 // File size 4823.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ContainerService;
import cn.cyberpeace.mp.devopscluster.converter.bo.ContainerBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ContainerPageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.ContainerModel;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName ContainerConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseConverter
 */
@Service
@RequiredArgsConstructor
public class ContainerConverter implements BaseConverter {

    private final ContainerService containerService;

    /**
     * 分页查询容器列表
     * @param dto
     * @param ignoreFields
     * @return
     */
    public BasePageBO<ContainerBO> page(ContainerPageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
        CloudPlatformPageResult<ContainerModel> model = containerService.page(getParams);
        BasePageBO<ContainerBO> convertModel = new BasePageBO();
        convertModel.setList(this.convertContainerModelList(model.getList()));
        convertModel.setTotal(Objects.isNull(model.getTotal()) ? NumberUtils.INTEGER_ZERO : model.getTotal());
        return convertModel;
    }

    /**
     * 获取容器总数量
     * @param dto
     * @return
     */
    public Integer getTotalContainers(ContainerPageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<ContainerBO> page = this.page(dto,this.getTotalPageIgnoreField());
        return page.getTotal();
    }

    /**
     * 转换List<ContainerModel>集合对象
     * @param containers
     * @return
     */
    private List<ContainerBO> convertContainerModelList(List<ContainerModel> containers){
        if (CollectionUtils.isEmpty(containers)){
            return Lists.newArrayList();
        }
        List<ContainerBO> list = new ArrayList<>();
        for (ContainerModel containerModel : containers) {
            ContainerBO containerBO = getContainerBO(containerModel);
            list.add(containerBO);
        }
        return list;
    }

    /**
     * getContainerBO
     *
     * @param containerModel
     * @return
     */
    private static ContainerBO getContainerBO(ContainerModel containerModel) {
        ContainerBO response = ContainerBO.builder()
                    .id(containerModel.getId())
                    .name(containerModel.getName()).image(containerModel.getImage())
                    .node(containerModel.getHost())
                    .status(containerModel.getStatus())
                    .createdAt(containerModel.getCreatedAt())
                    .runningTime(containerModel.getRunningTime())
                    .build();
        Map<String, Object> addresses = containerModel.getAddresses();
        if (MapUtils.isNotEmpty(addresses)) {
            List<ContainerModel.Address> addressList = new ArrayList<>();
            for (Object o : addresses.values()) {
                List<ContainerModel.Address> parseArray = JSONArray.parseArray(JSONArray.toJSONString(o), ContainerModel.Address.class);
                for (ContainerModel.Address address1 : parseArray) {
                    addressList.add(address1);
                }
            }
            List<String> result = new ArrayList<>();
            for (ContainerModel.Address address : addressList) {
                String addr = address.getAddr();
                result.add(addr);
            }
            response.setAddressList(result);
            List<Integer> list = new ArrayList<>();
            for (ContainerModel.Address v : addressList) {
                if (Objects.nonNull(v.getVlanId())) {
                    Integer vlanId = v.getVlanId();
                    list.add(vlanId);
                }
            }
            response.setVlanIdList(list);
        }
        return response;
    }
}
