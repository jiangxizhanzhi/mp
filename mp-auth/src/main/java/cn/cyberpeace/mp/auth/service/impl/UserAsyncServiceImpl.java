package cn.cyberpeace.mp.auth.service.impl;

import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.cyberpeace.mp.auth.bo.redis.AddUserMessage;
import cn.cyberpeace.mp.auth.bo.redis.DeleteUserMessage;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.UserAsyncService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAsyncServiceImpl implements UserAsyncService {

	@Autowired
	private UserService userService;

	@Autowired
	private OrgService orgService;

	@Override
	public void addUser(String msg) {
		final String rootOrgId = "a13262d600fe40c799d63dce5c1485af";
		AddUserMessage message = JsonUtils.parse(msg, AddUserMessage.class);
		String orgName = message.getDepartment();
		OrgModel orgModel = orgService.getModelByName(orgName);
		try {
			if (null == orgModel) {
				orgModel = new OrgModel();
				orgModel.setName(orgName);
				orgModel.setParent(rootOrgId);
				orgService.create(orgModel, null);
			}
		} catch (Exception e) {
			log.error("create org {} failed: {}", orgName, e.getMessage());
		}
		orgModel = orgService.getModelByName(orgName);
		userService.createRedisUser(message, null == orgModel ? rootOrgId : orgModel.getId());
	}

	@Override
	public void updateUser(String msg) {
		AddUserMessage message = JsonUtils.parse(msg, AddUserMessage.class);
		UserModel userModel = new UserModel();
		userModel.setId(message.getId());
		userModel.setUsername(message.getUsername());
		userModel.setName(message.getDisplayName());
		userModel.setEmail(message.getEmail());
		userModel.setMobile(message.getMobile());
		userService.update(userModel);
	}

	@Override
	public void deleteUser(String msg) {
		DeleteUserMessage message = JsonUtils.parse(msg, DeleteUserMessage.class);
		if (StringUtils.isBlank(message.getIds())) {
			return;
		}
		List<UserModel> userModelList = Lists.newArrayList();
		for (String id : message.parseIds()) {
			UserModel model = new UserModel();
			model.setId(id);
			model.setIsEnabled(UserModel.Status.DISABLED);
			userModelList.add(model);
		}
		userService.bulkUpdate(userModelList);
	}
}
