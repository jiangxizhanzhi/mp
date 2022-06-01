package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NpcService;
import cn.cyberpeace.mp.devops.cluster.converter.model.NpcPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.NpcModel;
import cn.cyberpeace.mp.devops.cluster.request.NpcPageRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 网络管理
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class NpcConverter implements BaseConverter{

    private final NpcService npcService;

    /**
     * 分页查询NPC
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public NpcPageConverterModel page(NpcPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        CloudPlatformPageResult<NpcModel> model = npcService.page(getParams);
        NpcPageConverterModel convertModel = new NpcPageConverterModel();
        convertModel.setModelList(model.getList());
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取NPC总数
     * @return
     */
    public Integer getTotalNpcs(NpcPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        CloudPlatformPageResult<NpcModel> page = npcService.page(getParams);
        return page.getTotal();
    }
}
