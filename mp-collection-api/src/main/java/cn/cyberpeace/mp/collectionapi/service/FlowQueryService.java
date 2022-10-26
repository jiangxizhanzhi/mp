package cn.cyberpeace.mp.collectionapi.service;

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
import cn.cyberpeace.mp.collection.bo.CountBO;
import cn.cyberpeace.mp.collection.bo.ESIPFieldBo;
import cn.cyberpeace.mp.collection.bo.TeamAttackCountBO;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collectionapi.request.AttackAggTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowBaseRequest.IPField;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest;
import cn.cyberpeace.mp.collectionapi.request.AttackFlowTopRequest.AggType;
import cn.cyberpeace.mp.collectionapi.vo.AttackFlowTopVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO.Count;
import cn.cyberpeace.mp.collectionapi.vo.FlowVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;
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
	public ListObjServiceResult<AttackFlowTopVO> queryAttackTrend(AttackFlowBaseRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		List<CountBO> bos = flowInfoService.queryAttackTrend(esipFieldBos);
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
	public ListObjServiceResult<AttackLevelVO> queryLevelTrend(AttackFlowBaseRequest request) {
		List<AttackLevelVO> vos = Lists.newArrayList();
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		for (int i = 0; i < 3; i++) {
			List<Long> severities = null;
			String severity = null;
			if (i == 0) {
				severities = Lists.newArrayList(1L);
				severity = "高级";
			} else if (i == 1) {
				severities = Lists.newArrayList(2L);
				severity = "中级";
			} else {
				severities = Lists.newArrayList(3L, 4L);
				severity = "低级";
			}

			List<CountBO> bos = flowInfoService.queryAttackLevel(esipFieldBos, severities);
			List<Count> counts = bos.stream().map(bo -> {
				Count vo = new Count();
				vo.setTime(bo.getKey());
				vo.setNum(bo.getCount());
				return vo;
			}).collect(Collectors.toList());

			AttackLevelVO vo = new AttackLevelVO();
			vo.setSeverity(severity);
			vo.setCounts(counts);
			vos.add(vo);
		}

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 获取top数据
	 * 
	 * @param request
	 * @return
	 */
	public ListObjServiceResult<AttackFlowTopVO> getAttackTop(AttackFlowTopRequest request) {
		Integer aggType = request.getAggType();
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
	 * 攻击手段占比top5
	 * 
	 * @return
	 */
	private ListObjServiceResult<AttackFlowTopVO> queryAttackTypeTop5(AttackFlowTopRequest request) {
		List<ESIPFieldBo> esipFieldBos = genIPFieldBos(request);
		List<CountBO> bos = flowInfoService.queryAttackType(esipFieldBos, request.getTopSize());
		double total = flowInfoService.attackTotalCount();
		List<AttackFlowTopVO> vos = bos.stream().map(bo -> {
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
		List<TeamAttackCountVO> vos = name2CountMap.values().stream().collect(Collectors.toList());
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
	public ListObjServiceResult<FlowVO> attackFlowList(AttackFlowBaseRequest request) {
		return null;
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

}
