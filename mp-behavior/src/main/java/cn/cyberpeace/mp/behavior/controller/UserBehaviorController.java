 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserBehaviorController.java.
 // File size 3305.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.repository.UserBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.AddUserBehaviorReq;
import cn.cyberpeace.mp.behavior.req.DeleteReq;
import cn.cyberpeace.mp.behavior.req.UpdateUserBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CodeNameRes;
import cn.cyberpeace.mp.behavior.res.UserBehaviorDetail;
import cn.cyberpeace.mp.behavior.service.UserBehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @ClassName UserBehaviorController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(tags = "用户行为管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user")
public class UserBehaviorController {

	@Autowired
	private UserBehaviorService userBehaviorService;

	@Autowired
	private UserBehaviorMapper userBehaviorMapper;

	/**
	 * 新增用户行为
	 *
	 * @param addReq
	 * @return
	 */
	@PostMapping("/")
	@ApiOperation("新增用户行为")
	public ServiceResult<String> addBehavior(@RequestBody @Validated AddUserBehaviorReq addReq) {
		return ResultCommon.success(userBehaviorService.add(addReq));
	}

	/**
	 * 修改用户行为
	 *
	 * @param updateReq
	 * @return
	 */
	@PatchMapping("/")
	@ApiOperation("修改用户行为")
	public ServiceResult<Boolean> updateBehavior(@RequestBody @Validated UpdateUserBehaviorReq updateReq) {
		userBehaviorService.update(updateReq);
		return ResultCommon.success(true);
	}

	/**
	 * 删除用户行为
	 *
	 * @param deleteReq
	 * @return
	 */
	@DeleteMapping("/")
	@ApiOperation("删除用户行为")
	public ServiceResult<Boolean> deleteBehavior(@RequestBody DeleteReq deleteReq) {
		userBehaviorService.delete(deleteReq.getIds());
		return ResultCommon.success(true);
	}

	/**
	 * 分页查询用户行为
	 *
	 * @param pageReq
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("分页查询用户行为")
	public ListObjServiceResult<UserBehaviorDetail> pageBehavior(PageRequest pageReq) {
		return userBehaviorService.page(pageReq).asResult();
	}

	/**
	 * 查看用户行为访问配置
	 *
	 * @return
	 */
	@GetMapping("/type")
	@ApiOperation("查看用户行为访问配置")
	public ServiceResult<List<CodeNameRes>> getUserType() {
		return ResultCommon.success(userBehaviorService.getUserType());
	}

	/**
	 * 查询用户行为详情
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/")
	@ApiOperation("查询用户行为详情")
	public ServiceResult<UserBehaviorDetail> behaviorDetail(@RequestParam String id) {
		return ResultCommon.success(userBehaviorService.getDetail(id));
	}

}
