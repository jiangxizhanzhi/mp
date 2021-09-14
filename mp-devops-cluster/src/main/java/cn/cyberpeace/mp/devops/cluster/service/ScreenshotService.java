package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.model.ScreenshotModel;
import cn.cyberpeace.mp.devops.cluster.request.ScreenshotDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ScreenshotUploadRequest;
import cn.cyberpeace.mp.devops.cluster.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.devops.cluster.response.ScreenshotUsedSpaceResponse;

/**
 * @Description 截屏文件管理service
 * @Author qiaokang
 * @Date 2021-09-13 11:05:18
 */
public interface ScreenshotService extends BaseModelService<ScreenshotModel, ScreenshotQueryParams> {

    /**
     * 上传截屏文件
     * @param request
     * @return
     */
    ServiceResult<ScreenshotModel> screenshotUpload(ScreenshotUploadRequest request);

    /**
     * 查询已用空间
     * @return
     */
    ServiceResult<ScreenshotUsedSpaceResponse> queryUsedSpace();

    /**
     * 按时间删除截屏文件
     * @param request
     * @return
     */
    ServiceResult<Integer> timeDelete(ScreenshotDeleteRequest request);

}

