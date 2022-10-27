package cn.cyberpeace.mp.collectionapi.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.collection.bo.AttackFlowBO;
import cn.cyberpeace.mp.collection.bo.AttackTypeBO;
import cn.cyberpeace.mp.collection.bo.CountBO;
import cn.cyberpeace.mp.collection.bo.ESIPFieldBo;
import cn.cyberpeace.mp.collection.bo.FlowBO;
import cn.cyberpeace.mp.collection.bo.TeamAttackCountBO;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collectionapi.request.AttackAggTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest.IPField;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackTargetTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackTargetTopRequest.Source;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest.AggType;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO.AttackCount;
import cn.cyberpeace.mp.collectionapi.vo.AggAttackFlowVO.FlowSize;
import cn.cyberpeace.mp.collectionapi.vo.AttackFlowTopVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO.Count;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackFlowTopVO.AttackFlowData;
import cn.cyberpeace.mp.collectionapi.vo.AttackTargetTopVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackTargetVO;
import cn.cyberpeace.mp.collectionapi.vo.FlowVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackFlowTopVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackTrendVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackTrendVO.TeamAttackCount;
import cn.hutool.core.collection.CollectionUtil;

/**
 * @Description:
 * @author zhangjiaxin
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
	public ListObjServiceResult<AttackFlowTopVO> queryAttackTrend() {
		List<CountBO> bos = flowInfoService.queryAttackTrend();
		List<AttackFlowTopVO> vos = bos.stream().map(bo -> {
			AttackFlowTopVO vo = new AttackFlowTopVO();
			vo.setName(bo.getKey());
			vo.setCount(bo.getCount());
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 攻击告警级别趋势
	 * 
	 * @return
	 */
	public ListObjServiceResult<AttackLevelVO> queryLevelTrend() {
		List<AttackLevelVO> vos = Lists.newArrayList();
		Map<String, List<CountBO>> severityMap = flowInfoService.queryAttackLevel();
		Map<String, AttackLevelVO> resultMap = Maps.newHashMap();

		for (int level = 1; level <= 4; level++) {
			String levelName = getSeverityDisplayName(String.valueOf(level));
			AttackLevelVO vo = resultMap.computeIfAbsent(levelName, k -> new AttackLevelVO());
			vo.setSeverity(levelName);
			vos.add(vo);

			List<CountBO> bos = severityMap.get(String.valueOf(level));
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

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 战队攻击趋势
	 * 
	 * @return
	 */
	public ListObjServiceResult<TeamAttackTrendVO> teamAttackTrend(AttackFlowBaseRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		Map<String, List<CountBO>> ipMap = flowInfoService.queryTeamAttackTrend(esipFieldBos);
		Map<String, TeamAttackTrendVO> resultMap = Maps.newHashMap();

		List<TeamAttackTrendVO> vos = Lists.newArrayList();
		ipMap.entrySet().forEach(entry -> {
			String team = getNameByIp(request, entry.getKey());
			TeamAttackTrendVO vo = resultMap.computeIfAbsent(team, k -> new TeamAttackTrendVO());
			vo.setName(team);
			vos.add(vo);

			List<CountBO> bos = entry.getValue();
			if (bos != null) {
				List<TeamAttackCount> countsAdd = bos.stream().map(bo -> {
					TeamAttackCount c = new TeamAttackCount();
					c.setTime(bo.getKey());
					c.setSize(bo.getCount());
					return c;
				}).collect(Collectors.toList());

				List<TeamAttackCount> countsOrig = vo.getTeamAttackCounts();
				if (countsOrig == null) {
					vo.setTeamAttackCounts(countsAdd);
				} else {
					int size = countsOrig.size();
					for (int i = 0; i < size; i++) {
						TeamAttackCount c = countsOrig.get(i);
						c.setSize(c.getSize() + countsAdd.get(i).getSize());
					}
				}
			}
		});

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 攻击次数与流量大小（战队 or 设备）
	 * 
	 * @return
	 */
	public ListObjServiceResult<AggAttackFlowVO> getAttackFlow(AttackFlowBaseRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		List<AttackFlowBO> bos = flowInfoService.queryAttackFlow(esipFieldBos);

		List<AggAttackFlowVO> vos = bos.stream().map(bo -> {
			AttackCount attackCount = new AttackCount();
			attackCount.setAttackCount(bo.getCount());
			FlowSize flowSize = new FlowSize();
			flowSize.setSize(bo.getSize());

			AggAttackFlowVO vo = new AggAttackFlowVO();
			vo.setName(getNameByIp(request, bo.getIp()));
			vo.setAttackCount(attackCount);
			vo.setFlowSize(flowSize);
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
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
		List<TeamAttackCountVO> vos = getTeamAttackAggTop(request).getSource().getList();
		List<AttackFlowTopVO> topVOs = vos.stream().map(vo -> {
			AttackFlowTopVO topVO = new AttackFlowTopVO();
			topVO.setName(vo.getTeam());
			topVO.setCount(vo.getCount());
			return topVO;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(topVOs, topVOs.size())).asResult();
	}

	/**
	 * 攻击手段占比top5（全部队伍）
	 * 
	 * @return
	 */
	private ListObjServiceResult<AttackFlowTopVO> queryAttackTypeTop5(AttackFlowTopRequest request) {
		List<CountBO> bos = flowInfoService.queryAttackType(request.getTopSize());
		double total = bos.get(0).getCount();
		List<AttackFlowTopVO> vos = bos.subList(1, bos.size()).stream().map(bo -> {
			AttackFlowTopVO vo = new AttackFlowTopVO();
			vo.setName(bo.getKey());
			vo.setPercent(bo.getCount() / total);
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 设备攻击协议TOP5
	 * 
	 * @return
	 */
	private ListObjServiceResult<AttackFlowTopVO> queryProtocolTop5(AttackFlowTopRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		List<CountBO> bos = flowInfoService.queryProtocolTop5(esipFieldBos, request.getTopSize());
		double total = bos.stream().mapToLong(CountBO::getCount).sum();
		List<AttackFlowTopVO> vos = bos.stream().map(bo -> {
			AttackFlowTopVO vo = new AttackFlowTopVO();
			vo.setName(bo.getKey());
			vo.setPercent(bo.getCount() / total);
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 设备类型被攻击占比TOP5
	 * 
	 * @return
	 */
	private ListObjServiceResult<AttackFlowTopVO> queryEquipmentTypeTop5(AttackFlowTopRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		List<CountBO> bos = flowInfoService.queryEquipmentIpAttackCount(esipFieldBos);
		Map<String, AttackFlowTopVO> vosMap = Maps.newHashMap();
		bos.forEach(bo -> {
			String name = getNameByIp(request, bo.getKey());
			AttackFlowTopVO vo = vosMap.computeIfAbsent(name, key -> new AttackFlowTopVO());
			vo.setName(name);
			vo.setCount(vo.getCount() + bo.getCount());
		});

		// // 排序取top5
		List<AttackFlowTopVO> vos = vosMap.values().stream().collect(Collectors.toList());
		vos.sort(Comparator.comparing(AttackFlowTopVO::getCount).reversed());
		vos = vos.size() < request.getTopSize() ? vos : vos.subList(0, request.getTopSize());
		double total = vos.stream().mapToLong(AttackFlowTopVO::getCount).sum();
		vos.forEach(v -> {
			v.setPercent(v.getCount() / total);
		});

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 总攻击次数
	 * 
	 * @param multiple 放大倍数
	 * @return
	 */
	public ServiceResult<Long> getAttackTotalCount(Integer multiple) {
		return new ServiceResult<Long>(flowInfoService.attackTotalCount() * multiple);
	}

	/**
	 * 总攻击次数
	 * 
	 * @param multiple 放大倍数
	 * @return
	 */
	public ListObjServiceResult<TeamAttackCountVO> getTeamAttackAggTop(AttackAggTopRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
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
		vos = vos.size() < request.getTopSize() ? vos : vos.subList(0, request.getTopSize());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 攻击流量列表
	 * 
	 * @param request
	 * @return
	 */
	public ListObjServiceResult<FlowVO> attackFlowList(Integer pageNum, Integer pageSize) {
		List<FlowBO> bos = flowInfoService.attackFlowList(pageNum, pageSize);
		List<FlowVO> vos = bos.stream().map(bo -> {
			FlowVO vo = new FlowVO();
			BeanUtils.copyProperties(bo, vo);
			vo.setSeverity(getSeverityDisplayName(vo.getSeverity()));
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 获取攻击目标top数据
	 * 
	 * @param request
	 * @return
	 */
	public ListObjServiceResult<AttackTargetTopVO> getAttackTargetTop(AttackTargetTopRequest request) {
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
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(baseRequest);
		Map<String, List<CountBO>> ipMap = flowInfoService.queryTeamAttackTarget(esipFieldBos);

		Map<String, AttackTargetTopVO> team2TargetMap = Maps.newHashMap();
		ipMap.entrySet().forEach(entry -> {
			String team = getNameByIp(sourceFields, entry.getKey());
			AttackTargetTopVO vo = team2TargetMap.computeIfAbsent(team, key -> new AttackTargetTopVO());
			vo.setName(team);

			List<AttackTargetVO> targets = vo.getTargets();
			if (targets == null) {
				targets = Lists.newArrayList();
				vo.setTargets(targets);
			}

			for (CountBO bo : entry.getValue()) {
				String targetName = getNameByIp(targetFields, bo.getKey());
				AttackTargetVO targetVO = targets.stream().filter(t -> targetName.equals(t.getName())).findFirst()
						.orElse(null);
				if (targetVO == null) {
					targetVO = new AttackTargetVO();
					targets.add(targetVO);
				}
				targetVO.setCount(targetVO.getCount() + bo.getCount());
			}
		});

		// 排序取topSize
		List<AttackTargetTopVO> vos = new ArrayList<>(team2TargetMap.values());
		vos.forEach(vo -> {
			List<AttackTargetVO> targets = vo.getTargets();
			targets.sort(Comparator.comparing(AttackTargetVO::getCount).reversed());
			targets = targets.size() < request.getTopSize() ? targets : targets.subList(0, request.getTopSize());
			vo.setTargets(targets);
		});

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 攻击手段占比top5（指定队伍）
	 * 
	 * @return
	 */
	public ListObjServiceResult<TeamAttackFlowTopVO> getAttackTypeTop(AttackAggTopRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		List<AttackFlowBO> bos = flowInfoService.queryAttackType(esipFieldBos);

		Map<String, TeamAttackFlowTopVO> vosMap = Maps.newHashMap();
		bos.forEach(bo -> {
			String name = getNameByIp(request, bo.getIp());
			TeamAttackFlowTopVO vo = vosMap.computeIfAbsent(name, key -> new TeamAttackFlowTopVO());
			vo.setName(name);
			
			List<AttackFlowData> attackFlowDatas = vo.getAttackFlowDatas();
			if(attackFlowDatas == null) {
				attackFlowDatas = Lists.newArrayList();
				vo.setAttackFlowDatas(attackFlowDatas);
			}
			
			for(AttackTypeBO attackTypeBO : bo.getAttackTypes()) {
				AttackFlowData data = attackFlowDatas.stream().filter(d -> attackTypeBO.getEnName().equals(d.getFiledName())).findFirst().orElse(null);
				if(data == null) {
					data = new AttackFlowData();
					data.setFiledName(attackTypeBO.getEnName());
					attackFlowDatas.add(data);
				}
				data.setCount(data.getCount() + attackTypeBO.getCount());
			}
		});

		// 排序取topSize
		List<TeamAttackFlowTopVO> vos = new ArrayList<>(vosMap.values());
		vos.forEach(vo -> {
			List<AttackFlowData> attackFlowDatas = vo.getAttackFlowDatas();
			attackFlowDatas.sort(Comparator.comparing(AttackFlowData::getCount).reversed());
			attackFlowDatas = attackFlowDatas.size() < request.getTopSize() ? attackFlowDatas : attackFlowDatas.subList(0, request.getTopSize());
			vo.setAttackFlowDatas(attackFlowDatas);
		});

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 生成ip字段的搜索项
	 * 
	 * @param request
	 * @return
	 */
	private List<ESIPFieldBo> genIPFieldBos(AttackFlowBaseRequest request) {
		List<ESIPFieldBo> bos = Lists.newArrayList();

		ESIPFieldBo.Field ipField = CollectionUtil.isNotEmpty(request.getSources()) ? ESIPFieldBo.Field.SOURCE
				: ESIPFieldBo.Field.DESTINATION;
		List<IPField> ipFields = CollectionUtil.isNotEmpty(request.getSources()) ? request.getSources()
				: request.getTargets();

		for (IPField field : ipFields) {
			ESIPFieldBo bo = new ESIPFieldBo();
			bo.setField(ipField);
			bos.add(bo);

			if (CollectionUtil.isNotEmpty(field.getIps())) {
				bo.setFilterType(ESIPFieldBo.FilterType.LIST);
				bo.setIps(field.getIps());
			} else {
				bo.setFilterType(ESIPFieldBo.FilterType.PREFIX);
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
		List<IPField> ipFields = CollectionUtil.isNotEmpty(request.getSources()) ? request.getSources()
				: request.getTargets();
		return getNameByIp(ipFields, ip);
	}

	/**
	 * 根据ip判断所属队伍
	 * 
	 * @param request
	 * @param ip
	 * @return
	 */
	private String getNameByIp(List<IPField> ipFields, String ip) {
		for (IPField field : ipFields) {
			if (CollectionUtil.isNotEmpty(field.getIps()) && field.getIps().contains(ip)) {
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
