package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.AccessProtocol;
import cn.cyberpeace.mp.devopscluster.service.PlatformService;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.model.PlatformModel;
import cn.cyberpeace.mp.devopscluster.repository.PlatformMapper;
import cn.cyberpeace.mp.devopscluster.service.fields.PlatformFields;
import cn.cyberpeace.mp.devopscluster.vo.PlatformVO;
import cn.cyberpeace.mp.devopscluster.request.PlatformPageRequest;
import cn.cyberpeace.mp.frontadmin.util.SQLUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author guquan
 * @Date：2025/9/9 11:13
 */
@Service
@Slf4j
public class PlatformServiceImpl extends BaseModelServiceImpl<PlatformModel, PlatformMapper, PlatformFields, QueryParams> implements PlatformService {

    @Autowired
    private PlatformMapper platformMapper;

    @Autowired
    public PlatformServiceImpl(PlatformMapper modelMapper, PlatformFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public PlatformVO platformModel2PlatformVO(PlatformModel platformModel) {
        if (Objects.isNull(platformModel)) {
            return null;
        }

        PlatformVO platformVO = new PlatformVO();
        BeanUtils.copyProperties(platformModel, platformVO);
        if (StringUtils.isNotEmpty(platformModel.getAccessProtocols())) {
            List<AccessProtocol> accessProtocolList = JSONObject.parseArray(platformModel.getAccessProtocols(), AccessProtocol.class);
            platformVO.setAccessProtocolList(accessProtocolList);
        }
        return platformVO;
    }

    @Override
    public PlatformModel platformVO2PlatformModel(PlatformVO platformVO) {
        if (Objects.isNull(platformVO)) {
            return null;
        }

        PlatformModel platformModel = new PlatformModel();
        BeanUtils.copyProperties(platformVO, platformModel);
        if (CollectionUtils.isNotEmpty(platformVO.getAccessProtocolList())) {
            String accessProtocols = JSONObject.toJSONString(platformVO.getAccessProtocolList());
            platformModel.setAccessProtocols(accessProtocols);
        }
        return platformModel;
    }

    @Override
    public ServiceResult page(PlatformPageRequest req) {
        String likeName = SQLUtils.escapeLike(req.getName());
        HumanCondition condition = new HumanCondition();
        if (StringUtils.isNotBlank(likeName)) {
            condition.filterContains(PlatformModel::getName, likeName);
        }
        int total = platformMapper.total(condition);
        condition.pagination(req.getPage(), req.getPerPage());
        List<PlatformModel> models = platformMapper.list(condition);
        List<PlatformVO> platformVOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(models)) {
            for (PlatformModel temp : models) {
                PlatformVO platformVO = platformModel2PlatformVO(temp);
                platformVOs.add(platformVO);
            }
        }
        ListObjServiceResult<PlatformModel> result = new ListObjServiceResult(new ListObjResult(platformVOs, total).asResult()).asResult();
        return result;
    }

    @Override
    public PlatformVO add(PlatformVO platformVO) {
        PlatformModel platformModel = platformVO2PlatformModel(platformVO);
        if (StringUtils.isBlank(platformModel.getCreateUser())) {
            UserModel currentUser = UserUtils.getLoginUser();
            if (Objects.nonNull(currentUser)) {
                platformModel.setCreateUser(currentUser.getUsername());
                platformModel.setCreateUsername(currentUser.getName());
            }
        }
        platformMapper.create(platformModel);
        return platformModel2PlatformVO(platformModel);
    }

    @Override
    public PlatformVO modify(PlatformVO platformVO) {
        PlatformModel platformModel = platformVO2PlatformModel(platformVO);
        if (StringUtils.isBlank(platformModel.getCreateUser())) {
            UserModel currentUser = UserUtils.getLoginUser();
            if (Objects.nonNull(currentUser)) {
                platformModel.setCreateUser(currentUser.getUsername());
                platformModel.setCreateUsername(currentUser.getName());
            }
        }
        platformMapper.update(platformModel);
        return platformModel2PlatformVO(platformModel);
    }

    @Override
    public Boolean deletePlatform(String id) {
        Boolean rtn = platformMapper.delete(id);
        return rtn;
    }

}
