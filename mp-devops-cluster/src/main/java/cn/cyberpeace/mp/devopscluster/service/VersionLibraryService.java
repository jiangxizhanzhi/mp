package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.BaseObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devopscluster.model.VersionLibraryModel;
import cn.cyberpeace.mp.devopscluster.request.alarm.VersionLibraryParams;

/**
* @author liyahao
* @description 针对表【mp_version_library(软件/固件版本库)】的数据库操作Service
* @createDate 2024-12-10 17:08:50
*/
public interface VersionLibraryService extends BaseModelService<VersionLibraryModel, QueryParams> {

    ListObjServiceResult<VersionLibraryModel> page(VersionLibraryParams queryParams);
}
