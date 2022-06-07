package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NpcService;
import cn.cyberpeace.mp.devops.cluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.NpcModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

/**
 * 网络管理
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class NpcConverter implements BaseConverter{

    private final NpcService npcService;

    /**
     * 获取NPC总数
     * @return
     */
    public Integer getTotalNpcs(BasePageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,this.getTotalPageIgnoreField());
        CloudPlatformPageResult<NpcModel> page = npcService.page(getParams);
        return page.getTotal();
    }
}
