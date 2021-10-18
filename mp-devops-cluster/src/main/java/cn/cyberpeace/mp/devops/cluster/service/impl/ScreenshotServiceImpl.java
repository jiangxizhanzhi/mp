package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.enums.FdfsGroupEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.devops.cluster.model.ScreenshotModel;
import cn.cyberpeace.mp.devops.cluster.repository.ScreenshotMapper;
import cn.cyberpeace.mp.devops.cluster.request.ScreenshotBatchDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ScreenshotDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ScreenshotUploadRequest;
import cn.cyberpeace.mp.devops.cluster.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.devops.cluster.response.ScreenshotUsedSpaceResponse;
import cn.cyberpeace.mp.devops.cluster.service.ScreenshotService;
import cn.cyberpeace.mp.devops.cluster.service.fields.ScreenshotFields;
import cn.hutool.core.date.DateUtil;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private FastDFSClient fastDFSClient;

    @Autowired
    private UserService userService;

    @Autowired
    private AsyncScreenshotModelService asyncScreenshotModelService;

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, ScreenshotQueryParams queryParams) {
        if (Objects.nonNull(queryParams.getStartTime())) {
            humanCondition.filterGreaterThanOrEqual("createTime", queryParams.getStartTime() + " 00:00:00");
        }

        if (Objects.nonNull(queryParams.getEndTime())) {
            humanCondition.filterLessThanOrEqual("createTime", queryParams.getEndTime() + " 23:59:59");
        }
    }

    /**
     * 上传截屏文件
     * @param request
     * @return
     */
    public ServiceResult<ScreenshotModel> screenshotUpload(ScreenshotUploadRequest request) {
        // 上传截屏文件
        String filePath = fastDFSClient.upload(request.getFile(), null, FdfsGroupEnum.DEFAULT.toString());
        ScreenshotModel model = new ScreenshotModel();
        model.setSceneName(request.getSceneName());
        model.setFileName(request.getFile().getOriginalFilename());
        model.setFileSize(request.getFile().getSize());

        // 获取当前用户信息
        UserModel loginUser = UserUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return ServiceResult.fail("Get login user info error");
        }
        // 用户id
        model.setUserId(loginUser.getId());
        UserDto userDto = userService.getUserDto(loginUser.getId());
        // 用户名
        model.setUserName(userDto.getUsername());
        if (CollectionUtils.isNotEmpty(userDto.getOrgs())) {
            // 封装组织名称，多个以逗号分隔
            List<String> nameList = userDto.getOrgs().stream().map(OrgModel::getName).collect(Collectors.toList());
            String userOrgName = Joiner.on(BaseConstants.COMMA).join(nameList);
            model.setUserOrgName(userOrgName);
        }

        // 新增截屏文件
        this.create(model);

        // 返回前端地址加上 /fdfs 前缀
        model.setFile("/fdfs/" + filePath);
        return ServiceResult.success(model);
    }

    /**
     * 查询已用空间
     * @return
     */
    @Override
    public ServiceResult<ScreenshotUsedSpaceResponse> queryUsedSpace() {
        // 查询已占用空间
        Long usedSpace = this.getModelMapper().queryUsedSpace();
        ScreenshotUsedSpaceResponse response = new ScreenshotUsedSpaceResponse(usedSpace);
        return ServiceResult.success(response);
    }

    /**
     * 按时间删除截屏文件
     * @param request 1: 6个月之前，2：3个月之前
     * @return
     */
    public ServiceResult<Integer> timeDelete(ScreenshotDeleteRequest request) {
        Integer deleteCount = 0;
        Long deleteMonth;
        if (2 == request.getRecordTime()) {
            // 减3个月
            deleteMonth = 3L;
        } else {
            // 减6个月
            deleteMonth = 6L;
        }

        // 查询删除日期之前的数据
        String deleteTime = DateUtil.format(LocalDateTime.now().minusMonths(deleteMonth), "yyyy-MM-dd") + " 00:00:00";
        List<ScreenshotModel> screenshotModels = this.getModelMapper().queryByCreateTime(deleteTime);
        if (CollectionUtils.isNotEmpty(screenshotModels)) {
            // 异步删除文件服务器上文件
            asyncScreenshotModelService.batchDeleteScreenshot(screenshotModels);

            // 删除数据库中文件
            String[] ids = screenshotModels.stream().map(ScreenshotModel::getId).toArray(String[]::new);
            this.bulkDelete(ids);
            deleteCount = ids.length;
        }

        return ServiceResult.success(deleteCount);
    }

    /**
     * 批量删除截屏文件
     * @param request
     * @return
     */
    public ServiceResult<Integer> batchDelete(ScreenshotBatchDeleteRequest request) {
        List<ScreenshotModel> screenshotModels = this.getModelMapper().selectByIds(request.getIds());

        if (CollectionUtils.isNotEmpty(screenshotModels)) {
            // 异步删除文件服务器上文件
            asyncScreenshotModelService.batchDeleteScreenshot(screenshotModels);
            bulkDelete(request.getIds().toArray(new String[0]));
            return ServiceResult.success(screenshotModels.size());
        } else {
            log.error("Screenshot to delete not exist:{}", request.getIds());
            return ServiceResult.fail("Screenshot to delete not exist");
        }

    }

}