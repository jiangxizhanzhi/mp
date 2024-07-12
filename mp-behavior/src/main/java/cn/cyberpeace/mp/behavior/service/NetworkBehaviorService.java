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

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.NetworkBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.AddNetworkBehaviorReq;
import cn.cyberpeace.mp.behavior.req.NetworkPageReq;
import cn.cyberpeace.mp.behavior.req.UpdateNetworkBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CodeNameRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName NetworkBehaviorService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class NetworkBehaviorService {

	@Autowired
	private NetworkBehaviorMapper networkBehaviorMapper;

	/**
	 * add
	 *
	 * @param addReq
	 * @return
	 */
	public String add(AddNetworkBehaviorReq addReq) {
		NetworkBehaviorModel model = new NetworkBehaviorModel();
		BeanUtils.copyProperties(addReq, model);
		networkBehaviorMapper.create(model);
		return model.getId();
	}

	/**
	 * update
	 *
	 * @param updateReq
	 */
	public void update(UpdateNetworkBehaviorReq updateReq) {
		NetworkBehaviorModel model = new NetworkBehaviorModel();
		BeanUtils.copyProperties(updateReq, model);
		networkBehaviorMapper.update(model);
	}

	/**
	 * delete
	 *
	 * @param ids
	 */
	public void delete(List<String> ids) {
		networkBehaviorMapper.bulkDelete(ids.toArray(new String[0]));
	}

	/**
	 * get
	 *
	 * @param id
	 * @return
	 */
	public NetworkBehaviorModel get(String id) {
		return networkBehaviorMapper.get(id);
	}

	/**
	 * page
	 *
	 * @param pageReq
	 * @return
	 */
	public ListObjServiceResult<NetworkBehaviorModel> page(NetworkPageReq pageReq) {
		pageReq.ensure();
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());

		if (pageReq.getType() != null) {
			humanCondition.filterEqual(NetworkBehaviorModel::getType, pageReq.getType());
		}
		if (StringUtils.isNotEmpty(pageReq.getSearch())) {
			humanCondition.filterIContains(NetworkBehaviorModel::getName, pageReq.getSearch());
		}

		List<NetworkBehaviorModel> modelList = networkBehaviorMapper.list(humanCondition);
		int total = networkBehaviorMapper.total(humanCondition);
		return new ListObjServiceResult(new ListObjResult(modelList, total).asResult());
	}

	/**
	 * getBehaviorType
	 *
	 * @return
	 */
	public List<CodeNameRes> getBehaviorType() {
		return Arrays.stream(NetworkBehaviorModel.BehaviorEnum.values())
					 .map(behaviorEnum -> new CodeNameRes(behaviorEnum.getCode(), behaviorEnum.getDesc()))
					 .collect(Collectors.toList());
	}
}
