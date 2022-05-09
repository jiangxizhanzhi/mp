package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.OpenFlowPageParams;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.OpenFlowService;
import cn.cyberpeace.mp.devops.cluster.converter.model.OpenFlowPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.OpenFlowCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.BasePageRequest;
import cn.cyberpeace.mp.devops.cluster.request.OpenFlowPageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowMatchConditionVO;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import cn.cyberpeace.mp.devops.cluster.vo.convert.OpenFlowVoConverter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OpenFlowConverter implements BaseConverter{

    private OpenFlowService openFlowService;

    private OpenFlowVoConverter openFlowVoConverter;

    public OpenFlowConverter(OpenFlowService openFlowService,OpenFlowVoConverter openFlowVoConverter) {
        this.openFlowService = openFlowService;
        this.openFlowVoConverter = openFlowVoConverter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getOffset,
                BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    /**
     * 分页查询流表信息
     * @param request
     * @return
     */
    public OpenFlowPageConverterModel page(OpenFlowPageRequest request) {
        OpenFlowPageParams params = new OpenFlowPageParams();
        params.setMethod(request.getMethod());
        params.setLimit(request.getLimit());
        params.setOffset(request.getOffset());
        OpenFlowCloudPlatformModel model = openFlowService.page(params);
        List<OpenFlow> openFlows = model.getOpenFlows();
        List<OpenFlowVO> openFlowVOS = openFlows.stream().map(v -> {
            OpenFlowVO openFlowVO = openFlowVoConverter.openFlowToOpenFlowVO(v);
            Map<String, Object> matchConditionMap = convertMatchCondition(v.getMatchCondition());
            JSONObject object = JSONUtil.parseObj(matchConditionMap);
            OpenFlowMatchConditionVO openFlowMatchConditionVO =  JSONUtil.toBean(object, OpenFlowMatchConditionVO.class);
            openFlowVO.setOpenFlowMatchConditionVO(openFlowMatchConditionVO);
            return openFlowVO;
        }).collect(Collectors.toList());
        OpenFlowPageConverterModel convertModel = new OpenFlowPageConverterModel();
        convertModel.setOpenFlowVOS(openFlowVOS);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 字符串转Map
     * @param matchCondition 例如：priority=4,in_port=2,dl_vlan=13
     * @return
     */
    private Map<String,Object> convertMatchCondition(String matchCondition){
        Map<String,Object> matchConditionMap = new HashMap<>();

        if (StringUtils.isBlank(matchCondition)){
            return matchConditionMap;
        }

        String[] matchConditions = matchCondition.split(",");
        for (int i = 0; i < matchConditions.length; i++){
            String condition = matchConditions[i];
            if (StringUtils.isBlank(condition)){
                break;
            }
            String[] map = condition.split("=");
            if (map.length == 2 ){
                matchConditionMap.put(map[0],map[1]);
            }
        }
        return matchConditionMap;
    }

}
