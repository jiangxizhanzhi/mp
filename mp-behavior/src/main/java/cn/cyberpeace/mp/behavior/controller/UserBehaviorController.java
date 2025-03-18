package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.bo.NodeUserBehavior;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.req.AddUserBehaviorReq;
import cn.cyberpeace.mp.behavior.req.DeleteReq;
import cn.cyberpeace.mp.behavior.req.UpdateUserBehaviorReq;
import cn.cyberpeace.mp.behavior.res.UserBehaviorDetailRes;
import cn.cyberpeace.mp.behavior.res.UserBehaviorPageRes;
import cn.cyberpeace.mp.behavior.service.UserBehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户行为管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user_behavior")
public class UserBehaviorController {

	@Autowired
	private UserBehaviorService userBehaviorService;

	@PostMapping("/")
	@ApiOperation("新增用户行为")
	public ServiceResult<String> addBehavior(@RequestBody @Validated AddUserBehaviorReq addUserBehaviorReq) {
		return ResultCommon.success(userBehaviorService.add(addUserBehaviorReq));
	}

	@PatchMapping("/")
	@ApiOperation("修改用户行为")
	public ServiceResult<Boolean> updateBehavior(@RequestBody @Validated UpdateUserBehaviorReq updateUserBehaviorReq) {
		userBehaviorService.update(updateUserBehaviorReq);
		return ResultCommon.success(true);
	}

	@DeleteMapping("/")
	@ApiOperation("删除用户行为")
	public ServiceResult<Boolean> deleteBehavior(@RequestBody DeleteReq deleteReq) {
		userBehaviorService.delete(deleteReq.getIds());
		return ResultCommon.success(true);
	}

	@GetMapping("/")
	@ApiOperation("获取用户行为详情")
	public ServiceResult<UserBehaviorDetailRes> getBehavior(@ApiParam(name = "id", value = "用户行为id", required = true)
														 @RequestParam("id") String id) {
		return ResultCommon.success(userBehaviorService.get(id));
	}

	@GetMapping("/page")
	@ApiOperation("分页查询用户行为")
	public ListObjServiceResult<UserBehaviorPageRes> pageBehavior(PageRequest pageReq) {
		return userBehaviorService.page(pageReq).asResult();
	}

	@GetMapping("/scene_page")
	@ApiOperation("场景中分页查询用户行为")
	public ListObjServiceResult<NodeUserBehavior> scenePageBehavior(PageRequest pageReq) {
		return userBehaviorService.scenePage(pageReq).asResult();
	}
}
