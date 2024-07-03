package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.base.util.mptool.ZipUtil;
import cn.cyberpeace.mp.behavior.bo.UserBehaviorConfigBo;
import cn.cyberpeace.mp.behavior.model.CsceneNetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.CsceneUserBehaviorModel;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.CsceneNetworkBehaviorMapper;
import cn.cyberpeace.mp.behavior.repository.CsceneUserBehaviorMapper;
import cn.cyberpeace.mp.behavior.repository.UserBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.ElementBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CsceneNetworkBehaviorRes;
import cn.cyberpeace.mp.behavior.res.CsceneNodeBehaviorRes;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName CsceneUserBehaviorService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class CsceneUserBehaviorService {

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private CsceneUserBehaviorMapper csceneUserBehaviorMapper;

	@Autowired
	private UserBehaviorMapper userBehaviorMapper;

	@Autowired
	private CsceneNetworkBehaviorService csceneNetworkBehaviorService;

	@Autowired
	private CsceneNetworkBehaviorMapper csceneNetworkBehaviorMapper;

	/**
	 * add
	 *
	 * @param elementBehaviorReq
	 * @return
	 */
	public Boolean add(ElementBehaviorReq elementBehaviorReq) {
		String nodeId = elementBehaviorReq.getNodeId();
		List<String> userBehaviorIds = elementBehaviorReq.getUserBos().stream()
														 .map(UserBehaviorConfigBo::getUserBehaviorId).collect(Collectors.toList());
		HumanCondition humanCondition = new HumanCondition().filterIn(UserBehaviorModel::getId, userBehaviorIds.toArray());
		Map<String, UserBehaviorModel> behaviorModelMap = userBehaviorMapper.list(humanCondition)
																			.stream().collect(Collectors.toMap(UserBehaviorModel::getId, m -> m));
		List<CsceneUserBehaviorModel> modelList = new ArrayList<>();

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

				csceneNetworkBehaviorMapper.deleteTopoByNodeId(nodeId);
				csceneUserBehaviorMapper.deleteTopoByNodeId(nodeId);
				for (UserBehaviorConfigBo userBo : elementBehaviorReq.getUserBos()) {
					List<String> networkIds = csceneNetworkBehaviorService.add(userBo.getNetworkBos(), nodeId);
					CsceneUserBehaviorModel model = new CsceneUserBehaviorModel();
					UserBehaviorModel originalModel = behaviorModelMap.get(userBo.getUserBehaviorId());
					if (originalModel == null) {
						throw new MpRuntimeException("找不到用户行为数据");
					}
					BeanUtils.copyProperties(originalModel, model);
					model.setNodeId(nodeId);
					model.setSceneId("");
					model.setUserBehaviorId(userBo.getUserBehaviorId());
					model.setNetworkIds(JSON.toJSONString(networkIds));
					model.setCreateTime(null);
					model.setUpdateTime(null);
					model.setId(null);
					modelList.add(model);
				}
				csceneUserBehaviorMapper.bulkCreate(modelList);
			}
		});
		return true;
	}

	/**
	 * create
	 *
	 * @param nodeIds
	 * @param sceneId
	 */
	public void create(List<String> nodeIds, String sceneId) {
		if (CollectionUtils.isEmpty(nodeIds)) {
			return;
		}
		List<CsceneUserBehaviorModel> userModels = csceneUserBehaviorMapper.getTopoAllByNodeIds(nodeIds);
		if (CollectionUtils.isEmpty(userModels)) {
			return;
		}
		List<String> networkIds = new ArrayList<>();
		for (CsceneUserBehaviorModel userModel : userModels) {
			networkIds.addAll(JSON.parseArray(userModel.getNetworkIds(), String.class));
		}
		userModels.forEach(userModel -> {
			userModel.setSceneId(sceneId);
			userModel.setCreateTime(null);
			userModel.setUpdateTime(null);
			userModel.setId(null);
		});


		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

				csceneNetworkBehaviorService.create(networkIds, sceneId);
				csceneUserBehaviorMapper.bulkCreate(userModels);

			}
		});
	}


	public void delete(String sceneId) {

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

				csceneUserBehaviorMapper.deleteBySceneId(sceneId);
				csceneNetworkBehaviorMapper.deleteBySceneId(sceneId);

			}
		});
	}

	/**
	 * get
	 *
	 * @param nodeId
	 * @return
	 */
	public List<CsceneNodeBehaviorRes> get(String nodeId) {
		List<CsceneUserBehaviorModel> userModelList = csceneUserBehaviorMapper.getTopoAllByNodeId(nodeId);
		Map<String, CsceneNetworkBehaviorModel> networkModelMap = csceneNetworkBehaviorService.getMapByNodeId(nodeId);
		List<CsceneNodeBehaviorRes> nodeBehaviorResList = new ArrayList<>();
		for (CsceneUserBehaviorModel userModel : userModelList) {
			CsceneNodeBehaviorRes nodeBehaviorRes = new CsceneNodeBehaviorRes();
			BeanUtils.copyProperties(userModel, nodeBehaviorRes);
			List<String> networkIds = JSON.parseArray(userModel.getNetworkIds(), String.class);
			List<CsceneNetworkBehaviorRes> networkBehaviorResList = new ArrayList<>();
			for (String networkId : networkIds) {
				CsceneNetworkBehaviorModel networkModel = networkModelMap.get(networkId);
				if (networkModel == null) {
					throw new MpRuntimeException("找不到网络行为模型");
				}
				CsceneNetworkBehaviorRes networkBehaviorRes = new CsceneNetworkBehaviorRes();
				BeanUtils.copyProperties(networkModel, networkBehaviorRes);
				networkBehaviorResList.add(networkBehaviorRes);
			}
			nodeBehaviorRes.setNetworkList(networkBehaviorResList);
			nodeBehaviorResList.add(nodeBehaviorRes);
		}
		return nodeBehaviorResList;
	}

	/**
	 * getElementResource
	 *
	 * @param response
	 * @param nodeId
	 * @param sceneId
	 */
	public void getElementResource(HttpServletResponse response, String nodeId, String sceneId) {
		HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneUserBehaviorModel::getNodeId, nodeId)
				.filterEqual(CsceneUserBehaviorModel::getSceneId, sceneId);
		List<CsceneUserBehaviorModel> userModels = csceneUserBehaviorMapper.list(humanCondition);
		humanCondition = new HumanCondition().filterEqual(CsceneNetworkBehaviorModel::getNodeId, nodeId)
				.filterEqual(CsceneNetworkBehaviorModel::getSceneId, sceneId);
		List<CsceneNetworkBehaviorModel> networkModels = csceneNetworkBehaviorMapper.list(humanCondition);
		Map<String, CsceneNetworkBehaviorModel> networkModelMap = networkModels.stream().collect(Collectors.toMap(CsceneNetworkBehaviorModel::getId, m -> m));
		String originalPath = FileUtil.getTmpDirPath() + File.separator + sceneId + "_" + nodeId;
		Map<Integer, String> typeMap = Arrays.stream(UserBehaviorModel.UserType.values())
											 .collect(Collectors.toMap(UserBehaviorModel.UserType::getCode, type -> type.getDesc().substring(0, 2)));
		for (CsceneUserBehaviorModel userModel : userModels) {
			String folderPath = originalPath + File.separator + userModel.getName();
			//config.json内容
			JSONObject config = new JSONObject();
			config.put("scene_id", sceneId);
			config.put("node_id", nodeId);
			config.put("user_model_name", userModel.getName());
			config.put("user_model_id", userModel.getId());
			config.put("start_time", userModel.getStartTime());
			config.put("end_time", userModel.getEndTime());
			config.put("type", typeMap.get(userModel.getType()));
			List<String> networkNameList = new ArrayList<>();
			List<String> networkIds = JSON.parseArray(userModel.getNetworkIds(), String.class);
			for (int i = 0 ; i < networkIds.size() ; i++){
				//网络行为文件导出部分
				String networkId = networkIds.get(i);
				CsceneNetworkBehaviorModel networkModel = networkModelMap.get(networkId);
				String fileName = folderPath + File.separator + networkModel.getName() + ".side";
				File file = new File(folderPath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String fileString = getRemoteFileContent(networkModel.getFileUrl());
				JSONObject content = JSONObject.parseObject(fileString);
				if (StringUtils.isNotEmpty(networkModel.getUrl())) {
					//替换side文件中的url、urls
					content.put("url", networkModel.getUrl());
					List<String> urls = Arrays.asList(networkModel.getUrl());
					content.put("urls", urls);
				}
				try (FileWriter writer = new FileWriter(fileName)){
					writer.write(content.toString());
				}catch (IOException e) {
					log.error("export file fail, networkBehaviorId = {}, e = {}", networkId, e);
					throw new MpRuntimeException("导出文件失败");
				}
				networkNameList.add(networkModel.getName() + ".side");
			}
			config.put("actions", networkNameList);
			try (FileWriter writer = new FileWriter(folderPath + File.separator + "config.json")){
				writer.write(config.toString());
			}catch (IOException e) {
				log.error("export file fail, userBehaviorId = {}, e = {}", userModel.getUserBehaviorId(), e);
				throw new MpRuntimeException("导出文件失败");
			}
		}
		String resultPath = ZipUtil.toZip(originalPath);
		FileUtil.setResponse(new File(resultPath), true, response);
		FileUtil.del(originalPath);
	}

	/**
	 * deleteByNodeId
	 *
	 * @param nodeId
	 */
	private void deleteByNodeIdAndTopo(String nodeId) {
		HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneUserBehaviorModel::getNodeId, nodeId)
				.filterEqual(CsceneUserBehaviorModel::getSceneId, null);
		csceneUserBehaviorMapper.filterDelete(humanCondition);
	}

	/**
	 * getRemoteFileContent
	 *
	 * @param fileUrl
	 * @return
	 */
	private String getRemoteFileContent(String fileUrl) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(fileUrl);
		try {
			// 发送请求并获取响应
			HttpResponse response = httpClient.execute(request);
			// 检查响应的状态码
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				// 将响应实体转换为字符串
				return EntityUtils.toString(entity, "UTF-8");
			} else {
				log.error("get file content fail, file_url = {}", fileUrl);
				throw new MpRuntimeException("读取文件内容错误");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
