package cn.cyberpeace.mp.sceneplugincloud.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincloud.model.AzureUserModel;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(AzureUserModel.class)
public interface AzureUserModelMapper extends BaseModelMapper<AzureUserModel> {
    AzureUserModel getLocalAzurer(JSONObject jsonObject);
}
