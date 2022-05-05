package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeAbilityDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import cn.cyberpeace.mp.situationaware.repository.ProbeAbilityMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<ProbeAbilityDto> list = probeAbilityMapper.listProbeAbilityDtos(condition);
        PageInfo<ProbeAbilityDto> pageInfo = new PageInfo(list);
        return pageInfo;
    }

}
