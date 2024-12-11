package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.devopscluster.model.VersionLibraryModel;
import cn.cyberpeace.mp.devopscluster.repository.MpVersionLibraryMapper;
import cn.cyberpeace.mp.devopscluster.request.alarm.VersionLibraryParams;
import cn.cyberpeace.mp.devopscluster.service.VersionLibraryService;
import cn.cyberpeace.mp.devopscluster.service.fields.AlarmStatisticsFields;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liyahao
 * @description 针对表【mp_version_library(软件/固件版本库)】的数据库操作Service实现
 * @createDate 2024-12-10 17:08:50
 */
@Service
public class VersionLibraryServiceImpl extends BaseModelServiceImpl<VersionLibraryModel, MpVersionLibraryMapper, AlarmStatisticsFields,
        QueryParams> implements VersionLibraryService {

    @Autowired
    public VersionLibraryServiceImpl(MpVersionLibraryMapper modelMapper) {
        setModelMapper(modelMapper);
    }

    @Override
    public ListObjServiceResult<VersionLibraryModel> page(VersionLibraryParams queryParams) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.orderBy(VersionLibraryModel::getUpdateTime, true);
        humanCondition.filterEqual(VersionLibraryModel::getType, queryParams.getType() ==null || queryParams.getType() != 1 ? 0 : 1);
        if (StringUtils.isNotBlank(queryParams.getName())) {
            humanCondition.filterContains(VersionLibraryModel::getName, queryParams.getName());
        }
        humanCondition.pagination(queryParams.getPage(), queryParams.getPageSize());
        List<VersionLibraryModel> list = getModelMapper().list(humanCondition);
        int total = getModelMapper().total(humanCondition);

        return new ListObjServiceResult<>(new ListObjResult<>(list, total));
    }
}




