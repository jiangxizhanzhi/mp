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

import cn.cyberpeace.mp.auth.common.FileCheckCommon;
import cn.cyberpeace.mp.auth.request.common.FileCheckRequest;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.behavior.bo.Param;
import cn.cyberpeace.mp.behavior.model.NetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.BehaviorTypeMapper;
import cn.cyberpeace.mp.behavior.repository.NetworkBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.AddNetworkBehaviorReq;
import cn.cyberpeace.mp.behavior.req.NetworkPageReq;
import cn.cyberpeace.mp.behavior.req.UpdateNetworkBehaviorReq;
import cn.cyberpeace.mp.behavior.res.*;
import cn.cyberpeace.mp.behavior.util.FileUtil;
import cn.cyberpeace.mp.behavior.util.ZipUtil;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import cn.cyberpeace.mp.common.util.FileClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
	private BaseAppConfig baseAppConfig;

	@Autowired
	private NetworkBehaviorMapper networkBehaviorMapper;

	@Autowired
	private BehaviorTypeMapper behaviorTypeMapper;

	/**
	 * add
	 *
	 * @param addReq
	 * @return
	 */
	public String add(AddNetworkBehaviorReq addReq) {
		NetworkBehaviorModel model = new NetworkBehaviorModel();
		BeanUtils.copyProperties(addReq, model);
		model.setParams(JSONObject.toJSONString(addReq.getParamList()));
		if (addReq.getType() == null) {
			model.setType(0);
		}
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
		model.setParams(JSONObject.toJSONString(updateReq.getParamList()));
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
	public NetworkBehaviorRes get(String id) {
		NetworkBehaviorRes networkBehaviorRes = new NetworkBehaviorRes();
		NetworkBehaviorModel networkBehaviorModel = networkBehaviorMapper.get(id);
		if (networkBehaviorModel == null) {
			log.error("cannot find networkBehavior ,id = [{}]", id);
			throw new MpRuntimeException("找不到对应的网络行为");
		}
		BeanUtils.copyProperties(networkBehaviorModel, networkBehaviorRes);
		networkBehaviorRes.setParamList(JSONObject.parseArray(networkBehaviorModel.getParams(), Param.class));
		return networkBehaviorRes;
	}

	/**
	 * page
	 *
	 * @param pageReq
	 * @return
	 */
	public ListObjServiceResult<NetworkBehaviorPageRes> page(NetworkPageReq pageReq) {
		pageReq.ensure();
		HumanCondition humanCondition = new HumanCondition()
				.pagination(pageReq.getPage(), pageReq.getPageSize());

		if (StringUtils.isNotEmpty(pageReq.getSearch())) {
			humanCondition.filterIContains(NetworkBehaviorModel::getName, pageReq.getSearch());
		}
		humanCondition.orderBy(NetworkBehaviorModel::getUpdateTime, true);

		List<NetworkBehaviorModel> modelList = networkBehaviorMapper.list(humanCondition);
		int total = networkBehaviorMapper.total(humanCondition);
		List<NetworkBehaviorPageRes> networkBehaviorPageResList = modelList.stream().map(this::NetworkBehaviorModel2Page).collect(Collectors.toList());
		return new ListObjServiceResult(new ListObjResult(networkBehaviorPageResList, total).asResult());
	}

	public BehaviorFileRes uploadBehaviorFile(MultipartFile file) {
		FileCheckRequest fileCheckRequest = new FileCheckRequest();
		fileCheckRequest.setSuffixList(Collections.singletonList("zip"));
		ServiceResult<Boolean> uploadResult = FileCheckCommon.checkFile(file,fileCheckRequest);
		if (!Boolean.TRUE.equals(uploadResult.getSource())) {
			throw new MpRuntimeException("仅支持上传zip文件");
		}
		FileResponseData fileResponseData =  FileClient.uploadSample(file);
		BehaviorFileRes behaviorFileRes = new BehaviorFileRes();
		behaviorFileRes.setFileName(fileResponseData.getFileName());
		behaviorFileRes.setFilePath(fileResponseData.getInnerHttpUrl());
		String fileContent = null;
		String unZipPath = null;
		try {
			unZipPath = ZipUtil.unZipToTmpPath(baseAppConfig.getFileServerDir() + File.separator + "default" + File.separator + fileResponseData.getFilePath());
			String behaviorJsonName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")) + ".json";
			fileContent = FileUtil.getContent(new File(unZipPath + File.separator + behaviorJsonName));
		} catch (ZipException e) {
			log.error("unzip or read file fail , e= ", e);
			throw new MpRuntimeException("读取压缩包内容失败");
		} finally {
			cn.cyberpeace.mp.base.util.mptool.FileUtil.del(unZipPath);
		}
		JSONArray commands = JSONObject.parseObject(fileContent).getJSONArray("Commands");
		if (commands == null) {
			log.error("commands is null");
			throw new MpRuntimeException("读取行为步骤失败，请确认是否含有行为步骤");
		}
		List<Param> params = new ArrayList<>();
		List<String> paramList = new ArrayList<>();
		for (Object command: commands) {
			JSONObject commandJson = (JSONObject) command;
			if ("store".equals(commandJson.getString("Command"))) {
				String value = commandJson.getString("Value");
				if (StringUtils.isNotEmpty(value) && !paramList.contains(value)) {
					Param param = new Param();
					param.setParam(value);
					param.setValue(commandJson.getString("Target"));
					param.setDesc("");
					params.add(param);
					paramList.add(value);
				}
			}
		}
		behaviorFileRes.setParamList(params);
		behaviorFileRes.setStep(commands.size());
		return behaviorFileRes;
	}

	public List<CodeNameRes> getTypeList() {
		return behaviorTypeMapper.selectAll();
	}

	public List<BehaviorListRes> getBehaviorList() {
		return networkBehaviorMapper.getBehaviorList()
									.stream()
									.map(bo -> new BehaviorListRes(bo.getId(), bo.getName(), JSONObject.parseArray(bo.getParams(), Param.class)))
									.collect(Collectors.toList());
	}

	private NetworkBehaviorPageRes NetworkBehaviorModel2Page(NetworkBehaviorModel networkBehaviorModel) {
		NetworkBehaviorPageRes networkBehaviorPageRes = new NetworkBehaviorPageRes();
		BeanUtils.copyProperties(networkBehaviorModel, networkBehaviorPageRes);
		networkBehaviorPageRes.setParamList(JSONObject.parseArray(networkBehaviorModel.getParams(), Param.class));
		return networkBehaviorPageRes;
	}
}
