package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.AzureVmSize;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
@ModelClass(AzureVmSize.class)
public interface AzureVimSizeMapper extends BaseModelMapper<AzureVmSize> {

    Boolean batchInsert(List<Map<String, String>> azureVmSizes);

    void truncate();

}
