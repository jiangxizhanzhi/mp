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
import cn.cyberpeace.mp.collectionapi.vo.FlowVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamTopVO;
import cn.cyberpeace.mp.collectionapi.vo.TopEntry;
import cn.cyberpeace.mp.collectionapi.vo.TrendVO;
import cn.cyberpeace.mp.collectionapi.vo.TrendVO.Count;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
            c.setNum(CollectionApiConstant.multipleNum(bo.getCount()));
            return c;
        }).collect(Collectors.toList());

        TrendVO vo = new TrendVO();
        vo.setName("");
        vo.setCounts(counts);

        // 填充空记录
        List<TrendVO> vos = Lists.newArrayList(vo);
        List<String> lines = Lists.newArrayList("");
        ;
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
            c.setNum(CollectionApiConstant.multipleNum(c.getNum()));
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
        if (CollectionUtils.isEmpty(esipFieldBos)) {
            return new ListObjServiceResult<>(new ListObjResult<>(Lists.newArrayList(), 0));
        }
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
            c.setNum(CollectionApiConstant.multipleNum(c.getNum()));
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * @param vos
     * @param pointsNum 时间点
     * @param interval  时间间隔（分钟）
     */
    private void fillEmptyTrendVO(List<TrendVO> vos, List<String> lines, int pointsNum, int interval) {
        final char LAST_CHAR = (char) ((interval % 10) + '0');
        List<String> timePoints = Lists.newArrayList();
        ;
        for (TrendVO vo : vos) {
            if (CollectionUtils.isNotEmpty(vo.getCounts())) {
                timePoints = vo.getCounts().stream().map(Count::getTime).collect(Collectors.toList());
                break;
            }
        }
        if (CollectionUtils.isEmpty(timePoints)) {
            Calendar now = Calendar.getInstance();
            for (int i = 0; i < pointsNum; i++) {
                now.add(Calendar.MINUTE, i > 0 ? -interval : 0);
                String time = DateUtil.format(now.getTime(), "HH:mm");
                char lastChar = time.charAt(time.length() - 1);
                lastChar = lastChar >= LAST_CHAR ? LAST_CHAR : '0';
                timePoints.add(0, time.substring(0, time.length() - 1) + lastChar);
            }
        }
        for (String lineName : lines) {
            TrendVO vo = vos.stream().filter(v -> v.getName().equals(lineName)).findFirst().orElse(null);
            if (vo == null) {
                vo = new TrendVO();
                vo.setName(lineName);
                vos.add(vo);
            }

            if (CollectionUtils.isEmpty(vo.getCounts())) {
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
        if (CollectionUtils.isEmpty(esipFieldBos)) {
            return new ListObjServiceResult<>(new ListObjResult<>(Lists.newArrayList(), 0));
        }
        List<AttackFlowBO> bos = flowInfoService.queryAttackFlow(esipFieldBos);

        List<AggAttackFlowVO> vos = bos.stream().map(bo -> {
            AttackCount attackCount = new AttackCount();
            attackCount.setAttackCount(CollectionApiConstant.multipleNum(bo.getCount()));
            FlowSize flowSize = new FlowSize();
            flowSize.setSize(CollectionApiConstant.multipleNum(bo.getSize()));

            AggAttackFlowVO vo = new AggAttackFlowVO();
            vo.setAttackTypeBOMap(new HashMap<>());
            vo.setName(getNameByIp(request, bo.getIp()));
            if (vo.getName() == null) {
                vo.setName(StringUtils.EMPTY);
            }
            vo.setAttackTypeBOS(bo.getAttackTypes());
            vo.setAttackCount(attackCount);
            vo.setFlowSize(flowSize);
            return vo;
        }).collect(Collectors.toList());

        Map<String, AggAttackFlowVO> resMap = new HashMap<>();
//        long countBig = 0;
//        long flowSizeBig = 0;
//        for (var vo : vos) {
//            if (vo.getAttackCount() != null) {
//                countBig += vo.getAttackCount().getAttackCount();
//            }
//            if (vo.getFlowSize() != null) {
//                flowSizeBig += vo.getFlowSize().getSize();
//            }
//        }
//        BigDecimal totalCount = BigDecimal.valueOf(countBig);
//        BigDecimal totalSize = BigDecimal.valueOf(flowSizeBig);
//        for (var vo : vos) {
//            if (vo.getAttackCount() != null) {
//                BigDecimal count = BigDecimal.valueOf(vo.getAttackCount().getAttackCount());
//                vo.getAttackCount().setPercent(count.divide(totalCount, 4, RoundingMode.HALF_DOWN).doubleValue());
//            }
//            if (vo.getFlowSize() != null) {
//                BigDecimal size = BigDecimal.valueOf(vo.getFlowSize().getSize());
//                vo.getFlowSize().setPercent(size.divide(totalSize, 4, RoundingMode.HALF_DOWN).doubleValue());
//            }
//        }
        for (var vo : vos) {
            if (!resMap.containsKey(vo.getName())) {
                AggAttackFlowVO res = new AggAttackFlowVO();
                res.setName(vo.getName());
                AttackCount attackCount = new AttackCount();
                attackCount.setAttackCount(vo.getAttackCount().getAttackCount());
                attackCount.setPercent(vo.getAttackCount().getPercent());
                if (attackCount.getPercent() == null) {
                    attackCount.setPercent(0.0);
                }
                vo.setAttackCount(attackCount);

                FlowSize flowSize = new FlowSize();
                flowSize.setSize(vo.getFlowSize().getSize());
                flowSize.setPercent(vo.getFlowSize().getPercent());
                if (flowSize.getPercent() == null) {
                    flowSize.setPercent(0.0);
                }
                vo.setFlowSize(flowSize);

                if (CollectionUtils.isNotEmpty(vo.getAttackTypeBOS())) {
                    vo.getAttackTypeBOS().forEach(t -> {
                        if (!vo.getAttackTypeBOMap().containsKey(t.getZhName())) {
                            AttackTypeBO attackTypeBO = new AttackTypeBO();
                            attackTypeBO.setZhName(t.getZhName());
                            attackTypeBO.setCount(t.getCount());
                            attackTypeBO.setFlowSize(t.getFlowSize());
                            vo.getAttackTypeBOMap().put(t.getZhName(), attackTypeBO);
                        } else {
                            var x = vo.getAttackTypeBOMap().get(t.getZhName());
                            x.setCount(x.getCount() + t.getCount());
                            x.setFlowSize(x.getFlowSize() + t.getFlowSize());
                        }
                    });
                }

                resMap.put(vo.getName(), vo);
            } else {
                AttackCount attackCount = resMap.get(vo.getName()).getAttackCount();
                attackCount.setAttackCount(attackCount.getAttackCount() + vo.getAttackCount().getAttackCount());
                if (vo.getAttackCount().getPercent() != null) {
                    attackCount.setPercent(attackCount.getPercent() + vo.getAttackCount().getPercent());
                }
                FlowSize flowSize = resMap.get(vo.getName()).getFlowSize();
                flowSize.setSize(flowSize.getSize() + vo.getFlowSize().getSize());
                if (vo.getFlowSize().getPercent() != null) {
                    flowSize.setPercent(flowSize.getPercent() + vo.getFlowSize().getPercent());
                }

                if (CollectionUtils.isNotEmpty(vo.getAttackTypeBOS())) {
                    vo.getAttackTypeBOS().forEach(t -> {
                        if (!vo.getAttackTypeBOMap().containsKey(t.getZhName())) {
                            AttackTypeBO attackTypeBO = new AttackTypeBO();
                            attackTypeBO.setZhName(t.getZhName());
                            attackTypeBO.setCount(t.getCount());
                            attackTypeBO.setFlowSize(t.getFlowSize());
                            vo.getAttackTypeBOMap().put(t.getZhName(), attackTypeBO);
                        } else {
                            var x = vo.getAttackTypeBOMap().get(t.getZhName());
                            x.setCount(x.getCount() + t.getCount());
                            x.setFlowSize(x.getFlowSize() + t.getFlowSize());
                        }
                    });
                }

            }
        }

        resMap.values().forEach(v -> {
            v.setAttackTypeBOS(new ArrayList<>(v.getAttackTypeBOMap().values()));
            Collections.sort(v.getAttackTypeBOS(), (v1, v2) -> {
                return (int) (v2.getCount() - v1.getCount());
            });
            v.setAttackTypeBOMap(null);
        });

        var rrMap = rrMap();
        var resArray = new ArrayList<>(resMap.values());
        Collections.sort(resArray, (r1, r2) -> {
            return r1.getName().compareTo(r2.getName());
        });
        for (var res : resArray) {
            System.out.println();
            boolean wr = false;
            for (var bo : res.getAttackTypeBOS()) {
                System.out.println();
                if (!wr) {
                    System.out.print(rrMap.get(res.getName()).hezi + "\t");
                    System.out.print(rrMap.get(res.getName()).saidao + "\t");
                    System.out.print(rrMap.get(res.getName()).shebei + "\t");
                    System.out.print(res.getAttackCount().getAttackCount() + "\t");
                    System.out.print(res.getFlowSize().getSize() + "\t");
                    wr = true;
                } else {
                    System.out.print("\t\t\t\t\t");
                }
                System.out.print(bo.getZhName() + "\t");
                System.out.print(bo.getCount() + "\t");
                System.out.print(bo.getFlowSize() + "\t");
            }
        }


        var result = new ArrayList<>(resMap.values());

        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size()));
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
            topVO.setCount(CollectionApiConstant.multipleNum(vo.getCount()));
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
            vo.setCount(CollectionApiConstant.multipleNum(vo.getCount()));
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
            vo.setCount(CollectionApiConstant.multipleNum(bo.getCount()));
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
     * @return
     */
    public ServiceResult<Long> getAttackTotalCount() {
//        7531894 77019
        long result = CollectionApiConstant.multipleNum(flowInfoService.attackTotalCount());
        return new ServiceResult<>(result);
    }

    /**
     * 战队攻击排名
     *
     * @param request
     * @return
     */
    public ListObjServiceResult<TeamAttackCountVO> getTeamAttackAggTop(AttackAggTopRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        if (CollectionUtils.isEmpty(esipFieldBos)) {
            return new ListObjServiceResult<>(new ListObjResult<>(Lists.newArrayList(), 0));
        }
        List<TeamAttackCountBO> bos = flowInfoService.queryTeamAttackAgg(esipFieldBos);

        Map<String, TeamAttackCountVO> name2CountMap = Maps.newHashMap();
        for (TeamAttackCountBO bo : bos) {
            String team = getNameByIp(request, bo.getIp());
            TeamAttackCountVO vo = name2CountMap.computeIfAbsent(team, key -> new TeamAttackCountVO());
            vo.setTeam(team);
            vo.setCount(bo.getCount() + vo.getCount());
            vo.setSize(bo.getSize() + vo.getSize());
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
            vo.setCount(CollectionApiConstant.multipleNum(vo.getCount()));
            vo.setSize(CollectionApiConstant.multipleNum(vo.getSize()));
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
        if (CollectionUtils.isEmpty(request.getSources())) {
            return new ListObjServiceResult<>(new ListObjResult<>(Lists.newArrayList(), 0));
        }
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
                if (StringUtils.isEmpty(targetName)) {
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
            if (topSize != null) {
                entries = entries.subList(0, Math.min(entries.size(), topSize));
            }
            vo.setEntries(entries);
        });

        // 放大攻击次数
        vos.stream().map(TeamTopVO::getEntries).flatMap(Collection::stream).forEach(c -> {
            c.setCount(CollectionApiConstant.multipleNum(c.getCount()));
        });

        return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size()));
    }

    /**
     * 攻击手段占比top5（指定队伍）
     *
     * @return
     */
    public ListObjServiceResult<TeamTopVO> getAttackTypeTop(AttackAggTopRequest request) {
        List<EsIpFieldBO> esipFieldBos = genIPFieldBos(request);
        if (CollectionUtils.isEmpty(esipFieldBos)) {
            return new ListObjServiceResult<>(new ListObjResult<>(Lists.newArrayList(), 0));
        }
        List<AttackFlowBO> bos = flowInfoService.queryAttackType(esipFieldBos);

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
        if (ipFields == null) {
            return bos;
        }

        for (IPField field : ipFields) {
            EsIpFieldBO bo = new EsIpFieldBO();
            bo.setField(ipField);
            bos.add(bo);

            if (field.getIps() != null) {
                bo.setFilterType(EsIpFieldBO.FilterType.LIST);
                bo.setIps(field.getIps());
            } else {
                bo.setFilterType(EsIpFieldBO.FilterType.PREFIX);
                bo.setIps(Lists.newArrayList(getIpPrefixByCidr(field.getCidr())));
            }
            if (CollectionUtils.isNotEmpty(bo.getIps())) {
                List<String> list = new ArrayList<>();
                for (String ip : bo.getIps()) {
                    if (!"10.50.19.29".equals(ip)) {
                        list.add(ip);
                    }
                }
                bo.setIps(list);
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
        if (ipFields == null) {
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


    public static Map<String, RR> rrMap() {
        String res = res();
        Map<String, RR> rrMap = new HashMap<>();
        JSONObject t1 = JSON.parseObject(res);
        for (String key : t1.keySet()) {
            JSONObject saidao = t1.getJSONObject(key);
            for (String saidaokey : saidao.keySet()) {
                JSONObject shebei = saidao.getJSONObject(saidaokey);
                for (String shebeiKey : shebei.keySet()) {
                    RR rr = new RR();
                    rr.hezi = key;
                    rr.saidao = saidaokey;
                    rr.shebei = shebeiKey;
                    rr.ip = new ArrayList<>();
                    rrMap.put(rr.shebei, rr);
                    JSONArray ip = shebei.getJSONArray(shebeiKey);
                    for (String ipa : ip.toArray(new String[]{})) {
                        rr.ip.add(ipa);
                    }
                }
            }
        }
        return rrMap;
    }

    public static class RR {
        String hezi;
        String saidao;
        String shebei;
        List<String> ip;
    }

    public static String res() {
        return "{\n" +
                "    \"black\": {\n" +
                "        \"互联网赛道\": {\n" +
                "            \"商用路由器-1\": [\n" +
                "                \"172.29.202.10\"\n" +
                "            ],\n" +
                "            \"商用路由器-2\": [\n" +
                "                \"172.29.251.20\"\n" +
                "            ],\n" +
                "            \"商用路由器-3\": [\n" +
                "                \"172.29.252.30\"\n" +
                "            ],\n" +
                "            \"商用路由器-4\": [\n" +
                "                \"172.29.250.40\"\n" +
                "            ],\n" +
                "            \"商用路由器-5\": [\n" +
                "                \"172.29.235.50\"\n" +
                "            ],\n" +
                "            \"黑盒拟态路由器\": [\n" +
                "                \"172.29.201.116\"\n" +
                "            ],\n" +
                "            \"商用存储-1\": [\n" +
                "                \"172.29.116.200\"\n" +
                "            ],\n" +
                "            \"商用存储-2\": [\n" +
                "                \"172.29.203.200\"\n" +
                "            ],\n" +
                "            \"黑盒拟态存储\": [\n" +
                "                \"172.29.6.101\"\n" +
                "            ],\n" +
                "            \"商用DNS\": [\n" +
                "                \"172.29.221.11\"\n" +
                "            ],\n" +
                "            \"黑盒拟态DNS\": [\n" +
                "                \"172.29.4.9\"\n" +
                "            ],\n" +
                "            \"商用交换机-1\": [\n" +
                "                \"172.29.217.200\"\n" +
                "            ],\n" +
                "            \"商用交换机-2\": [\n" +
                "                \"172.29.244.200\"\n" +
                "            ],\n" +
                "            \"商用交换机-3\": [\n" +
                "                \"172.29.246.200\"\n" +
                "            ],\n" +
                "            \"黑盒拟态交换机\": [\n" +
                "                \"172.29.243.240\"\n" +
                "            ],\n" +
                "            \"商用云\": [\n" +
                "                \"172.29.126.100\"\n" +
                "            ],\n" +
                "            \"黑盒拟态云\": [\n" +
                "                \"172.29.248.100\"\n" +
                "            ],\n" +
                "            \"黑盒拟态WEB\": [\n" +
                "                \"172.29.215.60\"\n" +
                "            ],\n" +
                "            \"商用WEB-1\": [\n" +
                "                \"172.29.216.11\",\n" +
                "                \"172.29.216.12\",\n" +
                "                \"172.29.216.13\",\n" +
                "                \"172.29.216.14\",\n" +
                "                \"172.29.216.15\",\n" +
                "                \"172.29.216.16\",\n" +
                "                \"172.29.216.17\",\n" +
                "                \"172.29.216.18\",\n" +
                "                \"172.29.216.19\",\n" +
                "                \"172.29.216.20\",\n" +
                "                \"172.29.216.21\",\n" +
                "                \"172.29.216.22\",\n" +
                "                \"172.29.216.23\",\n" +
                "                \"172.29.216.24\",\n" +
                "                \"172.29.216.25\",\n" +
                "                \"172.29.216.26\",\n" +
                "                \"172.29.216.27\",\n" +
                "                \"172.29.216.28\",\n" +
                "                \"172.29.216.29\",\n" +
                "                \"172.29.216.30\",\n" +
                "                \"172.29.216.31\",\n" +
                "                \"172.29.216.32\",\n" +
                "                \"172.29.216.33\",\n" +
                "                \"172.29.216.34\",\n" +
                "                \"172.29.216.35\",\n" +
                "                \"172.29.216.36\",\n" +
                "                \"172.29.216.37\",\n" +
                "                \"172.29.216.38\",\n" +
                "                \"172.29.216.39\",\n" +
                "                \"172.29.216.40\",\n" +
                "                \"172.29.216.41\",\n" +
                "                \"172.29.216.42\",\n" +
                "                \"172.29.216.43\",\n" +
                "                \"172.29.216.44\",\n" +
                "                \"172.29.216.45\",\n" +
                "                \"172.29.216.46\",\n" +
                "                \"172.29.216.47\",\n" +
                "                \"172.29.216.48\",\n" +
                "                \"172.29.216.49\",\n" +
                "                \"172.29.216.50\",\n" +
                "                \"172.29.216.51\",\n" +
                "                \"172.29.216.52\",\n" +
                "                \"172.29.216.53\",\n" +
                "                \"172.29.216.54\",\n" +
                "                \"172.29.216.55\",\n" +
                "                \"172.29.216.56\",\n" +
                "                \"172.29.216.57\",\n" +
                "                \"172.29.216.58\",\n" +
                "                \"172.29.216.59\",\n" +
                "                \"172.29.216.60\",\n" +
                "                \"172.29.216.61\",\n" +
                "                \"172.29.216.62\",\n" +
                "                \"172.29.216.63\",\n" +
                "                \"172.29.216.64\",\n" +
                "                \"172.29.216.65\",\n" +
                "                \"172.29.216.66\",\n" +
                "                \"172.29.216.67\",\n" +
                "                \"172.29.216.68\",\n" +
                "                \"172.29.216.69\"\n" +
                "            ],\n" +
                "            \"商用WEB-2\": [\n" +
                "                \"172.29.227.11\",\n" +
                "                \"172.29.227.12\",\n" +
                "                \"172.29.227.13\",\n" +
                "                \"172.29.227.14\",\n" +
                "                \"172.29.227.15\",\n" +
                "                \"172.29.227.16\",\n" +
                "                \"172.29.227.17\",\n" +
                "                \"172.29.227.18\",\n" +
                "                \"172.29.227.19\",\n" +
                "                \"172.29.227.20\",\n" +
                "                \"172.29.227.21\",\n" +
                "                \"172.29.227.22\",\n" +
                "                \"172.29.227.23\",\n" +
                "                \"172.29.227.24\",\n" +
                "                \"172.29.227.25\",\n" +
                "                \"172.29.227.26\",\n" +
                "                \"172.29.227.27\",\n" +
                "                \"172.29.227.28\",\n" +
                "                \"172.29.227.29\",\n" +
                "                \"172.29.227.30\",\n" +
                "                \"172.29.227.31\",\n" +
                "                \"172.29.227.32\",\n" +
                "                \"172.29.227.33\",\n" +
                "                \"172.29.227.34\",\n" +
                "                \"172.29.227.35\",\n" +
                "                \"172.29.227.36\",\n" +
                "                \"172.29.227.37\",\n" +
                "                \"172.29.227.38\",\n" +
                "                \"172.29.227.39\",\n" +
                "                \"172.29.227.40\",\n" +
                "                \"172.29.227.41\",\n" +
                "                \"172.29.227.42\",\n" +
                "                \"172.29.227.43\",\n" +
                "                \"172.29.227.44\",\n" +
                "                \"172.29.227.45\",\n" +
                "                \"172.29.227.46\",\n" +
                "                \"172.29.227.47\",\n" +
                "                \"172.29.227.48\",\n" +
                "                \"172.29.227.49\",\n" +
                "                \"172.29.227.50\",\n" +
                "                \"172.29.227.51\",\n" +
                "                \"172.29.227.52\",\n" +
                "                \"172.29.227.53\",\n" +
                "                \"172.29.227.54\",\n" +
                "                \"172.29.227.55\",\n" +
                "                \"172.29.227.56\",\n" +
                "                \"172.29.227.57\",\n" +
                "                \"172.29.227.58\",\n" +
                "                \"172.29.227.59\",\n" +
                "                \"172.29.227.60\",\n" +
                "                \"172.29.227.61\",\n" +
                "                \"172.29.227.62\",\n" +
                "                \"172.29.227.63\",\n" +
                "                \"172.29.227.64\",\n" +
                "                \"172.29.227.65\",\n" +
                "                \"172.29.227.66\",\n" +
                "                \"172.29.227.67\",\n" +
                "                \"172.29.227.68\",\n" +
                "                \"172.29.227.69\"\n" +
                "            ],\n" +
                "            \"商用WEB-3\": [\n" +
                "                \"172.29.233.11\",\n" +
                "                \"172.29.233.12\",\n" +
                "                \"172.29.233.13\",\n" +
                "                \"172.29.233.14\",\n" +
                "                \"172.29.233.15\",\n" +
                "                \"172.29.233.16\",\n" +
                "                \"172.29.233.17\",\n" +
                "                \"172.29.233.18\",\n" +
                "                \"172.29.233.19\",\n" +
                "                \"172.29.233.20\",\n" +
                "                \"172.29.233.21\",\n" +
                "                \"172.29.233.22\",\n" +
                "                \"172.29.233.23\",\n" +
                "                \"172.29.233.24\",\n" +
                "                \"172.29.233.25\",\n" +
                "                \"172.29.233.26\",\n" +
                "                \"172.29.233.27\",\n" +
                "                \"172.29.233.28\",\n" +
                "                \"172.29.233.29\",\n" +
                "                \"172.29.233.30\",\n" +
                "                \"172.29.233.31\",\n" +
                "                \"172.29.233.32\",\n" +
                "                \"172.29.233.33\",\n" +
                "                \"172.29.233.34\",\n" +
                "                \"172.29.233.35\",\n" +
                "                \"172.29.233.36\",\n" +
                "                \"172.29.233.37\",\n" +
                "                \"172.29.233.38\",\n" +
                "                \"172.29.233.39\",\n" +
                "                \"172.29.233.40\",\n" +
                "                \"172.29.233.41\",\n" +
                "                \"172.29.233.42\",\n" +
                "                \"172.29.233.43\",\n" +
                "                \"172.29.233.44\",\n" +
                "                \"172.29.233.45\",\n" +
                "                \"172.29.233.46\",\n" +
                "                \"172.29.233.47\",\n" +
                "                \"172.29.233.48\",\n" +
                "                \"172.29.233.49\",\n" +
                "                \"172.29.233.50\",\n" +
                "                \"172.29.233.51\",\n" +
                "                \"172.29.233.52\",\n" +
                "                \"172.29.233.53\",\n" +
                "                \"172.29.233.54\",\n" +
                "                \"172.29.233.55\",\n" +
                "                \"172.29.233.56\",\n" +
                "                \"172.29.233.57\",\n" +
                "                \"172.29.233.58\",\n" +
                "                \"172.29.233.59\",\n" +
                "                \"172.29.233.60\",\n" +
                "                \"172.29.233.61\",\n" +
                "                \"172.29.233.62\",\n" +
                "                \"172.29.233.63\",\n" +
                "                \"172.29.233.64\",\n" +
                "                \"172.29.233.65\",\n" +
                "                \"172.29.233.66\",\n" +
                "                \"172.29.233.67\",\n" +
                "                \"172.29.233.68\",\n" +
                "                \"172.29.233.69\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"电信网赛道\": {\n" +
                "            \"商用5G UDM\": [\n" +
                "                \"172.29.228.60\"\n" +
                "            ],\n" +
                "            \"黑盒拟态5G UDM\": [\n" +
                "                \"172.29.238.52\"\n" +
                "            ],\n" +
                "            \"商用IMS HSS\": [\n" +
                "                \"172.29.245.129\",\n" +
                "                \"172.29.245.10\"\n" +
                "            ],\n" +
                "            \"黑盒拟态IMS HSS\": [\n" +
                "                \"172.29.207.4\",\n" +
                "                \"172.29.214.4\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"密码机赛道\": {\n" +
                "            \"商用密码机A-1\": [\n" +
                "                \"172.29.210.222\"\n" +
                "            ],\n" +
                "            \"商用密码机A-2\": [\n" +
                "                \"172.29.209.20\"\n" +
                "            ],\n" +
                "            \"商用密码机A-3\": [\n" +
                "                \"172.29.230.245\"\n" +
                "            ],\n" +
                "            \"黑盒拟态密码机A\": [\n" +
                "                \"172.29.234.21\"\n" +
                "            ],\n" +
                "            \"商用密码机B\": [\n" +
                "                \"172.29.240.100\"\n" +
                "            ],\n" +
                "            \"黑盒拟态密码机B\": [\n" +
                "                \"172.29.247.110\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"车联网赛道\": {\n" +
                "            \"T-BOX-1\": [\n" +
                "                \"172.29.192.6\"\n" +
                "            ],\n" +
                "            \"T-BOX-2\": [\n" +
                "                \"172.29.195.6\"\n" +
                "            ],\n" +
                "            \"T-BOX-3\": [\n" +
                "                \"172.29.195.16\"\n" +
                "            ],\n" +
                "            \"T-BOX-4\": [\n" +
                "                \"172.29.206.6\"\n" +
                "            ],\n" +
                "            \"T-BOX-5\": [\n" +
                "                \"172.29.206.16\"\n" +
                "            ],\n" +
                "            \"T-BOX-6\": [\n" +
                "                \"172.29.192.16\"\n" +
                "            ],\n" +
                "            \"T-BOX-7\": [\n" +
                "                \"172.29.200.6\"\n" +
                "            ],\n" +
                "            \"T-BOX-8\": [\n" +
                "                \"172.29.200.16\"\n" +
                "            ],\n" +
                "            \"T-BOX-9\": [\n" +
                "                \"172.29.198.6\"\n" +
                "            ],\n" +
                "            \"T-BOX-10\": [\n" +
                "                \"172.29.199.6\"\n" +
                "            ],\n" +
                "            \"黑盒拟态T-BOX\": [\n" +
                "                \"172.29.205.2\"\n" +
                "            ],\n" +
                "            \"ADAS-1\": [\n" +
                "                \"192.168.1.250\"\n" +
                "            ],\n" +
                "            \"ADAS-2\": [\n" +
                "                \"172.29.223.11\"\n" +
                "            ],\n" +
                "            \"ADAS-3\": [\n" +
                "                \"192.168.0.233\"\n" +
                "            ],\n" +
                "            \"ADAS-4\": [\n" +
                "                \"172.29.225.35\"\n" +
                "            ],\n" +
                "            \"ADAS-5\": [\n" +
                "                \"172.29.224.28\"\n" +
                "            ],\n" +
                "            \"ADAS-6\": [\n" +
                "                \"172.29.226.43\"\n" +
                "            ],\n" +
                "            \"ADAS-7\": [\n" +
                "                \"172.29.218.59\"\n" +
                "            ],\n" +
                "            \"ADAS-8\": [\n" +
                "                \"172.29.220.33\"\n" +
                "            ],\n" +
                "            \"ADAS-9\": [\n" +
                "                \"172.29.220.23\"\n" +
                "            ],\n" +
                "            \"ADAS-10\": [\n" +
                "                \"172.29.219.66\"\n" +
                "            ],\n" +
                "            \"黑盒拟态ADAS\": [\n" +
                "                \"172.29.211.21\"\n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"white\": {\n" +
                "        \"互联网赛道\": {\n" +
                "            \"白盒拟态路由器-1\": [\n" +
                "                \"172.29.121.220\",\n" +
                "                \"172.29.121.220\"\n" +
                "            ],\n" +
                "            \"白盒拟态路由器-2\": [\n" +
                "                \"172.29.110.216\",\n" +
                "                \"172.29.110.216\"\n" +
                "            ],\n" +
                "            \"白盒拟态路由器-3\": [\n" +
                "                \"172.29.191.249\",\n" +
                "                \"172.29.191.249\"\n" +
                "            ],\n" +
                "            \"白盒拟态存储-1\": [\n" +
                "                \"172.29.208.101\",\n" +
                "                \"172.29.208.8\"\n" +
                "            ],\n" +
                "            \"白盒拟态存储-2\": [\n" +
                "                \"172.29.236.101\",\n" +
                "                \"172.29.236.8\"\n" +
                "            ],\n" +
                "            \"白盒拟态DNS-1\": [\n" +
                "                \"172.29.40.9\",\n" +
                "                \"172.29.40.15\",\n" +
                "                \"172.29.40.14\",\n" +
                "                \"172.29.40.13\",\n" +
                "                \"172.29.40.12\",\n" +
                "                \"172.29.40.11\"\n" +
                "            ],\n" +
                "            \"白盒拟态DNS-2\": [\n" +
                "                \"172.29.30.9\",\n" +
                "                \"172.29.30.15\",\n" +
                "                \"172.29.30.14\",\n" +
                "                \"172.29.30.13\",\n" +
                "                \"172.29.30.12\",\n" +
                "                \"172.29.30.11\"\n" +
                "            ],\n" +
                "            \"白盒拟态交换机-1\": [\n" +
                "                \"172.29.157.39\",\n" +
                "                \"172.29.157.39\"\n" +
                "            ],\n" +
                "            \"白盒拟态交换机-2\": [\n" +
                "                \"172.29.164.39\",\n" +
                "                \"172.29.164.39\"\n" +
                "            ],\n" +
                "            \"白盒拟态交换机-3\": [\n" +
                "                \"172.29.183.39\",\n" +
                "                \"172.29.183.39\"\n" +
                "            ],\n" +
                "            \"白盒拟态云\": [\n" +
                "                \"172.29.181.100\",\n" +
                "                \"172.29.181.110\"\n" +
                "            ],\n" +
                "            \"白盒拟态WEB-2\": [\n" +
                "                \"172.29.117.60\",\n" +
                "                \"172.29.117.65\"\n" +
                "            ],\n" +
                "            \"白盒拟态WEB-3\": [\n" +
                "                \"172.29.165.60\",\n" +
                "                \"172.29.165.65\"\n" +
                "            ],\n" +
                "            \"白盒拟态WEB-1\": [\n" +
                "                \"172.29.113.60\",\n" +
                "                \"172.29.113.65\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"电信网赛道\": {\n" +
                "            \"白盒拟态5G UDM-1\": [\n" +
                "                \"172.29.12.101\",\n" +
                "                \"172.29.12.110\"\n" +
                "            ],\n" +
                "            \"白盒拟态5G UDM-2\": [\n" +
                "                \"172.29.24.159\",\n" +
                "                \"172.29.24.153\"\n" +
                "            ],\n" +
                "            \"白盒拟态IMS HSS-1\": [\n" +
                "                \"172.29.17.4\",\n" +
                "                \"172.29.19.4\",\n" +
                "                \"172.29.17.15\"\n" +
                "            ],\n" +
                "            \"白盒拟态IMS HSS-2\": [\n" +
                "                \"172.29.10.6\",\n" +
                "                \"172.29.18.6\",\n" +
                "                \"172.29.10.17\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"密码机赛道\": {\n" +
                "            \"白盒拟态密码机A-1\": [\n" +
                "                \"172.29.88.41\",\n" +
                "                \"172.29.88.42\"\n" +
                "            ],\n" +
                "            \"白盒拟态密码机A-2\": [\n" +
                "                \"172.29.92.51\",\n" +
                "                \"172.29.92.52\"\n" +
                "            ],\n" +
                "            \"白盒拟态密码机A-3\": [\n" +
                "                \"172.29.103.61\",\n" +
                "                \"172.29.103.62\"\n" +
                "            ]\n" +
                "        },\n" +
                "        \"车联网赛道\": {\n" +
                "            \"白盒拟态T-BOX-1\": [\n" +
                "                \"172.29.131.2\",\n" +
                "                \"172.29.131.2\"\n" +
                "            ],\n" +
                "            \"白盒拟态T-BOX-2\": [\n" +
                "                \"172.29.136.2\",\n" +
                "                \"172.29.136.2\"\n" +
                "            ],\n" +
                "            \"白盒拟态T-BOX-3\": [\n" +
                "                \"172.29.142.2\",\n" +
                "                \"172.29.142.2\"\n" +
                "            ],\n" +
                "            \"白盒拟态ADAS-1\": [\n" +
                "                \"172.29.182.11\",\n" +
                "                \"172.29.182.11\"\n" +
                "            ],\n" +
                "            \"白盒拟态ADAS-2\": [\n" +
                "                \"172.29.173.12\",\n" +
                "                \"172.29.173.12\"\n" +
                "            ],\n" +
                "            \"白盒拟态ADAS-3\": [\n" +
                "                \"172.29.177.13\",\n" +
                "                \"172.29.177.13\"\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }
}
