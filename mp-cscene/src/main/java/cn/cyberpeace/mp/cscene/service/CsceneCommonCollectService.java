 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneCommonCollectService.java.
 // File size 1673.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.model.CsceneCommonCollectModel;
import cn.cyberpeace.mp.cscene.repository.CsceneCommonCollectMapper;
import cn.cyberpeace.mp.cscene.response.CommonCollectRes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CsceneCommonCollectService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
public class CsceneCommonCollectService {

	@Autowired
	private CsceneCommonCollectMapper collectMapper;

	/**
	 * getCollect
	 *
	 * @param csceneId
	 * @param nodeId
	 * @param agreement
	 * @return
	 */
	public List<CommonCollectRes> getCollect(String csceneId, String nodeId, String agreement) {
		HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneCommonCollectModel::getCsceneId, csceneId);
		if (StringUtils.isNotEmpty(nodeId)) {
			humanCondition.filterEqual(CsceneCommonCollectModel::getNodeId, nodeId);
		}
		if (StringUtils.isNotEmpty(agreement)) {
			humanCondition.filterEqual(CsceneCommonCollectModel::getAgreement, agreement);
		}
		List<CsceneCommonCollectModel> modelList = collectMapper.list(humanCondition);
		return modelList.stream().map(mode -> {
			CommonCollectRes res = new CommonCollectRes();
			BeanUtils.copyProperties(mode, res);
			return res;
		}).collect(Collectors.toList());
	}
}
