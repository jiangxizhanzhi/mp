package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.NpcService;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.NpcModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NpcConverter implements BaseConverter{

    private final NpcService npcService;

    
    public Integer getTotalNpcs(BasePageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,this.getTotalPageIgnoreField());
        CloudPlatformPageResult<NpcModel> page = npcService.page(getParams);
        return page.getTotal();
    }
}
