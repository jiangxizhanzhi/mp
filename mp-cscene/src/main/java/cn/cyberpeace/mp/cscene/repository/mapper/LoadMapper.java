package cn.cyberpeace.mp.cscene.repository.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSource;
import cn.cyberpeace.mp.cscene.request.load.LoadRequest;
import cn.cyberpeace.mp.cscene.request.load.LoadResultSearchRequest;
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

    Boolean updateLoadByPk(String pk, @Param("param") JSONObject requestData);

    Boolean deleteLoadByPk(String pk);

    // pk和pks为工具的主键
    List<MpDevopsLoadSceneLog> getLoadExecutionResult(@Param("param") LoadResultSearchRequest requestData);

    Integer getLoadExecutionResultNum(@Param("param") LoadResultSearchRequest requestData);

    Boolean deleteLoadExecutionResultByPk(String pk);

    Boolean updateLoadExecutionResultByPk(String pk, @Param("param") MpDevopsLoadSceneLog requestData);

    Boolean addLoadExecutionResult(@Param("param") MpDevopsLoadSceneLog requestData);

    Integer addResult(@Param("param") JSONObject requestData);

    Boolean updateLoadExecutionOutResult(@Param("param") List<LoadRequest> requestData);

    Boolean addLoad(@Param("param") JSONObject requestData);

    Boolean synchronousDataSource(@Param("param") MpDevopsLoadSource requestData);

    Boolean addSynchronousDataSource(@Param("param") MpDevopsLoadSource requestData);

    MpDevopsLoadSource getSynchronousDataSource();

    Boolean addLoads(@Param("param") List<MpDevopsLoadSceneLog> list);

    Boolean addLoadLogs(@Param("param") List<MpDevopsLoadSceneLog> list);

    Boolean deleteData(@Param("param") List<String> ids);

    List<MpDevopsLoad> getToolListByloadIds(@Param("param") List<String> ids);

    List<String> getLoadsBySceneId(String sceneId);

    List<JSONObject> getLoadResultByLoads(@Param("param") List<String> loadIds);

    List<MpDevopsLoadSceneLog> getLoadResultByLoadIds(@Param("param") List<String> loadIds);

    Integer getLoadListNum(@Param("param") JSONObject requestData);

    List<MpDevopsLoad> getLoadByIds(@Param("param") List<String> loadIds);

    Boolean batchUpdateData(@Param("param") List<MpDevopsLoadSceneLog> sameLoads);

    String getSceneIdByTaskId(String taskId);

    MpDevopsLoadSceneLog getLoadExecutionResultByLogPk(String pk);

    List<String> getLoadBySceneId(String sceneId);

    MpDevopsLoad getLoadByPk(String pk);

    MpDevopsLoad getLoadById(String id);
}
