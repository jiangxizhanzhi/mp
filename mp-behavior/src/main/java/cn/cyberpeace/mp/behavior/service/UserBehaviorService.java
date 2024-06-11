package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.UserBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.AddUserBehaviorReq;
import cn.cyberpeace.mp.behavior.req.UpdateUserBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CodeNameRes;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserBehaviorService {

	@Autowired
	private UserBehaviorMapper userBehaviorMapper;

	public String add(AddUserBehaviorReq addReq) {
		UserBehaviorModel model = new UserBehaviorModel();
		BeanUtils.copyProperties(addReq, model);
		model.setStartTime(TimeUtils.stringToLocalDateTime(addReq.getStartTime()));
		model.setEndTime(TimeUtils.stringToLocalDateTime(addReq.getEndTime()));
		model.setNetworkIds(JSON.toJSONString(addReq.getNetworkIdList()));
		userBehaviorMapper.create(model);
		return model.getId();
	}

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

	public void delete(List<String> ids) {
		userBehaviorMapper.bulkDelete(ids.toArray(new String[0]));
	}

	public ListObjServiceResult<UserBehaviorModel> page(PageRequest pageReq) {
		pageReq.ensure();
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());

		if (StringUtils.isNotEmpty(pageReq.getSearch())) {
			humanCondition.filterIContains(UserBehaviorModel::getName, pageReq.getSearch());
		}

		List<UserBehaviorModel> modelList = userBehaviorMapper.list(humanCondition);
		int total = userBehaviorMapper.total(humanCondition);
		return new ListObjServiceResult(new ListObjResult(modelList, total).asResult());
	}

	public List<CodeNameRes> getUserType() {
		return Arrays.stream(UserBehaviorModel.userType.values())
			  .map(userType -> new CodeNameRes(userType.getCode(), userType.getDesc()))
			  .collect(Collectors.toList());
	}
}
