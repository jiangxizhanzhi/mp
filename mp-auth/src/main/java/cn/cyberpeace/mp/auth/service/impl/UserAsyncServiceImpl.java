package cn.cyberpeace.mp.auth.service.impl;

import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.redis.AddUserMessage;
import cn.cyberpeace.mp.auth.bo.redis.DeleteUserMessage;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.TenantUserService;
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

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private TenantUserService tenantUserService;

	private final String rootOrgId = "a13262d600fe40c799d63dce5c1485af";

	@Override
	public void addUser(String msg) {
		AddUserMessage message = JsonUtils.parse(msg, AddUserMessage.class);
		String orgName = message.getDepartment();
		OrgModel orgModel = orgService.getOrgModel(message.getDepartmentId());
		try {
			if (null == orgModel) {
				orgModel = new OrgModel();
				orgModel.setId(message.getDepartmentId());
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
		UserBo userBo = userService.getUserDto(message.getId());
		if (null == userBo) {
			// 用户不存在，创建
			this.addUser(msg);
			return;
		}
		if (!userBo.getOrgs().isEmpty()) {
			OrgBO oldOrgBO = userBo.getOrgs().get(0);
			// 用户组织发生变换
			if (!oldOrgBO.getId().equals(message.getDepartmentId())) {
				OrgModel orgModel = orgService.getOrgModel(message.getDepartmentId());
				if (null == orgModel) {
					// 组织不存在则创建
					orgModel = new OrgModel();
					orgModel.setId(message.getDepartmentId());
					orgModel.setName(message.getDepartment());
					orgModel.setParent(rootOrgId);
					orgService.create(orgModel, null);
				}
				// 修改用户组织
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {

					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						tenantUserService.deleteByUserId(userBo.getId());
						TenantUserModel tenantUserModel = TenantUserModel.builder()
								.user(userBo.getId()).org(message.getDepartmentId()).build();
						tenantUserService.create(tenantUserModel);
					}
				});
			} else if (!oldOrgBO.getName().equals(message.getDepartment())) {
				// 组织存在但名称发生变换
				OrgModel model = new OrgModel();
				model.setId(message.getDepartmentId());
				model.setName(message.getDepartment());
				orgService.update(model);
			}
		}
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
