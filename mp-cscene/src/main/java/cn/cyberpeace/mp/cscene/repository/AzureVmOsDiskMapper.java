package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.AzureVmOsDisk;
import cn.cyberpeace.mp.cscene.service.param.AzureVmOsDiskQueryParams;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
@ModelClass(AzureVmOsDisk.class)
public interface AzureVmOsDiskMapper extends BaseModelMapper<AzureVmOsDisk> {


    void truncate();

    Boolean batchInsert(List<Map<String, String>> azureVmOsDisks);

    List<AzureVmOsDisk> vmOsDiskTypeList(AzureVmOsDiskQueryParams humanCondition);

    List<AzureVmOsDisk> list(AzureVmOsDiskQueryParams queryParams);
}
