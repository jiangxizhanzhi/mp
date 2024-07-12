 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FirewallConverter.java.
 // File size 2081.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Firewall;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.FirewallService;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @ClassName FirewallConverter
 * @Description 防火墙
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseConverter
 */
@Service
@RequiredArgsConstructor
public class FirewallConverter implements BaseConverter{

    private final FirewallService firewallService;

    /**
     * 分页查询
     * @param dto
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<Firewall> page(BasePageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
        CloudPlatformPageResult<Firewall> model = firewallService.page(getParams);
        BasePageBO<Firewall> convertModel = new BasePageBO<>();
        convertModel.setList(model.getList());
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取防火墙总数
     * @return
     */
    public Integer getTotalFirewall(BasePageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<Firewall> page = this.page(dto, this.getTotalPageIgnoreField());
        return page.getTotal();
    }
}
