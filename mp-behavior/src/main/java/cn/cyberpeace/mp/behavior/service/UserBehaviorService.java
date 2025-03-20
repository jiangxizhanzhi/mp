 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkBehaviorService.java.
 // File size 3136.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.service;

 import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
 import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
 import cn.cyberpeace.mp.behavior.bo.NetworkBehaviorBo;
import cn.cyberpeace.mp.behavior.bo.NodeNetworkBehavior;
import cn.cyberpeace.mp.behavior.bo.NodeUserBehavior;
import cn.cyberpeace.mp.behavior.bo.Param;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.NetworkBehaviorMapper;
import cn.cyberpeace.mp.behavior.repository.UserBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.AddUserBehaviorReq;
import cn.cyberpeace.mp.behavior.req.UpdateUserBehaviorReq;
import cn.cyberpeace.mp.behavior.res.NetworkBehaviorBoRes;
import cn.cyberpeace.mp.behavior.res.UserBehaviorDetailRes;
import cn.cyberpeace.mp.behavior.res.UserBehaviorPageRes;
import cn.cyberpeace.mp.behavior.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

 /**
 * @ClassName UserBehaviorService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class UserBehaviorService {

	@Autowired
	private UserBehaviorMapper userBehaviorMapper;

	@Autowired
	private NetworkBehaviorMapper networkBehaviorMapper;

	/**
	 * add
	 *
	 * @param addReq
	 * @return
	 */
	public String add(AddUserBehaviorReq addReq) {
		UserBehaviorModel model = new UserBehaviorModel();
		BeanUtils.copyProperties(addReq, model);
		model.setBehaviors(JSONObject.toJSONString(addReq.getBehaviorList()));
		model.setStartTime(TimeUtil.toLocalTime(addReq.getStartTime()));
		model.setEndTime(TimeUtil.toLocalTime(addReq.getEndTime()));
		userBehaviorMapper.create(model);
		return model.getId();
	}

	/**
	 * update
	 *
	 * @param updateReq
	 */
	public void update(UpdateUserBehaviorReq updateReq) {
		UserBehaviorModel model = new UserBehaviorModel();
		BeanUtils.copyProperties(updateReq, model);
		model.setBehaviors(JSONObject.toJSONString(updateReq.getBehaviorList()));
		if (StringUtils.isNotEmpty(updateReq.getStartTime())) {
			model.setStartTime(TimeUtil.toLocalTime(updateReq.getStartTime()));
		}
		if (StringUtils.isNotEmpty(updateReq.getEndTime())) {
			model.setEndTime(TimeUtil.toLocalTime(updateReq.getEndTime()));
		}
		userBehaviorMapper.update(model);
	}

	/**
	 * delete
	 *
	 * @param ids
	 */
	public void delete(List<String> ids) {
		userBehaviorMapper.bulkDelete(ids.toArray(new String[0]));
	}

	/**
	 * get
	 *
	 * @param id
	 * @return
	 */
	public UserBehaviorDetailRes get(String id) {
		UserBehaviorDetailRes userBehaviorDetailRes = new UserBehaviorDetailRes();
		UserBehaviorModel userBehaviorModel = userBehaviorMapper.get(id);
		if (userBehaviorModel == null) {
			log.error("cannot find userBehavior ,id = [{}]", id);
			throw new MpRuntimeException("找不到对应的用户行为");
		}
		BeanUtils.copyProperties(userBehaviorModel, userBehaviorDetailRes);
		List<String> networkIds = JSONObject.parseArray(userBehaviorModel.getBehaviors(), NetworkBehaviorBo.class)
											.stream()
											.map(NetworkBehaviorBo::getNetworkBehaviorId)
											.collect(Collectors.toList());
		List<NetworkBehaviorModel> networkBehaviorModelList = networkBehaviorMapper.list(new HumanCondition()
				.filterIn(NetworkBehaviorModel::getId, networkIds.toArray()));
		Map<String, NetworkBehaviorModel> networkBehaviorModelMap = networkBehaviorModelList.stream()
																							.collect(Collectors.toMap(NetworkBehaviorModel::getId, m -> m));
		Map<String, String> runTimeMap = JSONObject.parseArray(userBehaviorModel.getBehaviors(), NetworkBehaviorBo.class)
													  .stream()
													  .collect(Collectors.toMap(NetworkBehaviorBo::getNetworkBehaviorId, NetworkBehaviorBo::getRunTime));
		List<NetworkBehaviorBoRes> networkBehaviorBoResList = new ArrayList<>();
		for (String networkId : networkIds) {
			NetworkBehaviorBoRes networkBehaviorBoRes = new NetworkBehaviorBoRes();
			BeanUtils.copyProperties(networkBehaviorModelMap.get(networkId), networkBehaviorBoRes);
			networkBehaviorBoRes.setRunTime(runTimeMap.get(networkId));
			networkBehaviorBoResList.add(networkBehaviorBoRes);
		}
		userBehaviorDetailRes.setBehaviorList(networkBehaviorBoResList);
		userBehaviorDetailRes.setStartTime(TimeUtil.format(userBehaviorModel.getStartTime()));
		userBehaviorDetailRes.setEndTime(TimeUtil.format(userBehaviorModel.getEndTime()));
		return userBehaviorDetailRes;
	}

	/**
	 * page
	 *
	 * @param pageReq
	 * @return
	 */
	public ListObjServiceResult<UserBehaviorPageRes> page(PageRequest pageReq) {
		pageReq.ensure();
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());

		if (StringUtils.isNotEmpty(pageReq.getSearch())) {
			humanCondition.filterIContains(UserBehaviorModel::getName, pageReq.getSearch());
		}

		List<UserBehaviorModel> modelList = userBehaviorMapper.list(humanCondition);
		int total = userBehaviorMapper.total(humanCondition);
		List<UserBehaviorPageRes> networkBehaviorPageResList = modelList.stream().map(this::UserBehaviorModel2Page).collect(Collectors.toList());
		return new ListObjServiceResult(new ListObjResult(networkBehaviorPageResList, total).asResult());
	}

	public ListObjServiceResult<NodeUserBehavior> scenePage(PageRequest pageReq) {
		pageReq.ensure();
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());
		List<UserBehaviorModel> modelList = userBehaviorMapper.list(humanCondition);
		int	total = userBehaviorMapper.total(humanCondition);
		Set<String> networkBehaviorIds = new HashSet<>();
		for (UserBehaviorModel model : modelList) {
			List<NetworkBehaviorBo> networkBehaviorBos = JSONObject.parseArray(model.getBehaviors(), NetworkBehaviorBo.class);
			networkBehaviorIds.addAll(networkBehaviorBos.stream().map(NetworkBehaviorBo::getNetworkBehaviorId).collect(Collectors.toSet()));
		}
		Map<String, NetworkBehaviorModel> networkBehaviorModelMap = new HashMap<>();
		if (CollectionUtil.isNotEmpty(networkBehaviorIds)) {
			HumanCondition networkHumanCondition = new HumanCondition().filterIn(NetworkBehaviorModel::getId, networkBehaviorIds.toArray());
			List<NetworkBehaviorModel> networkBehaviorModelList = networkBehaviorMapper.list(networkHumanCondition);
			networkBehaviorModelMap = networkBehaviorModelList.stream()
															  .collect(Collectors.toMap(NetworkBehaviorModel::getId, m -> m));
		}
		List<NodeUserBehavior> nodeUserBehaviors = new ArrayList<>();
		for (UserBehaviorModel model : modelList) {
			NodeUserBehavior nodeUserBehavior = new NodeUserBehavior();
			nodeUserBehavior.setUserBehaviorId(model.getId());
			nodeUserBehavior.setUserBehaviorName(model.getName());
			nodeUserBehavior.setStartTime(TimeUtil.format(model.getStartTime()));
			nodeUserBehavior.setEndTime(TimeUtil.format(model.getEndTime()));
			List<NetworkBehaviorBo> networkBehaviorBoList = JSONObject.parseArray(model.getBehaviors(), NetworkBehaviorBo.class);
			nodeUserBehavior.setBehaviorCount(networkBehaviorBoList.size());
			List<NodeNetworkBehavior> nodeNetworkBehaviorList = new ArrayList<>();
			for (NetworkBehaviorBo networkBehaviorBo : networkBehaviorBoList) {
				NodeNetworkBehavior nodeNetworkBehavior = new NodeNetworkBehavior();
				NetworkBehaviorModel networkBehaviorModel = networkBehaviorModelMap.get(networkBehaviorBo.getNetworkBehaviorId());
				BeanUtils.copyProperties(networkBehaviorModel, nodeNetworkBehavior);
				nodeNetworkBehavior.setNetworkBehaviorId(networkBehaviorModel.getId());
				nodeNetworkBehavior.setFileUrl(networkBehaviorModel.getFilePath());
				nodeNetworkBehavior.setParamList(JSONObject.parseArray(networkBehaviorModel.getParams(), Param.class));
				nodeNetworkBehavior.setRunTime(networkBehaviorBo.getRunTime());
				nodeNetworkBehaviorList.add(nodeNetworkBehavior);
			}
			nodeUserBehavior.setNetworkBehaviorList(nodeNetworkBehaviorList);
			nodeUserBehaviors.add(nodeUserBehavior);
		}
		return new ListObjServiceResult(new ListObjResult(nodeUserBehaviors, total).asResult());
	}

	private UserBehaviorPageRes UserBehaviorModel2Page(UserBehaviorModel userBehaviorModel) {
		UserBehaviorPageRes userBehaviorPageRes = new UserBehaviorPageRes();
		BeanUtils.copyProperties(userBehaviorModel, userBehaviorPageRes);
		userBehaviorPageRes.setCount(JSONObject.parseArray(userBehaviorModel.getBehaviors(), NetworkBehaviorBo.class).size());
		userBehaviorPageRes.setStartTime(TimeUtil.format(userBehaviorModel.getStartTime()));
		userBehaviorPageRes.setEndTime(TimeUtil.format(userBehaviorModel.getEndTime()));
		return userBehaviorPageRes;
	}
}
