package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.request.ReportCollectReq;
import cn.cyberpeace.mp.devopscluster.request.ReportCollectSoapReq;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.RealEquipmentConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ReportDataController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(value = "场景实例", tags = "CSCENE-场景实例")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/report")
@AuthTarget(TargetEnum.NO_AUTH)
public class ReportDataController {

	@Autowired
	private RealEquipmentConverter realEquipmentConverter;

	/**
	 * 上报通用协议采集数据
	 *
	 * @param request
	 * @param collectReq
	 * @return
	 */
	@PostMapping("/data")
	@ApiOperation(value = "上报通用协议采集数据")
	public ServiceResult<Boolean> report(HttpServletRequest request, @RequestBody ReportCollectReq collectReq) {
		realEquipmentConverter.report(request, collectReq.getContent());
		return new ServiceResult<>(true);
	}

	/**
	 * 上报通用协议采集数据(soap)
	 *
	 * @param collectReq
	 * @return
	 */
	@PostMapping("/data/soap")
	@ApiOperation(value = "上报通用协议采集数据(soap)")
	public ServiceResult<Boolean> report(@RequestBody ReportCollectSoapReq collectReq) {
		realEquipmentConverter.report(collectReq.getIp(), collectReq.getContent(), RealEquipmentConverter.WEBSERVICE);
		return new ServiceResult<>(true);
	}
}
