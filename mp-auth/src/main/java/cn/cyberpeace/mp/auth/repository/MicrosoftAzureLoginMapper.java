package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(MicrosoftAzureUserModel.class)
public interface MicrosoftAzureLoginMapper extends BaseModelMapper<MicrosoftAzureUserModel> {

    List<MicrosoftAzureUserModel> getMicrosoftAzureUserList(@Param("param") JSONObject requestData);

    MicrosoftAzureUserModel getLocalAzurer(@Param("param") JSONObject requestData);
}
