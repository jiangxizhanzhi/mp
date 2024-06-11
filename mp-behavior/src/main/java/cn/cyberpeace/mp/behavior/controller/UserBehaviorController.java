package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import cn.cyberpeace.mp.behavior.req.AddUserBehaviorReq;
import cn.cyberpeace.mp.behavior.req.DeleteReq;
import cn.cyberpeace.mp.behavior.req.UpdateUserBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CodeNameRes;
import cn.cyberpeace.mp.behavior.service.UserBehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "用户行为管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/user")
public class UserBehaviorController {

	@Autowired
	private UserBehaviorService userBehaviorService;

	@PostMapping("/")
	@ApiOperation("新增用户行为")
	public ServiceResult<String> addBehavior(@RequestBody @Validated AddUserBehaviorReq addReq) {
		return ResultCommon.success(userBehaviorService.add(addReq));
	}

	@PatchMapping("/")
	@ApiOperation("修改用户行为")
	public ServiceResult<Boolean> updateBehavior(@RequestBody @Validated UpdateUserBehaviorReq updateReq) {
		userBehaviorService.update(updateReq);
		return ResultCommon.success(true);
	}

	@DeleteMapping("/")
	@ApiOperation("删除用户行为")
	public ServiceResult<Boolean> deleteBehavior(@RequestBody DeleteReq deleteReq) {
		userBehaviorService.delete(deleteReq.getIds());
		return ResultCommon.success(true);
	}

	@GetMapping("/page")
	@ApiOperation("分页查询用户行为")
	public ListObjServiceResult<UserBehaviorModel> pageBehavior(PageRequest pageReq) {
		return userBehaviorService.page(pageReq).asResult();
	}

	@GetMapping("/type")
	@ApiOperation("查看用户行为访问配置")
	public ServiceResult<List<CodeNameRes>> getUserType() {
		return ResultCommon.success(userBehaviorService.getUserType());
	}

}
