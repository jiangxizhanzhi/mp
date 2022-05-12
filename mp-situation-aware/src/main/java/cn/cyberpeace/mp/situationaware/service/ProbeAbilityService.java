package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeAbilityDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.ProbeAbilityMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/29 10:17
 */
@Service
@Slf4j
public class ProbeAbilityService {

    @Autowired
    private ProbeAbilityMapper probeAbilityMapper;

    /**
     * 探针能力分页查询
     *
     * @return
     */
    public PageInfo<ProbeAbilityDto> page(ProbePageQueryRequest pageRequest) {
        log.info("pageRequest:{}", pageRequest);
        pageRequest.ensure();

        ProbeSearchCondition condition = new ProbeSearchCondition();
        BeanUtils.copyProperties(pageRequest, condition);
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getPageSize());
        List<ProbeAbilityDto> dtos = probeAbilityMapper.listProbeAbilityDtos(condition);
        dtos.forEach(dto -> processProbeDtoOsTypes(dto));
        PageInfo<ProbeAbilityDto> pageInfo = new PageInfo(dtos);
        return pageInfo;
    }

    private void processProbeDtoOsTypes(ProbeAbilityDto dto) {
        if (StringUtils.isNotBlank(dto.getOsType())) {
            List<Integer> osTypes = new ArrayList<>();
            for (String osType : dto.getOsType().split(",")) {
                osTypes.add(Integer.valueOf(osType));
            }
            dto.setOsTypes(osTypes);
        }
    }

}
