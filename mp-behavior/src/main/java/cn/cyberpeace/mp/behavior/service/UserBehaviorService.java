 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserBehaviorService.java.
 // File size 6065.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.NetworkBehaviorMapper;
import cn.cyberpeace.mp.behavior.repository.UserBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.AddUserBehaviorReq;
import cn.cyberpeace.mp.behavior.req.UpdateUserBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CodeNameRes;
import cn.cyberpeace.mp.behavior.res.UserBehaviorDetail;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
		model.setStartTime(TimeUtils.stringToLocalDateTime(addReq.getStartTime()));
		model.setEndTime(TimeUtils.stringToLocalDateTime(addReq.getEndTime()));
		model.setNetworkIds(JSON.toJSONString(addReq.getNetworkIdList()));
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
		if (StringUtils.isNotEmpty(updateReq.getStartTime())) {
			model.setStartTime(TimeUtils.stringToLocalDateTime(updateReq.getStartTime()));
		}
		if (StringUtils.isNotEmpty(updateReq.getEndTime())) {
			model.setEndTime(TimeUtils.stringToLocalDateTime(updateReq.getEndTime()));
		}
		if (CollectionUtils.isNotEmpty(updateReq.getNetworkIdList())) {
			model.setNetworkIds(JSON.toJSONString(updateReq.getNetworkIdList()));
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
	 * page
	 *
	 * @param pageReq
	 * @return
	 */
	public ListObjServiceResult<UserBehaviorDetail> page(PageRequest pageReq) {
		pageReq.ensure();
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());

		if (StringUtils.isNotEmpty(pageReq.getSearch())) {
			humanCondition.filterIContains(UserBehaviorModel::getName, pageReq.getSearch());
		}

		List<UserBehaviorModel> userModelList = userBehaviorMapper.list(humanCondition);
		if (CollectionUtils.isEmpty(userModelList)) {
			return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<UserBehaviorDetail>(), 0).asResult());
		}
		int total = userBehaviorMapper.total(humanCondition);

		List<String> networkIds = new ArrayList<>();
		for (UserBehaviorModel userModel : userModelList) {
			networkIds.addAll(JSON.parseArray(userModel.getNetworkIds(), String.class));
		}
		humanCondition = new HumanCondition().filterIn(NetworkBehaviorModel::getId, networkIds.toArray(new String[0]));
		List<NetworkBehaviorModel> networkModelList = networkBehaviorMapper.list(humanCondition);
		Map<String, NetworkBehaviorModel> networkModelMap = networkModelList.stream().collect(Collectors.toMap(NetworkBehaviorModel::getId, n -> n));
		List<UserBehaviorDetail> details = userModelList.stream().map(userModel -> {
			UserBehaviorDetail detail = new UserBehaviorDetail();
			BeanUtils.copyProperties(userModel, detail);
			List<NetworkBehaviorModel> networkList = new ArrayList<>();
			for (String networkId : JSON.parseArray(detail.getNetworkIds(), String.class)) {
				NetworkBehaviorModel networkModel = networkModelMap.get(networkId);
				if (networkModel!= null) {
					networkList.add(networkModelMap.get(networkId));
				}
			}
			detail.setNetworkBehaviorModelList(networkList);
			detail.setNetworkBehaviorIdList(networkList.stream().map(NetworkBehaviorModel::getId).collect(Collectors.toList()));
			return detail;
		}).collect(Collectors.toList());
		return new ListObjServiceResult(new ListObjResult(details, total).asResult());
	}

	/**
	 * getUserType
	 *
	 * @return
	 */
	public List<CodeNameRes> getUserType() {
		return Arrays.stream(UserBehaviorModel.UserType.values())
			  .map(userType -> new CodeNameRes(userType.getCode(), userType.getDesc()))
			  .collect(Collectors.toList());
	}

	/**
	 * 获取用户行为详情
	 *
	 * @param id
	 * @return
	 */
	public UserBehaviorDetail getDetail(String id) {
		UserBehaviorModel userBehaviorModel = userBehaviorMapper.get(id);
		String networkIds = userBehaviorModel.getNetworkIds();
		List<String> networkIdList = JSON.parseArray(networkIds, String.class);
		HumanCondition humanCondition = new HumanCondition().filterIn(NetworkBehaviorModel::getId, networkIdList.toArray(new String[0]));
		List<NetworkBehaviorModel> networkBehaviorModelList = networkBehaviorMapper.list(humanCondition);
		UserBehaviorDetail detail = new UserBehaviorDetail();
		BeanUtils.copyProperties(userBehaviorModel, detail);
		detail.setNetworkBehaviorModelList(networkBehaviorModelList);
		detail.setNetworkBehaviorIdList(networkBehaviorModelList.stream().map(NetworkBehaviorModel::getId).collect(Collectors.toList()));
		return detail;
	}
}
