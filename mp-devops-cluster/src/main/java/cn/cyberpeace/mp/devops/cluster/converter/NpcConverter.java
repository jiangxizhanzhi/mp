package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NpcService;
import cn.cyberpeace.mp.devops.cluster.converter.model.NpcPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.NpcCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.NpcPageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

/**
 * 网络管理
 * @author wanggang
 */
@Service
public class NpcConverter implements BaseConverter{

    private NpcService npcService;

    @Autowired
    public NpcConverter(NpcService npcService) {
        this.npcService = npcService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(NpcPageRequest::getPage,NpcPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(NpcPageRequest::getOffset,NpcPageRequest::getPage,NpcPageRequest::getPerPage);
    }

    /**
     * 分页查询NPC
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public NpcPageConverterModel page(NpcPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        NpcCloudPlatformModel model = npcService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getNpcModels())){
            return null;
        }
        NpcPageConverterModel convertModel = new NpcPageConverterModel();
        convertModel.setModelList(model.getNpcModels());
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取NPC总数
     * @return
     */
    public Integer getTotalNpcs(NpcPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        NpcCloudPlatformModel page = npcService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }
}
