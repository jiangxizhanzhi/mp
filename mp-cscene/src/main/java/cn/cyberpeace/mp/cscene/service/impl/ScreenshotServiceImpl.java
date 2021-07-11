package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import cn.cyberpeace.mp.cscene.repository.ScreenshotMapper;
import cn.cyberpeace.mp.cscene.request.ScreenshotBatchDeleteRequest;
import cn.cyberpeace.mp.cscene.request.ScreenshotDeleteRequest;
import cn.cyberpeace.mp.cscene.request.ScreenshotUploadRequest;
import cn.cyberpeace.mp.cscene.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.cscene.response.ScreenshotUsedSpaceResponse;
import cn.cyberpeace.mp.cscene.service.AsyncScreenshotModelService;
import cn.cyberpeace.mp.cscene.service.ScreenshotService;
import cn.cyberpeace.mp.cscene.service.filed.ScreenshotFields;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScreenshotServiceImpl extends BaseModelServiceImpl<ScreenshotModel, ScreenshotMapper, ScreenshotFields,
        ScreenshotQueryParams> implements ScreenshotService {

    public ScreenshotServiceImpl(ScreenshotMapper stuMapper, ScreenshotFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private AsyncScreenshotModelService asyncScreenshotModelService;

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, ScreenshotQueryParams queryParams) {
        if (Objects.nonNull(queryParams.getStartTime())) {
            humanCondition.filterGreaterThanOrEqual("createTime", queryParams.getStartTime() + " 00:00:00");
        }

        if (Objects.nonNull(queryParams.getEndTime())) {
            humanCondition.filterLessThanOrEqual("createTime", queryParams.getEndTime() + " 23:59:59");
        }
    }

    
    public ServiceResult<ScreenshotModel> screenshotUpload(ScreenshotUploadRequest request) {
        
        String suffix = FileUtil.getSuffix(request.getFile().getOriginalFilename());
        String filePath = null;
        try {
            filePath = FsCommon.upload(request.getFile().getInputStream(), suffix);
        } catch (IOException e) {
            return ResultCommon.fail("Upload screenshot error");
        }

        ScreenshotModel model = new ScreenshotModel();
        model.setFile(filePath);
        model.setSceneName(request.getSceneName());
        model.setFileName(request.getFile().getOriginalFilename());
        model.setFileSize(request.getFile().getSize());
        model.setNodeId(request.getNodeId());
        model.setScene(request.getScene());

        
        UserModel loginUser = UserUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return ResultCommon.fail("Get login user info error");
        }
        
        model.setUserId(loginUser.getId());
        UserBo userBo = userService.getUserDto(loginUser.getId());
        
        if (StringUtils.isEmpty(userBo.getName())) {
            
            model.setUserName(userBo.getUsername());
        } else {
            model.setUserName(userBo.getName());
        }

        if (CollectionUtils.isNotEmpty(userBo.getOrgs())) {
            
            List<String> nameList = userBo.getOrgs().stream().map(OrgModel::getName).collect(Collectors.toList());
            String userOrgName = Joiner.on(",").join(nameList);
            model.setUserOrgName(userOrgName);
        }

        
        this.create(model);
        
        model.setFile(FsCommon.genHttpUrl(filePath));

        return ResultCommon.success(model);
    }

    
    @Override
    public ServiceResult<ScreenshotUsedSpaceResponse> queryUsedSpace() {
        
        Long usedSpace = this.getModelMapper().queryUsedSpace();
        if (Objects.isNull(usedSpace)) {
            usedSpace = 0L;
        }
        ScreenshotUsedSpaceResponse response = new ScreenshotUsedSpaceResponse(usedSpace);
        return ResultCommon.success(response);
    }

    
    public ServiceResult<Integer> timeDelete(ScreenshotDeleteRequest request) {
        Integer deleteCount = 0;
        Long deleteMonth;
        if (2 == request.getRecordTime()) {
            
            deleteMonth = 3L;
        } else {
            
            deleteMonth = 6L;
        }

        
        String deleteTime = DateUtil.format(LocalDateTime.now().minusMonths(deleteMonth), "yyyy-MM-dd") + " 00:00:00";
        List<ScreenshotModel> screenshotModels = this.getModelMapper().queryByCreateTime(deleteTime);
        if (CollectionUtils.isNotEmpty(screenshotModels)) {
            
            asyncScreenshotModelService.batchDeleteScreenshot(screenshotModels);

            
            String[] ids = screenshotModels.stream().map(ScreenshotModel::getId).toArray(String[]::new);
            this.bulkDelete(ids);
            deleteCount = ids.length;
        }

        return ResultCommon.success(deleteCount);
    }

    
    public ServiceResult<Integer> batchDelete(ScreenshotBatchDeleteRequest request) {
        List<ScreenshotModel> screenshotModels = this.getModelMapper().selectByIds(request.getIds());

        if (CollectionUtils.isNotEmpty(screenshotModels)) {
            
            asyncScreenshotModelService.batchDeleteScreenshot(screenshotModels);
            bulkDelete(request.getIds().toArray(new String[0]));
            return ResultCommon.success(screenshotModels.size());
        } else {
            log.error("Screenshot to delete not exist:{}", request.getIds());
            return ResultCommon.fail("Screenshot to delete not exist");
        }

    }

    
    public ServiceResult<Integer> batchDeleteBySceneId(String csceneId) {
        HumanCondition condition = new HumanCondition().filterEqual(ScreenshotModel::getScene, csceneId);
        List<ScreenshotModel> screenshotModels = this.getModelMapper().list(condition);

        if (CollectionUtils.isNotEmpty(screenshotModels)) {
            List<String> idList = screenshotModels.stream().map(ScreenshotModel::getId).collect(Collectors.toList());
            
            asyncScreenshotModelService.batchDeleteScreenshot(screenshotModels);
            bulkDelete(idList.toArray(new String[0]));
            return ResultCommon.success(screenshotModels.size());
        } else {
            log.warn("Screenshot to delete by csceneId not exist:{}", csceneId);
            return ResultCommon.fail("Screenshot to delete by csceneId not exist");
        }

    }

}
