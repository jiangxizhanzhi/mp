package cn.cyberpeace.mp.collectionapi.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.collection.bo.AttackFlowBO;
import cn.cyberpeace.mp.collection.bo.AttackTypeBO;
import cn.cyberpeace.mp.collection.bo.CountBO;
import cn.cyberpeace.mp.collection.bo.EsIpFieldBO;
import cn.cyberpeace.mp.collection.bo.FlowBO;
import cn.cyberpeace.mp.collection.bo.TeamAttackCountBO;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collection.util.MessageCommonUtil;
import cn.cyberpeace.mp.collectionapi.constant.CollectionApiConstant;
import cn.cyberpeace.mp.collectionapi.request.AttackAggTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest.IPField;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest.AggType;
import cn.cyberpeace.mp.collectionapi.request.AttackTargetTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackTargetTopRequest.Source;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO.AttackCount;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO.FlowSize;
import cn.cyberpeace.mp.collectionapi.vo.AttackFlowTopVO;
import cn.cyberpeace.mp.collectionapi.vo.TrendVO;
import cn.cyberpeace.mp.collectionapi.vo.TrendVO.Count;
import cn.hutool.core.date.DateUtil;
import cn.cyberpeace.mp.collectionapi.vo.FlowVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamTopVO;
import cn.cyberpeace.mp.collectionapi.vo.TopEntry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangjiaxin
 * @Description:
 * @date 2022年10月24日上午9:45:29
 */

@Service
public class FlowQueryService {

    @Resource
    private FlowInfoService flowInfoService;

    /**
     * 总体攻击趋势
     *
     * @return
     */
    public ServiceResult<TrendVO> queryAttackTrend() {
        List<CountBO> bos = flowInfoService.queryAttackTrend();
        
        List<Count> counts = bos.stream().map(bo -> {
            Count c = new Count();
            c.setTime(bo.getKey());
            c.setNum(bo.getCount() * CollectionApiConstant.multiple);
            return c;
        }).collect(Collectors.toList());
        
        TrendVO vo = new TrendVO();
        vo.setName("");
        vo.setCounts(counts);
        
        // 填充空记录
        List<TrendVO> vos = Lists.newArrayList(vo);
        List<String> lines = Lists.newArrayList("");;
        fillEmptyTrendVO(vos, lines, 7, 10);

        return new ServiceResult<>(vo);
    }

    /**
     * 攻击告警级别趋势
     *
     * @return
     */
    public ListObjServiceResult<TrendVO> queryLevelTrend() {
        Map<String, List<CountBO>> severityMap = flowInfoService.queryAttackLevel();
        Map<String, TrendVO> resultMap = Maps.newHashMap();

        for (int level = 1; level <= 4; level++) {
            String levelName = getSeverityDisplayName(String.valueOf(level));
            TrendVO vo = resultMap.computeIfAbsent(levelName, k -> new TrendVO());
            vo.setName(levelName);

            mergeTrendData(vo, severityMap.get(String.valueOf(level)));
        }
        
        // 填充空记录
        List<TrendVO> vos = Lists.newArrayList(resultMap.values());
        List<String> lines = resultMap.keySet().stream().collect(Collectors.toList());
        fillEmptyTrendVO(vos, lines, 7, 10);

        // 放大攻击次数
        vos.stream().map(TrendVO::getCounts).flatMap(Collection::stream).forEach(c -> {
            c.setNum(c.getNum() * CollectionApiConstant.multiple);
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 战队攻击趋势
     *
     * @return
     */
    public ListObjServiceResult<TrendVO> teamAttackTrend(AttackFlowBaseRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        Map<String, List<CountBO>> ipMap = flowInfoService.queryTeamAttackTrend(esipFieldBos);
        Map<String, TrendVO> resultMap = Maps.newHashMap();

        List<TrendVO> vos = Lists.newArrayList();
        ipMap.forEach((key, bos) -> {
            String team = getNameByIp(request, key);
            TrendVO vo = resultMap.computeIfAbsent(team, k -> new TrendVO());
            vo.setName(team);
            vos.add(vo);

            mergeTrendData(vo, bos);
        });

        // 填充空记录
        List<String> lines = request.getSources().stream().map(IPField::getName).collect(Collectors.toList());
        fillEmptyTrendVO(vos, lines, 73, 5);
        
        // 放大攻击次数
        vos.stream().map(TrendVO::getCounts).flatMap(Collection::stream).forEach(c -> {
            c.setNum(c.getNum() * CollectionApiConstant.multiple);
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }
    
    /**
     * 
     * @param vos
     * @param pointsNum 时间点
     * @param interval 时间间隔（分钟）
     */
    private void fillEmptyTrendVO(List<TrendVO> vos, List<String> lines, int pointsNum, int interval) {
        final char LAST_CHAR = (char) ((interval % 10) + '0');
        List<String> timePoints = Lists.newArrayList();;
        for(TrendVO vo : vos) {
            if(CollectionUtils.isNotEmpty(vo.getCounts())) {
                timePoints = vo.getCounts().stream().map(Count::getTime).collect(Collectors.toList());
                break;
            }
        }
        if(CollectionUtils.isEmpty(timePoints)) {
            Calendar now = Calendar.getInstance();
            for(int i = 0; i < pointsNum; i++) {
                now.add(Calendar.MINUTE, i > 0 ? -interval : 0);
                String time = DateUtil.format(now.getTime(), "HH:mm");
                char lastChar = time.charAt(time.length() - 1);
                lastChar = lastChar >= LAST_CHAR ? LAST_CHAR : '0';
                timePoints.add(0, time.substring(0, time.length() - 1) + lastChar);
            }
        }
        for(String lineName : lines) {
            TrendVO vo = vos.stream().filter(v -> v.getName().equals(lineName)).findFirst().orElse(null);
            if(vo == null) {
                vo = new TrendVO();
                vo.setName(lineName);
                vos.add(vo);
            }
            
            if(CollectionUtils.isEmpty(vo.getCounts())) {
                List<Count> counts = timePoints.stream().map(t -> {
                    Count c = new Count();
                    c.setNum(0);
                    c.setTime(t);
                    return c;
                }).collect(Collectors.toList());
                
                vo.setCounts(counts);
            }
        }
    }

    /**
     * 合并趋势数据点
     *
     * @param vo
     * @param bos
     */
    private void mergeTrendData(TrendVO vo, List<CountBO> bos) {
        if (bos != null) {
            List<Count> countsAdd = bos.stream().map(bo -> {
                Count c = new Count();
                c.setTime(bo.getKey());
                c.setNum(bo.getCount());
                return c;
            }).collect(Collectors.toList());

            List<Count> countsOrig = vo.getCounts();
            if (countsOrig == null) {
                vo.setCounts(countsAdd);
            } else {
                int size = countsOrig.size();
                for (int i = 0; i < size; i++) {
                    Count c = countsOrig.get(i);
                    c.setNum(c.getNum() + countsAdd.get(i).getNum());
                }
            }
        }
    }

    /**
     * 攻击次数与流量大小（战队 or 设备）
     *
     * @return
     */
    public ListObjServiceResult<AggAttackFlowVO> getAttackFlow(AttackFlowBaseRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        List<AttackFlowBO> bos = flowInfoService.queryAttackFlow(esipFieldBos);

        List<AggAttackFlowVO> vos = bos.stream().map(bo -> {
            AttackCount attackCount = new AttackCount();
            attackCount.setAttackCount(bo.getCount() * CollectionApiConstant.multiple);
            FlowSize flowSize = new FlowSize();
            flowSize.setSize(bo.getSize());

            AggAttackFlowVO vo = new AggAttackFlowVO();
            vo.setName(getNameByIp(request, bo.getIp()));
            vo.setAttackCount(attackCount);
            vo.setFlowSize(flowSize);
            return vo;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 获取top数据
     *
     * @param request
     * @return
     */
    public ListObjServiceResult<AttackFlowTopVO> getAttackTop(AttackFlowTopRequest request) {
        int aggType = request.getAggType();
        if (aggType == AggType.ATTACK_COUNT) {
            return queryTeamAttackCountTop5(request);
        } else if (aggType == AggType.ATTACK_TYPE) {
            return queryAttackTypeTop5(request);
        } else if (aggType == AggType.ATTACK_PROTOCOL) {
            return queryProtocolTop5(request);
        } else if (aggType == AggType.EQUIPMENT_TYPE) {
            return queryEquipmentTypeTop5(request);
        }

        return null;
    }

    /**
     * 攻击次数top5
     *
     * @return
     */
    private ListObjServiceResult<AttackFlowTopVO> queryTeamAttackCountTop5(AttackAggTopRequest request) {
        List<TeamAttackCountVO> vos = getTeamAttackAggTop(request).getSource().getObjs();
        List<AttackFlowTopVO> topVOs = vos.stream().map(vo -> {
            AttackFlowTopVO topVO = new AttackFlowTopVO();
            topVO.setName(vo.getTeam());
            topVO.setCount(vo.getCount() * CollectionApiConstant.multiple);
            return topVO;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(topVOs, topVOs.size()));
    }

    /**
     * 攻击手段占比top5（全部队伍）
     *
     * @return
     */
    private ListObjServiceResult<AttackFlowTopVO> queryAttackTypeTop5(AttackFlowTopRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        List<CountBO> bos = flowInfoService.queryAttackTypeAllTeams(esipFieldBos);
        double total = bos.stream().mapToLong(CountBO::getCount).sum();

        List<AttackFlowTopVO> vos = bos.stream().map(bo -> {
            AttackFlowTopVO vo = new AttackFlowTopVO();
            vo.setName(bo.getKey());
            vo.setCount(bo.getCount());
            vo.setPercent(bo.getCount() / total);
            return vo;
        }).collect(Collectors.toList());
        
        // 排序取top
        vos = vos.stream().sorted(Comparator.comparing(AttackFlowTopVO::getCount).reversed()).collect(Collectors.toList());
        if (request.getTopSize() != null) {
            vos = vos.subList(0, Math.min(vos.size(), request.getTopSize()));
        }

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 设备攻击协议TOP5
     *
     * @return
     */
    private ListObjServiceResult<AttackFlowTopVO> queryProtocolTop5(AttackFlowTopRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        List<CountBO> bos = flowInfoService.queryProtocolTop5(esipFieldBos);
        double total = bos.stream().mapToLong(CountBO::getCount).sum();

        List<AttackFlowTopVO> vos = bos.stream().map(bo -> {
            AttackFlowTopVO vo = new AttackFlowTopVO();
            vo.setName(bo.getKey());
            vo.setCount(bo.getCount() * CollectionApiConstant.multiple);
            vo.setPercent(bo.getCount() / total);
            return vo;
        }).collect(Collectors.toList());
        
        // 排序取top
        vos = vos.stream().sorted(Comparator.comparing(AttackFlowTopVO::getCount).reversed()).collect(Collectors.toList());
        if (request.getTopSize() != null) {
            vos = vos.subList(0, Math.min(vos.size(), request.getTopSize()));
        }

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 设备类型被攻击占比TOP5
     *
     * @return
     */
    private ListObjServiceResult<AttackFlowTopVO> queryEquipmentTypeTop5(AttackFlowTopRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        List<CountBO> bos = flowInfoService.queryEquipmentIpAttackCount(esipFieldBos);
        double total = bos.stream().mapToLong(CountBO::getCount).sum();

        Map<String, AttackFlowTopVO> vosMap = Maps.newHashMap();
        bos.forEach(bo -> {
            String name = getNameByIp(request, bo.getKey());
            AttackFlowTopVO vo = vosMap.computeIfAbsent(name, key -> new AttackFlowTopVO());
            vo.setName(name);
            vo.setCount(vo.getCount() + bo.getCount());
        });

        // 排序取top
        List<AttackFlowTopVO> vos = vosMap.values().stream().sorted(Comparator.comparing(AttackFlowTopVO::getCount).reversed()).collect(Collectors.toList());
        if (request.getTopSize() != null) {
            vos = vos.subList(0, Math.min(vos.size(), request.getTopSize()));
        }
        vos.forEach(v -> {
            v.setPercent(v.getCount() / total);
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }
    
    /**
     * 总攻击次数
     *
     * @param multiple 放大倍数
     * @return
     */
    public ServiceResult<Long> getAttackTotalCount() {
        return new ServiceResult<>(flowInfoService.attackTotalCount() * CollectionApiConstant.multiple);
    }

    /**
     * 战队攻击排名
     *
     * @param request
     * @return
     */
    public ListObjServiceResult<TeamAttackCountVO> getTeamAttackAggTop(AttackAggTopRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        List<TeamAttackCountBO> bos = flowInfoService.queryTeamAttackAgg(esipFieldBos);

        Map<String, TeamAttackCountVO> name2CountMap = Maps.newHashMap();
        for (TeamAttackCountBO bo : bos) {
            String team = getNameByIp(request, bo.getIp());
            TeamAttackCountVO vo = name2CountMap.computeIfAbsent(team, key -> new TeamAttackCountVO());
            vo.setTeam(team);
            vo.setCount(bo.getCount() + vo.getCount());
            vo.setAttackTargetIp(bo.getAttackTargetIps().stream().map(CountBO::getKey).collect(Collectors.toList()));
            vo.setAttackTypes(bo.getAttackTypes().stream().map(CountBO::getKey).collect(Collectors.toList()));
        }

        // 排序取top3
        List<TeamAttackCountVO> vos = new ArrayList<>(name2CountMap.values());
        vos.sort(Comparator.comparing(TeamAttackCountVO::getCount).reversed());
        if (request.getTopSize() != null) {
            vos = vos.subList(0, Math.min(vos.size(), request.getTopSize()));
        }

        // 放大攻击次数
        vos.forEach(vo -> {
            vo.setCount(vo.getCount() * CollectionApiConstant.multiple);
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 攻击流量列表
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    public ListObjServiceResult<FlowVO> attackFlowList(Integer pageNum, Integer pageSize) {
        List<FlowBO> bos = flowInfoService.attackFlowList(pageNum, pageSize);
        List<FlowVO> vos = bos.stream().map(bo -> {
            bo.fillProperties();
            FlowVO vo = new FlowVO();
            BeanUtils.copyProperties(bo, vo);
            vo.setSeverity(getSeverityDisplayName(vo.getSeverity()));
            vo.setAttackType(MessageCommonUtil.convertMessage(vo.getAttackType()));
            return vo;
        }).collect(Collectors.toList());

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 获取攻击目标top数据
     *
     * @param request
     * @return
     */
    public ListObjServiceResult<TeamTopVO> getAttackTargetTop(AttackTargetTopRequest request) {
        // 转换数据结构
        List<IPField> sourceFields = request.getSources().stream().map(s -> {
            IPField ipField = new IPField();
            ipField.setName(s.getName());
            ipField.setIps(s.getIps());
            return ipField;
        }).collect(Collectors.toList());
        List<IPField> targetFields = request.getSources().stream().map(Source::getTargets).flatMap(Collection::stream)
                .collect(Collectors.toList());

        AttackFlowBaseRequest baseRequest = new AttackFlowBaseRequest();
        baseRequest.setSources(sourceFields);
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(baseRequest);
        Map<String, List<CountBO>> ipMap = flowInfoService.queryTeamAttackTarget(esipFieldBos);

        Map<String, TeamTopVO> vosMap = Maps.newHashMap();
        ipMap.forEach((key1, value) -> {
            String team = getNameByIp(sourceFields, key1);
            TeamTopVO vo = vosMap.computeIfAbsent(team, key -> new TeamTopVO());
            vo.setName(team);

            List<TopEntry> entries = vo.getEntries();
            if (entries == null) {
                entries = Lists.newArrayList();
                vo.setEntries(entries);
            }

            for (CountBO bo : value) {
                String targetName = getNameByIp(targetFields, bo.getKey());
                if(StringUtils.isEmpty(targetName)) {
                    continue;
                }
                TopEntry topEntry = entries.stream().filter(t -> targetName.equals(t.getName())).findFirst()
                        .orElse(null);
                if (topEntry == null) {
                    topEntry = new TopEntry();
                    topEntry.setName(targetName);
                    entries.add(topEntry);
                }
                topEntry.setCount(topEntry.getCount() + bo.getCount());
            }
        });

        // 排序取topSize
        List<TeamTopVO> vos = new ArrayList<>(vosMap.values());
        Integer topSize = request.getTopSize();
        vos.forEach(vo -> {
            List<TopEntry> entries = vo.getEntries();
            entries.sort(Comparator.comparing(TopEntry::getCount).reversed());
            if(topSize != null) {
                entries = entries.subList(0, Math.min(entries.size(), topSize));
            }
            vo.setEntries(entries);
        });

        // 放大攻击次数
        vos.stream().map(TeamTopVO::getEntries).flatMap(Collection::stream).forEach(c -> {
            c.setCount(c.getCount() * CollectionApiConstant.multiple);
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 攻击手段占比top5（指定队伍）
     *
     * @return
     */
    public ListObjServiceResult<TeamTopVO> getAttackTypeTop(AttackAggTopRequest request) {
        List<EsIpFieldBO> esIpFieldBOS = genIPFieldBos(request);
        List<AttackFlowBO> bos = flowInfoService.queryAttackType(esIpFieldBOS);

        Map<String, TeamTopVO> vosMap = Maps.newHashMap();
        bos.forEach(bo -> {
            String name = getNameByIp(request, bo.getIp());
            TeamTopVO vo = vosMap.computeIfAbsent(name, key -> new TeamTopVO());
            vo.setName(name);

            List<TopEntry> entries = vo.getEntries();
            if (entries == null) {
                entries = Lists.newArrayList();
                vo.setEntries(entries);
            }

            for (AttackTypeBO attackTypeBO : bo.getAttackTypes()) {
                String attackTypeName = attackTypeBO.getEnName();
                TopEntry topEntry = entries.stream().filter(t -> attackTypeName.equals(t.getName())).findFirst()
                        .orElse(null);
                if (topEntry == null) {
                    topEntry = new TopEntry();
                    topEntry.setName(attackTypeName);
                    entries.add(topEntry);
                }
                topEntry.setCount(topEntry.getCount() + attackTypeBO.getCount());
            }
        });

        // 排序取topSize
        List<TeamTopVO> vos = new ArrayList<>(vosMap.values());
        Integer topSize = request.getTopSize();
        vos.forEach(vo -> {
            List<TopEntry> entries = vo.getEntries();
            double total = entries.stream().mapToLong(TopEntry::getCount).sum();
            entries.sort(Comparator.comparing(TopEntry::getCount).reversed());
            if (topSize != null) {
                entries = entries.subList(0, Math.min(entries.size(), topSize));
            }
            vo.setEntries(entries);
            
            entries.forEach(e -> {
               e.setPercent(e.getCount() / total); 
            });
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 生成ip字段的搜索项
     *
     * @param request
     * @return
     */
    private List<EsIpFieldBO> genIPFieldBos(AttackFlowBaseRequest request) {
        List<EsIpFieldBO> bos = Lists.newArrayList();

        EsIpFieldBO.Field ipField = CollectionUtils.isNotEmpty(request.getSources()) ? EsIpFieldBO.Field.SOURCE
                : EsIpFieldBO.Field.DESTINATION;
        List<IPField> ipFields = CollectionUtils.isNotEmpty(request.getSources()) ? request.getSources()
                : request.getTargets();
        if(ipFields == null) {
            return bos;
        }

        for (IPField field : ipFields) {
            EsIpFieldBO bo = new EsIpFieldBO();
            bo.setField(ipField);
            bos.add(bo);

            if (CollectionUtils.isNotEmpty(field.getIps())) {
                bo.setFilterType(EsIpFieldBO.FilterType.LIST);
                bo.setIps(field.getIps());
            } else {
                bo.setFilterType(EsIpFieldBO.FilterType.PREFIX);
                bo.setIps(Lists.newArrayList(getIpPrefixByCidr(field.getCidr())));
            }
        }

        return bos;
    }

    /**
     * cidr前缀
     *
     * @param cidr
     * @return
     */
    private String getIpPrefixByCidr(String cidr) {
        return cidr.substring(0, cidr.lastIndexOf('.') + 1);
    }

    /**
     * 根据ip判断所属队伍
     *
     * @param request
     * @param ip
     * @return
     */
    private String getNameByIp(AttackFlowBaseRequest request, String ip) {
        List<IPField> ipFields = CollectionUtils.isNotEmpty(request.getSources()) ? request.getSources()
                : request.getTargets();
        if(ipFields == null) {
            return "";
        }
        return getNameByIp(ipFields, ip);
    }

    /**
     * 根据ip判断所属队伍
     *
     * @param ipFields
     * @param ip
     * @return
     */
    private String getNameByIp(List<IPField> ipFields, String ip) {
        for (IPField field : ipFields) {
            if (CollectionUtils.isNotEmpty(field.getIps()) && field.getIps().contains(ip)) {
                return field.getName();
            }

            if (StringUtils.isNotEmpty(field.getCidr()) && ip.startsWith(getIpPrefixByCidr(field.getCidr()))) {
                return field.getName();
            }
        }

        return "";
    }

    /**
     * 告警级别显示名称
     *
     * @param severity
     * @return
     */
    private String getSeverityDisplayName(String severity) {
        if ("1".equals(severity)) {
            return "高级";
        } else if ("2".equals(severity)) {
            return "中级";
        } else if ("3".equals(severity) || "4".equals(severity)) {
            return "低级";
        }

        return "";
    }
}
