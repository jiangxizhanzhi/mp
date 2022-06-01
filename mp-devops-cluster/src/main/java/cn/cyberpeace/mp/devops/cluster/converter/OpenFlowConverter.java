package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.OpenFlowPageParams;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.OpenFlowService;
import cn.cyberpeace.mp.devops.cluster.converter.model.OpenFlowPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.request.OpenFlowPageRequest;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowMatchConditionVO;
import cn.cyberpeace.mp.devops.cluster.vo.OpenFlowVO;
import cn.cyberpeace.mp.devops.cluster.vo.convert.OpenFlowVoConverter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenFlowConverter implements BaseConverter{

    private final OpenFlowService openFlowService;

    private final OpenFlowVoConverter openFlowVoConverter;

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
        CloudPlatformPageResult<OpenFlow> model = openFlowService.page(params);
        List<OpenFlow> openFlows = model.getList();
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
