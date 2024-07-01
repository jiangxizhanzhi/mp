package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.request.common.FileCheckRequest;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.req.AddNetworkBehaviorReq;
import cn.cyberpeace.mp.behavior.req.DeleteReq;
import cn.cyberpeace.mp.behavior.req.NetworkPageReq;
import cn.cyberpeace.mp.behavior.req.UpdateNetworkBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CodeNameRes;
import cn.cyberpeace.mp.behavior.service.NetworkBehaviorService;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.common.util.FileClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "网络行为管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/network")
public class NetworkBehaviorController {

	@Autowired
	private NetworkBehaviorService networkBehaviorService;

	/**
	 * 新增网络行为
	 *
	 * @param addNetworkBehaviorReq
	 * @return
	 */
	@PostMapping("/")
	@ApiOperation("新增网络行为")
	public ServiceResult<String> addBehavior(@RequestBody @Validated AddNetworkBehaviorReq addNetworkBehaviorReq) {
		return ResultCommon.success(networkBehaviorService.add(addNetworkBehaviorReq));
	}

	/**
	 * 修改网络行为
	 *
	 * @param updateNetworkBehaviorReq
	 * @return
	 */
	@PatchMapping("/")
	@ApiOperation("修改网络行为")
	public ServiceResult<Boolean> updateBehavior(@RequestBody @Validated UpdateNetworkBehaviorReq updateNetworkBehaviorReq) {
		networkBehaviorService.update(updateNetworkBehaviorReq);
		return ResultCommon.success(true);
	}

	/**
	 * 删除网络行为
	 *
	 * @param deleteReq
	 * @return
	 */
	@DeleteMapping("/")
	@ApiOperation("删除网络行为")
	public ServiceResult<Boolean> deleteBehavior(@RequestBody DeleteReq deleteReq) {
		networkBehaviorService.delete(deleteReq.getIds());
		return ResultCommon.success(true);
	}

	/**
	 * 获取网络行为详情
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/")
	@ApiOperation("获取网络行为详情")
	public ServiceResult<NetworkBehaviorModel> getBehavior(@ApiParam(name = "id", value = "网络行为id", required = true)
															   @RequestParam("id") String id) {
		return ResultCommon.success(networkBehaviorService.get(id));
	}

	/**
	 * 分页查询网络行为
	 *
	 * @param pageReq
	 * @return
	 */
	@GetMapping("/page")
	@ApiOperation("分页查询网络行为")
	public ListObjServiceResult<NetworkBehaviorModel> pageBehavior(NetworkPageReq pageReq) {
		return networkBehaviorService.page(pageReq).asResult();
	}

	/**
	 * 查询行为类型
	 *
	 * @return
	 */
	@GetMapping("/type")
	@ApiOperation("查询行为类型")
	public ServiceResult<List<CodeNameRes>> getBehaviorType() {
		return ResultCommon.success(networkBehaviorService.getBehaviorType());
	}

	/**
	 * 导入文件
	 *
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	@ApiOperation("导入文件")
	@ResponseBody
	@ApiImplicitParam(name="file", value="文件", dataTypeClass = MultipartFile.class)
	public ServiceResult<FileResponseData> uploadFile(MultipartFile file) {
		FileCheckRequest fileCheckRequest = new FileCheckRequest();
		fileCheckRequest.setSuffixList(AuthConstant.BEHAVIOR_FILE_TYPE);
		return ResultCommon.success(FileClient.uploadSample(file));
	}

}
