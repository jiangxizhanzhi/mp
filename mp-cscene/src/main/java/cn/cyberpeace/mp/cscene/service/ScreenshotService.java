package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.model.ScreenshotModel;
import cn.cyberpeace.mp.cscene.request.ScreenshotBatchDeleteRequest;
import cn.cyberpeace.mp.cscene.request.ScreenshotDeleteRequest;
import cn.cyberpeace.mp.cscene.request.ScreenshotUploadRequest;
import cn.cyberpeace.mp.cscene.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.cscene.response.ScreenshotUsedSpaceResponse;


public interface ScreenshotService extends BaseModelService<ScreenshotModel, ScreenshotQueryParams> {

    
    ServiceResult<ScreenshotModel> screenshotUpload(ScreenshotUploadRequest request);

    
    ServiceResult<ScreenshotUsedSpaceResponse> queryUsedSpace();

    
    ServiceResult<Integer> timeDelete(ScreenshotDeleteRequest request);

    
    ServiceResult<Integer> batchDelete(ScreenshotBatchDeleteRequest request);

}

