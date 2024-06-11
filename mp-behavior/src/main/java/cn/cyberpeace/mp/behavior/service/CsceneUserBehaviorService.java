package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.behavior.bo.UserBehaviorConfigBo;
import cn.cyberpeace.mp.behavior.model.CsceneNetworkBehaviorModel;
import cn.cyberpeace.mp.behavior.model.CsceneUserBehaviorModel;
import cn.cyberpeace.mp.behavior.model.UserBehaviorModel;
import cn.cyberpeace.mp.behavior.repository.CsceneUserBehaviorMapper;
import cn.cyberpeace.mp.behavior.repository.UserBehaviorMapper;
import cn.cyberpeace.mp.behavior.req.ElementBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CsceneNetworkBehaviorRes;
import cn.cyberpeace.mp.behavior.res.CsceneNodeBehaviorRes;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

				csceneNetworkBehaviorService.deleteByNodeId(nodeId);
				deleteByNodeId(nodeId);
				for (UserBehaviorConfigBo userBo : elementBehaviorReq.getUserBos()) {
					List<String> networkIds = csceneNetworkBehaviorService.add(userBo.getNetworkBos(), nodeId);
					CsceneUserBehaviorModel model = new CsceneUserBehaviorModel();
					UserBehaviorModel originalModel = behaviorModelMap.get(userBo.getUserBehaviorId());
					if (originalModel == null) {
						throw new MpRuntimeException("找不到用户行为数据");
					}
					BeanUtils.copyProperties(originalModel, model);
					model.setNodeId(nodeId);
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

	public List<CsceneNodeBehaviorRes> get(String nodeId) {
		HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneUserBehaviorModel::getNodeId, nodeId);
		List<CsceneUserBehaviorModel> userModelList = csceneUserBehaviorMapper.list(humanCondition);
		List<CsceneNetworkBehaviorModel> networkModelList = csceneNetworkBehaviorService.get(nodeId);
		Map<String, CsceneNetworkBehaviorModel> networkModelMap = networkModelList.stream().collect(Collectors.toMap(CsceneNetworkBehaviorModel::getId, m -> m));
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

	private void deleteByNodeId(String nodeId) {
		HumanCondition humanCondition = new HumanCondition().filterEqual(CsceneUserBehaviorModel::getNodeId, nodeId);
		csceneUserBehaviorMapper.filterDelete(humanCondition);
	}

}
