package cn.cyberpeace.mp.cscene.repository.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(MpDevopsLoad.class)
public interface LoadMapper extends BaseModelMapper<MpDevopsLoad> {

    List<MpDevopsLoad> getLoadList(@Param("param") JSONObject requestData);

    Integer getLoadListotal(@Param("param") JSONObject requestData);

    Boolean updateLoadByPk(String pk, @Param("param") JSONObject requestData);

    Boolean deleteLoadByPk(String pk);

    List<MpDevopsLoadSceneLog> getLoadExecutionResultStatistics(@Param("param") JSONObject requestData);

    Boolean deleteLoadExecutionResultByPk(String pk);

    Boolean updateLoadExecutionResultByPk(String pk, @Param("param") JSONObject requestData);

    Boolean addLoadExecutionResult(@Param("param") JSONObject requestData);

    Boolean addLoad(@Param("param") JSONObject requestData);

    Boolean stageBindLoad(String addType, String sceneId, String stageId, String loadId,
                          String interfaceType, String deployment, String paramListString);

    Boolean synchronousDataSource(@Param("param") JSONObject requestData);

    Boolean addSynchronousDataSource(@Param("param") JSONObject requestData);

    JSONObject getSynchronousDataSource();

    Boolean addLoads(@Param("param") List<JSONObject> list);

    Boolean deleteData();
}
