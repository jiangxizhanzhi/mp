 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkConverter.java.
 // File size 5058.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.CloudClient;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.NetworkPageParam;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Network;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkSubnetBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.NetworkPageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkModel;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName NetworkConverter
 * @Description 网络管理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseConverter
 */
@Service
@RequiredArgsConstructor
public class NetworkConverter implements BaseConverter{

    private final NetworkService networkService;

    private final CsceneSpecialService csceneSpecialService;

    private final CloudClient client;

    /**
     * page
     *
     * @param dto
     * @return
     */
    public ListObjServiceResult<Network> page(NetworkPageDTO dto) {
        NetworkPageParam param = new NetworkPageParam();
        param.setPage(dto.getOffset());
        param.setPerPage(dto.getLimit());
        param.setName(dto.getName());
        var result = client.networkPage(param);
        if (CollectionUtils.isEmpty(result.getObjs())) {
            return new ListObjServiceResult<>(result);
        }
        Map<String, String> name2ElementId = new HashMap<>();
        List<String> elementIds = result.getObjs().stream().map(network -> {
            String elementId;
            if (network.getName().startsWith("MP_SCENE_")) {
                elementId = StringUtils.substringBeforeLast(network.getName(), "_").replace("MP_SCENE_", "");
            } else {
                elementId = network.getName();
            }
            name2ElementId.put(network.getName(), elementId);
            return elementId;
        }).collect(Collectors.toList());
        var elementId2Scene = csceneSpecialService.statDeviceReferScenes(elementIds).getSource();
        result.getObjs().forEach(network -> {
            var elementId = name2ElementId.get(network.getName());
            var scene = elementId2Scene.get(elementId);
            if (Objects.nonNull(scene)) {
                network.setSceneName(scene.getName());
            } else {
                network.setSceneName("-");
            }
        });
        return new ListObjServiceResult<>(result);
    }

    /**
     * 分页查询网络信息
     * @param dto
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<NetworkBO> page(NetworkPageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
        CloudPlatformPageResult<NetworkModel> model = networkService.page(getParams);
        List<NetworkBO> list = model.getList().stream().map(networkModel -> {
            NetworkModel.Subnet subnet = networkModel.getSubnet();
            List<NetworkSubnetBO> subnets = Lists.newArrayList();
            if (Objects.nonNull(subnet)) {
                subnets = List.of(new NetworkSubnetBO(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp()));
            }
            return  NetworkBO.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                    .status(networkModel.getStatus())
                    .subnets(subnets).createdAt(networkModel.getCreatedAt()).build();
        }).collect(Collectors.toList());
        BasePageBO<NetworkBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }


    /**
     * 获取网络总数
     * @return
     */
    public Integer getTotalNetwork(NetworkPageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<NetworkBO> page = this.page(dto, this.getTotalPageIgnoreField());
        return page.getTotal();
    }

}