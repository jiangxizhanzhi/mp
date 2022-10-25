package cn.cyberpeace.mp.collectionapi.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.collection.bo.CountBO;
import cn.cyberpeace.mp.collection.service.FlowInfoService;
import cn.cyberpeace.mp.collectionapi.config.ContestConfig;
import cn.cyberpeace.mp.collectionapi.config.ContestConfig.Team;
import cn.cyberpeace.mp.collectionapi.util.ContestConfigUtil;
import cn.cyberpeace.mp.collectionapi.vo.AttackEquipmentVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackLevelVO.Count;
import cn.cyberpeace.mp.collectionapi.vo.AttackProtocolVO;
import cn.cyberpeace.mp.collectionapi.vo.AttackTypeVO;
import cn.cyberpeace.mp.collectionapi.vo.FlowAttackVO;
import cn.cyberpeace.mp.collectionapi.vo.TeamAttackCountVO;

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
	 * 攻击次数(累计) top10的队伍
	 * 
	 * @return
	 */
	public ListObjServiceResult<TeamAttackCountVO> queryTeamAttackCountTop10() {
		Map<String, String> ip2TeamMap = ContestConfig.teams.stream()
				.collect(Collectors.toMap(Team::getIp, Team::getName, (v1, v2) -> v2));
		List<CountBO> bos = flowInfoService.queryTeamAttackCount(Lists.newArrayList(ip2TeamMap.keySet()));
		List<TeamAttackCountVO> vos = bos.stream().map(bo -> {
			TeamAttackCountVO vo = new TeamAttackCountVO();
			vo.setTeam(ip2TeamMap.get(bo.getKey()));
			vo.setCount(bo.getCount());
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 所有队伍的攻击类型占比top5
	 * 
	 * @return
	 */
	public ListObjServiceResult<AttackTypeVO> queryAttackTypeTop5() {
		List<String> ips = ContestConfig.teams.stream().map(Team::getIp).collect(Collectors.toList());
		List<CountBO> bos = flowInfoService.queryAttackType(ips);
		List<AttackTypeVO> vos = bos.stream().map(bo -> {
			AttackTypeVO vo = new AttackTypeVO();
			vo.setName(bo.getKey());
			vo.setCount(bo.getCount());
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 所有队伍的总体攻击次数
	 * 
	 * @return
	 */
	public ListObjServiceResult<FlowAttackVO> queryAttackTrend() {
		List<String> ips = ContestConfig.teams.stream().map(Team::getIp).collect(Collectors.toList());
		List<CountBO> bos = flowInfoService.queryAttackTrend(ips);
		List<FlowAttackVO> vos = bos.stream().map(bo -> {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(Long.valueOf(bo.getKey()));

			FlowAttackVO vo = new FlowAttackVO();
			vo.setTime(LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault()));
			vo.setAttackCount(bo.getCount());
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
		List<String> ips = ContestConfig.teams.stream().map(Team::getIp).collect(Collectors.toList());
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

			List<CountBO> bos = flowInfoService.queryAttackLevel(ips, severities);
			List<Count> counts = bos.stream().map(bo -> {
				Calendar time = Calendar.getInstance();
				time.setTimeInMillis(Long.valueOf(bo.getKey()));

				Count vo = new Count();
				vo.setTime(LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault()));
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
	 * 设备攻击协议TOP5
	 * 
	 * @return
	 */
	public ListObjServiceResult<AttackProtocolVO> queryProtocolTop5() {
		List<String> ips = ContestConfigUtil.getIpsByType(null, null);
		List<CountBO> bos = flowInfoService.queryProtocolTop5(ips);
		double total = bos.stream().mapToLong(CountBO::getCount).sum();
		List<AttackProtocolVO> vos = bos.stream().map(bo -> {
			Calendar time = Calendar.getInstance();
			time.setTimeInMillis(Long.valueOf(bo.getKey()));

			AttackProtocolVO vo = new AttackProtocolVO();
			vo.setProtocol(bo.getKey());
			vo.setPercent(bo.getCount() / total);
			return vo;
		}).collect(Collectors.toList());

		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

	/**
	 * 被攻击设备类型TOP5
	 * 
	 * @return
	 */
	public ListObjServiceResult<AttackEquipmentVO> queryEquipmentTypeTop5() {
		List<CountBO> bos = flowInfoService.queryEquipmentIpAttackCount(ContestConfigUtil.getIpsByType(null, null));
		Map<String, AttackEquipmentVO> vosMap = Maps.newHashMap();
		bos.forEach(bo -> {
			String name = ContestConfigUtil.getTpyeByIp(bo.getKey());
			AttackEquipmentVO vo = vosMap.computeIfAbsent(name, key -> new AttackEquipmentVO());
			vo.setType(name);
			vo.setCount(vo.getCount() + bo.getCount());
		});

		final int TOP_NUM = 5;
		List<AttackEquipmentVO> vos = vosMap.values().stream().collect(Collectors.toList());
		vos.sort(Comparator.comparing(AttackEquipmentVO::getCount).reversed());
		vos = vos.size() < TOP_NUM ? vos : vos.subList(0, TOP_NUM);
		double total = vos.stream().mapToLong(AttackEquipmentVO::getCount).sum();
		vos.forEach(v -> {
			v.setPercent(v.getCount() / total);
		});
		return new ListObjServiceResult<>(new ListObjResult<>(vos, vos.size())).asResult();
	}

}
