package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ImportService;
import cn.cyberpeace.mp.devops.cluster.model.ImportDiskSpaceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.BasePageRequest;
import cn.cyberpeace.mp.devops.cluster.response.ImportDiskSpaceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 靶标导入管理
 * @author qiaokang
 */
@Service
public class ImportConverter implements BaseConverter{

    private ImportService importService;

    @Autowired
    public ImportConverter(ImportService importService) {
        this.importService = importService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getOffset,
                BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    /**
     * 获取磁盘剩余空间
     * @return
     */
    public ImportDiskSpaceResponse getDiskSpace() {
        ImportDiskSpaceCloudPlatformModel diskSpaceModel = importService.getDiskSpace();
        return ImportDiskSpaceResponse.builder()
                .vmSpace(diskSpaceModel.getData().getVmSpace())
                .dockerSpace(diskSpaceModel.getData().getDockerSpace())
                .build();
    }
}
