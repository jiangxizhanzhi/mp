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


@Service
@RequiredArgsConstructor
public class FirewallConverter implements BaseConverter{

    private final FirewallService firewallService;

    
    public BasePageBO<Firewall> page(BasePageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
        CloudPlatformPageResult<Firewall> model = firewallService.page(getParams);
        BasePageBO<Firewall> convertModel = new BasePageBO<>();
        convertModel.setList(model.getList());
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    
    public Integer getTotalFirewall(BasePageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<Firewall> page = this.page(dto, this.getTotalPageIgnoreField());
        return page.getTotal();
    }
}
