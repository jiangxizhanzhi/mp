package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.constant.codeEnum.CommonConvertStatusEnum;
import cn.cyberpeace.mp.common.constant.codeEnum.CommonFunctionCodeEnum;
import cn.cyberpeace.mp.common.constant.codeEnum.CommonStatusCodeEnum;
import cn.cyberpeace.mp.common.service.impl.CommonCodeServiceImpl;
import cn.cyberpeace.mp.common.service.impl.CommonConfigServiceImpl;
import cn.cyberpeace.mp.common.service.impl.CommonFileServiceImpl;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneUploadLogFile;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSource;
import cn.cyberpeace.mp.cscene.repository.mapper.LoadMapper;
import cn.cyberpeace.mp.cscene.request.load.LoadRequest;
import cn.cyberpeace.mp.cscene.request.load.LoadResultExportRequest;
import cn.cyberpeace.mp.cscene.request.load.LoadResultSearchRequest;
import cn.cyberpeace.mp.cscene.request.load.OutLoadModifyTagsRequest;
import cn.cyberpeace.mp.cscene.request.ojj.IdsRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.websocket.LoadResultWebSocketServer;
import cn.cyberpeace.mp.cscene.websocket.WebSocketServer;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LoadServiceImpl {
    @Autowired
    private LoadMapper loadMapper;

    @Autowired
    private CommonCodeServiceImpl commonCodeService;

    @Autowired
    private OutLoadServiceImpl outLoadService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private CommonFileServiceImpl commonFileService;

    @Autowired
    private CommonConfigServiceImpl commonConfigService;

    // 载荷改造地址
    @Value("${project.setting.load-transform-url:http://ip:port}")
    private String LOAD_TRANSFORM_URL;

    /*
     * 载荷页面
     * */
    public JSONObject getLoadList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10000;
        requestData.put("pageSize", pageSize);
        requestData.put("offset", (pageNum - 1) * pageSize);
        List<MpDevopsLoad> list = loadMapper.getLoadList(requestData);

        Map<String, String> nextLevelMapsByCodes = commonCodeService.getNextLevelMapsByCodes(CommonStatusCodeEnum.CODE.getCode());
        //list每个成员的imageType赋值
        list.forEach(item -> {
            item.setRunTableName(nextLevelMapsByCodes.get(item.getRunTable()));
            if (StringUtils.hasText(item.getActionType())) {
                item.setActionTypeConvertName(item.getActionType(), nextLevelMapsByCodes);
            }
            item.setControlTypeName(nextLevelMapsByCodes.get(item.getControlType()));
        });
        Integer total = loadMapper.getLoadListNum(requestData);
        JSONObject result = new JSONObject();
        result.put("source", list);
        result.put("total", total);
        return result;
    }

    /*
     * 载荷详情
     * */
    public MpDevopsLoad getLoadByPk(String pk) {
        MpDevopsLoad load = loadMapper.getLoadByPk(pk);
        Map<String, String> nextLevelMapsByCodes = commonCodeService.getNextLevelMapsByCodes(CommonStatusCodeEnum.CODE.getCode());
        load.setRunTableName(nextLevelMapsByCodes.get(load.getRunTable()));
        if (StringUtils.hasText(load.getActionType())) {
            load.setActionTypeConvertName(load.getActionType(), nextLevelMapsByCodes);
        }
        load.setControlTypeName(nextLevelMapsByCodes.get(load.getControlType()));
        return load;
    }

    /*
     * 根据id查询载荷
     * */
    public MpDevopsLoad getLoadById(String id) {
        MpDevopsLoad load = loadMapper.getLoadById(id);
        Map<String, String> nextLevelMapsByCodes = commonCodeService.getNextLevelMapsByCodes(CommonStatusCodeEnum.CODE.getCode());
        load.setRunTableName(nextLevelMapsByCodes.get(load.getRunTable()));
        if (StringUtils.hasText(load.getActionType())) {
            load.setActionTypeConvertName(load.getActionType(), nextLevelMapsByCodes);
        }
        load.setControlTypeName(nextLevelMapsByCodes.get(load.getControlType()));
        return load;
    }

    /*
     * 载荷数据同步
     * */
    @Transactional
    public synchronized JSONObject synchronousData(Boolean isNeedMessage) {
        //推送的消息返回格式
        Map jsonObject = new LinkedHashMap<>();
        jsonObject.put("code", CommonFunctionCodeEnum.SYNCHRONOUS_DATA_ACTION_CODE.getCode());
        Map data = new LinkedHashMap<>();

        //同步设置
        MpDevopsLoadSource synchronousDataSource = getSynchronousDataSource(new JSONObject());
        if (synchronousDataSource == null) {
            if (isNeedMessage) {
                //给所有当前登录的session推送消息
                WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                    sendMessage(webSocketServer, session, jsonObject, data, 1, "获取载荷平台ip失败,会话关闭！", false);
                });
            }
            return null;
        }

        JSONObject request = new JSONObject();
        request.put("pagenum", 0);
        request.put("pagesize", 1000);
        //通过部署的载荷服务器获取载荷列表
        List<LinkedHashMap> initialToolList = outLoadService.getToolList(request);

        if (initialToolList == null || initialToolList.isEmpty()) {
            if (isNeedMessage) {
                //给所有当前登录的session推送消息
                WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                    sendMessage(webSocketServer, session, jsonObject, data, 1, "正在连接载荷管理机:" + synchronousDataSource.getIp() + "......失败,会话关闭！", false);
                });
            }
            return null;
        } else {
            if (isNeedMessage) {
                WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                    sendMessage(webSocketServer, session, jsonObject, data, 1, "正在连接载荷管理机:" + synchronousDataSource.getIp() + "......成功,正在获取数据", true);
                });
            }
        }

        //给所有当前登录的session推送消息
        if (isNeedMessage) {
            WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                sendMessage(webSocketServer, session, jsonObject, data, 2, "正在获取同步载荷......！", true);
            });
        }

        //准备新增到数据库的载荷列表
        List<MpDevopsLoadSceneLog> waitAddList = new ArrayList<>();
        for (LinkedHashMap tool : initialToolList) {
            if (tool != null) {
                tool.put("source", "1");
                waitAddList.add(convertToolToJSONObject(tool));
            }
        }

        //本地的载荷列表数据
        List<MpDevopsLoad> nowLoadList = loadMapper.getLoadList(new JSONObject());
        //收集id相同的数据,id相同的loadId不需要新增,只需要修改
        List<MpDevopsLoadSceneLog> sameLoads = new ArrayList<>();
        // 创建一个新集合用于存储需要移除的元素，避免直接在遍历中修改原集合
        List<MpDevopsLoadSceneLog> toRemove = new ArrayList<>();

        for (MpDevopsLoadSceneLog waitAdd : waitAddList) {
            if (waitAdd.getId() != null) {
                String waitAddId = waitAdd.getId();
                for (MpDevopsLoad nowLoad : nowLoadList) {
                    if (nowLoad.getId() != null && waitAddId.equals(nowLoad.getId())) {
                        // 找到了匹配项，添加到待移除列表并加入sameLoads
                        toRemove.add(waitAdd);
                        sameLoads.add(waitAdd);
                        break; // 匹配到一个后即可跳出内层循环
                    }
                }
            }
        }
        waitAddList.removeAll(toRemove);
        //更新本地数据
        Boolean batchUpdateData = true;
        if (sameLoads != null && sameLoads.size() > 0) {
            batchUpdateData = loadMapper.batchUpdateData(sameLoads);
        }
        if (batchUpdateData && waitAddList != null && !waitAddList.isEmpty()) {
            if (!loadMapper.addLoads(waitAddList)) {
                if (isNeedMessage) {
                    WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                        sendMessage(webSocketServer, session, jsonObject, data, 3, "载荷数据入库失败,会话关闭！", false);
                    });
                }
                return null;
            }
        }

        //睡眠两秒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (isNeedMessage) {
            WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                data.put("stage3", "载荷数据入库成功！");
                sendMessage(webSocketServer, session, jsonObject, data, 3, "载荷数据入库成功！", true);
                data.put("stage4", "共同步" + waitAddList.size() + "个载荷");
                sendMessage(webSocketServer, session, jsonObject, data, 4, "共同步" + waitAddList.size() + "个载荷以及更新" + sameLoads.size() + "条载荷", true);
                data.put("stage5", "载荷列表同步成功,会话关闭！");
                sendMessage(webSocketServer, session, jsonObject, data, 5, "载荷列表同步成功,会话关闭！", true);
            });
        }

        JSONObject requestData = new JSONObject();
        //同步设置来源
        requestData.put("source", synchronousDataSource);
        requestData.put("dataNum", sameLoads.size() + waitAddList.size());
        return requestData;
    }

    private void sendMessage(WebSocketServer webSocketServer, Session session, Map map, Map data,
                             Integer sort, String message, Boolean result) {
        try {
            data.put("stage" + sort, message);
            map.put("data", data);
            // 将LinkedHashMap转换为JSONObject
            JSONObject object = new JSONObject(map);
            // 将JSONObject转换为JSON字符串
            String jsonString = object.toJSONString();
            webSocketServer.sendMessage(session, jsonString);
            if (!result) {
                webSocketServer.onClose(session);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MpDevopsLoadSceneLog convertToolToJSONObject(LinkedHashMap tool) {
        MpDevopsLoadSceneLog mpDevopsLoadSceneLog = new MpDevopsLoadSceneLog();
        if (tool != null) {
            mpDevopsLoadSceneLog.setId(tool.get("id") != null ? tool.get("id").toString() : null);
            mpDevopsLoadSceneLog.setToolName(tool.get("toolName") != null ? tool.get("toolName").toString() : null);
            mpDevopsLoadSceneLog.setToolDescription(tool.get("toolDescribe") != null ? tool.get("toolDescribe").toString() : null);
            mpDevopsLoadSceneLog.setConvertRunTable(tool.get("toolPlatform") != null ? tool.get("toolPlatform").toString() : null);
            mpDevopsLoadSceneLog.setReadMe(tool.get("toolReadme") != null ? tool.get("toolReadme").toString() : null);
            mpDevopsLoadSceneLog.setReadMeName(tool.get("readMeName") != null ? tool.get("readMeName").toString() : null);
            //toolType 工具类型 暂未同步
            mpDevopsLoadSceneLog.setConvertControlType(tool.get("controlType") != null ? tool.get("controlType").toString() : null);
            mpDevopsLoadSceneLog.setControlPort(tool.get("controlPort") != null ? tool.get("controlPort").toString() : null);
            //imageName 镜像名称 暂未同步
            mpDevopsLoadSceneLog.setDefaultCmd(tool.get("defaultCmd") != null ? tool.get("defaultCmd").toString() : null);
            mpDevopsLoadSceneLog.setCreatorId(tool.get("creatorId") != null ? tool.get("creatorId").toString() : null);
            Object createTimeObj = tool.get("createTime");
            if (createTimeObj != null) {
                try {
                    LocalDateTime createTime = LocalDateTime.parse(createTimeObj.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    mpDevopsLoadSceneLog.setCreateTime(createTime);
                } catch (DateTimeParseException e) {
                    // 记录错误日志，跳过此条数据或使用默认值
                    LocalDateTime createTime = LocalDateTime.now();
                    mpDevopsLoadSceneLog.setCreateTime(createTime);
                }
            }

            Object updateTimeObj = tool.get("updateTime");
            if (updateTimeObj != null) {
                try {
                    LocalDateTime updateTime = LocalDateTime.parse(updateTimeObj.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    mpDevopsLoadSceneLog.setUpdateTime(updateTime);
                } catch (DateTimeParseException e) {
                    // 记录错误日志，跳过此条数据或使用默认值
                    LocalDateTime updateTime = LocalDateTime.now();
                    mpDevopsLoadSceneLog.setUpdateTime(updateTime);
                }
            }

            mpDevopsLoadSceneLog.setConvertActionType(tool.get("toolTags") != null ? tool.get("toolTags").toString() : null);
            mpDevopsLoadSceneLog.setSource(tool.get("source") != null ? tool.get("source").toString() : null);
            mpDevopsLoadSceneLog.setAttachment(tool.get("attachment") != null ? tool.get("attachment").toString() : null);
            mpDevopsLoadSceneLog.setAttachmentName(tool.get("attachmentName") != null ? tool.get("attachmentName").toString() : null);
            mpDevopsLoadSceneLog.setCover(tool.get("cover") != null ? tool.get("cover").toString() : "/api/mp/file/877/298/b99fc02c34507fb82c5a800bcbe65287.png");
            mpDevopsLoadSceneLog.setCoverName(tool.get("coverName") != null ? tool.get("coverName").toString() : null);
        }
        return mpDevopsLoadSceneLog;
    }

    /*
     * 修改载荷
     * */
    public Boolean updateLoadByPk(MpDevopsLoad requestData) {
        Boolean updateLoadByPk;
        if (!StringUtils.isEmpty(requestData.getPk())) {
            MpDevopsLoad load = loadMapper.getLoadByPk(requestData.getPk().toString());
            updateLoadByPk = loadMapper.updateLoadByPk(requestData.getPk().toString(), requestData);

            // 更新载荷平台该载荷的标签
            OutLoadModifyTagsRequest request = new OutLoadModifyTagsRequest();
            request.setLoadId(load.getId());
            // 获取标签 code
            String s = requestData.getActionType();
            String[] split = (s != null && !s.isEmpty()) ? s.split(",") : new String[0];
            // 使用 commonCodeService 获取 codeMap
            Map<String, String> codeMap = new HashMap<String, String>();
            if(split != null && split.length > 0){
                codeMap = commonCodeService.getDatasByCodes(split);
            }
            // 收集 codeMap 的 value 转成 String[]
            request.setTags(Arrays.stream(split)
                    .map(codeMap::get)
                    .filter(Objects::nonNull)
                    .toArray(String[]::new));
            Boolean result = outLoadService.modifyLoadTags(request);
        } else {
            return false;
        }
        return updateLoadByPk;
    }

    /*
     * 新增载荷
     * */
    public ServiceResult addLoad(JSONObject requestData) {
        if (requestData != null) {
            //来源(1载荷平台 2本地)
            requestData.put("source", "2");
            Boolean addLoad = loadMapper.addLoad(requestData);
            if (!addLoad) {
                return new ServiceResult(null, BaseErrorCode.ERROR, "载荷数据新增失败").asResult();
            }
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "无参数").asResult();
        }
    }

    /*
     * 删除载荷
     * */
    public Boolean deleteLoadByPk(JSONObject requestData) {
        Boolean deleteLoadByPk;
        if (requestData != null && requestData.get("pk") != null && !requestData.get("pk").toString().equals("")) {
            deleteLoadByPk = loadMapper.deleteLoadByPk(requestData.get("pk").toString());
        } else {
            return false;
        }
        return deleteLoadByPk;
    }

    /*
     * 根据场景id获取绑定的载荷
     * */
    public List<MpDevopsLoad> getLoadBySceneId(String sceneId) {
        //节点表
        List<CloudPluginSceneElementModel> loadList = csceneInstanceService.listLoads(sceneId);
        List<String> loadIds = loadList.stream()
                .map(CloudPluginSceneElementModel::getLoadToolId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<MpDevopsLoad> getLoadByIds = new ArrayList<>();
        if (loadIds != null && loadIds.size() > 0) {
            getLoadByIds = loadMapper.getLoadByIds(loadIds);
        }
        return getLoadByIds;
    }

    /*
     * 载荷执行结果统计
     * */
    public Map<String, Integer> getLoadExecutionResultStatistics(LoadResultSearchRequest requestData) {
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResult(requestData);

        final String SUCCESS_CODE = "1005010101";
        final String FAIL_CODE = "1005010102";
        final String NO_RESULT_CODE = "1005010103";

        Map<String, Integer> statusCount = list.stream()
                .filter(log -> log.getResultStatus() != null)
                .collect(Collectors.groupingBy(
                        MpDevopsLoadSceneLog::getResultStatus,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Integer successTimes = statusCount.getOrDefault(SUCCESS_CODE, 0);
        Integer failTimes = statusCount.getOrDefault(FAIL_CODE, 0);
        Integer noResultForTheTime = statusCount.getOrDefault(NO_RESULT_CODE, 0);

        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("ask_times", list.size());
        resultMap.put("success_times", successTimes);
        resultMap.put("fail_times", failTimes);
        resultMap.put("have_result", successTimes + failTimes);
        resultMap.put("no_result_for_the_time", noResultForTheTime);
        return resultMap;
    }

    public ListObjServiceResult checkGetResultParam(LoadResultSearchRequest request) {
        String sceneId = request.getSceneId() != null && !request.getSceneId().equals("") ? request.getSceneId() : "";
        if (sceneId.equals("")) {
            return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "scene_id不能为空").asResult();
        }
        //将time转换成LocaldateTime
        LocalDateTime beginTimeLocalDateTime = null;
        LocalDateTime finishTimeLocalDateTime = null;
        if (request.getBeginTime() != null && request.getOverTime() != null) {
            try {
                beginTimeLocalDateTime = LocalDateTime.parse(request.getBeginTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                finishTimeLocalDateTime = LocalDateTime.parse(request.getOverTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                //比较时间
                if (beginTimeLocalDateTime.isAfter(finishTimeLocalDateTime)) {
                    return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "结束时间不能小于开始时间");
                }
            } catch (Exception e) {
                return new ListObjServiceResult(null, BaseErrorCode.PARAMS_INVALID, "开始时间转换时间类型错误");
            }
        }
        return new ListObjServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS");
    }

    /*
     * 载荷执行结果
     * */
    public ListObjServiceResult getLoadExecutionResult(LoadResultSearchRequest requestData) {
        Integer offset = 0;
        Integer pageSize = 10;
        if (requestData.getPage() != null && requestData.getPerPage() != null) {
            offset = (requestData.getPage() - 1) * requestData.getPerPage();
            pageSize = requestData.getPerPage();
            requestData.setOffset(offset);
            requestData.setPageSize(pageSize);
        }
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResult(requestData);
        String[] codes = {CommonStatusCodeEnum.RESULT_STATUS.getCode(), CommonStatusCodeEnum.ACTION_TYPE.getCode(),
                CommonStatusCodeEnum.RESULT_TYPE.getCode(), CommonStatusCodeEnum.CONTROL_TYPE.getCode()};
        Map<String, String> codeMap = commonCodeService.convertNextLevelDatasByCodes(codes);
        //将list中的resultStatus转换成codeName
        list = list.stream()
                .map(log -> {
                    log.setControlTypeName(log.getControlType() != null ? codeMap.get(log.getControlType()) : log.getControlType());
                    if (StringUtils.hasText(log.getActionType())) {
                        log.setActionTypeConvertName(log.getActionType(), codeMap);
                    }
                    log.setResultTypeName(log.getResultType() != null ? codeMap.get(log.getResultType()) : log.getResultType());
                    log.setResultStatusName(log.getResultStatus() != null ? codeMap.get(log.getResultStatus()) : log.getResultStatus());
                    return log;
                })
                .collect(Collectors.toList());
        //总数量
        Integer num = loadMapper.getLoadExecutionResultNum(requestData);
        return new ListObjServiceResult(list, num).asResult();
    }

    /*
     * 载荷执行结果
     * */
    public List<MpDevopsLoadSceneLog> getLoadExecutionResultList(LoadResultSearchRequest requestData) {
        return loadMapper.getLoadExecutionResult(requestData);
    }

    public MpDevopsLoadSceneLog getLoadExecutionResultByLogPk(String pk) {
        return loadMapper.getLoadExecutionResultByLogPk(pk);
    }

    /*
     * 删除载荷执行结果
     * */
    public ServiceResult deleteLoadExecutionResultByPk(MpDevopsLoadSceneLog requestData) {
        Boolean deleteLoadExecutionResultByPk;
        if (requestData.getPk() != null && !requestData.getPk().equals("")) {
            deleteLoadExecutionResultByPk = loadMapper.deleteLoadExecutionResultByPk(requestData.getPk().toString());
        } else {
            return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "主键参数为空").asResult();
        }

        if (deleteLoadExecutionResultByPk) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "删除成功").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "删除失败").asResult();
        }
    }

    /*
     * 修改载荷执行结果
     * */
    public ServiceResult updateLoadExecutionResultByPk(MpDevopsLoadSceneLog requestData) {
        Boolean updateLoadExecutionResultByPk;
        if (requestData.getPk() != null && !requestData.getPk().equals("")) {
            updateLoadExecutionResultByPk = loadMapper.updateLoadExecutionResultByPk(requestData.getPk().toString(), requestData);
        } else {
            return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "主键参数为空").asResult();
        }

        if (updateLoadExecutionResultByPk) {
            return new ServiceResult(null, BaseErrorCode.SUCCESS, "修改成功").asResult();
        } else {
            return new ServiceResult(null, BaseErrorCode.PARAMS_INVALID, "修改失败").asResult();
        }
    }

    /*
     * 新增载荷执行结果
     * */
    public Boolean addLoadExecutionResult(MpDevopsLoadSceneLog requestData) {
        Boolean addLoadExecutionResult = loadMapper.addLoadExecutionResult(requestData);
        return addLoadExecutionResult;
    }

    private boolean validateRequestData(List<LoadRequest> requestData) {
        if (requestData == null || requestData.isEmpty()) {
            return false;
        }
        return requestData.stream().allMatch(request ->
                request != null &&
                        !StringUtils.isEmpty(request.getTaskId()) &&
                        !StringUtils.isEmpty(request.getInstanceId())
        );
    }

    private String convertStatusToCode(String status) {
        if ("成功".equals(status)) {
            return CommonConvertStatusEnum.STATUS_SUCCESS_CODE.getCode();
        } else if ("失败".equals(status)) {
            return CommonConvertStatusEnum.STATUS_FAIL_CODE.getCode();
        }
        return ""; // 或者处理无效状态的逻辑
    }

    /*
     * 批量新增载荷执行结果
     * */
    public ServiceResult updateLoadExecutionOutResult(List<LoadRequest> requestData) {
        // 验证参数
        if (!validateRequestData(requestData)) {
            return new ServiceResult(null, BaseErrorCode.ERROR, "列表数据部分参数缺失").asResult();
        }

        // 状态字符串转状态码
        requestData.forEach(req -> req.setResultStatus(convertStatusToCode(req.getResultStatus())));

        //根据taskId获取场景id
        String sceneId = loadMapper.getSceneIdByTaskId(requestData.get(0).getTaskId());

        //根据sceneId查找对应ws会话推送消息
        Session session = LoadResultWebSocketServer.sceneIdAndSession.get(sceneId);
        if (session != null) {
            LoadResultWebSocketServer loadResultWebSocketServer = LoadResultWebSocketServer.sessionPools.get(session);
            if (loadResultWebSocketServer != null) {
                Map jsonObject = new LinkedHashMap<>();
                jsonObject.put("code", CommonFunctionCodeEnum.LOAD_RESULT_ACTION_CODE.getCode());
                Map data = new LinkedHashMap<>();
                Integer sort = 0;
                String message = requestData.toString();
                Boolean result = true;
                sendResultMessage(loadResultWebSocketServer, session, jsonObject, data, sort, message, result);
            }
        }

        // 更新数据库
        Boolean success = requestData.size() > 0 && loadMapper.updateLoadExecutionOutResult(requestData);
        return success
                ? new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult()
                : new ServiceResult(null, BaseErrorCode.ERROR, "批量新增载荷执行结果失败").asResult();
    }

    private void sendResultMessage(LoadResultWebSocketServer webSocketServer, Session session, Map map, Map data,
                                   Integer sort, String message, Boolean result) {
        try {
            data.put("stage" + sort, message);
            map.put("data", data);
            // 将LinkedHashMap转换为JSONObject
            JSONObject object = new JSONObject(map);
            // 将JSONObject转换为JSON字符串
            String jsonString = object.toJSONString();
            webSocketServer.sendMessage(session, jsonString);
            if (!result) {
                webSocketServer.onClose(session);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 载荷执行结果导入
     * */
    public ServiceResult importLoadExecutionResult(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.getName() == null || multipartFile.getName().equals("")) {
            return new ServiceResult(null, BaseErrorCode.ERROR, "文件无名称").asResult();
        } else if (multipartFile != null && !multipartFile.getOriginalFilename().endsWith("xlsx")) {
            return new ServiceResult(null, BaseErrorCode.ERROR, "文件格式错误,请上传xlsx表格文件").asResult();
        }
        //当前时间
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (multipartFile.getOriginalFilename().endsWith("xlsx")) {
            // 创建临时文件
            File outputFile = new File(UUID.randomUUID().toString() + "temp.xlsx");
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, outputFile.toPath());
            }
            FileInputStream fis = new FileInputStream(outputFile);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            //数据集初始化
            List<MpDevopsLoadSceneLog> data = new ArrayList<>();
            //读取第一行
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            //当前行数
            int rowTime = 0;
            for (Row row : sheet) {
                if (rowTime == 0) {
                    int cellNum = 0;
                    for (Cell cell : row) {
                        map.put(cellNum, Objects.requireNonNull(getCellValue(cell)).toString());
                        cellNum++;
                    }
                } else {
                    MpDevopsLoadSceneLog log = new MpDevopsLoadSceneLog();
                    int cellNum = 0;
                    for (Cell cell : row) {
                        getCellValueToSetBody(cellNum, map, cell, log, formattedDate);
                        cellNum++;
                    }
                    data.add(log);
                }
                rowTime++;
            }
            //outputFile临时文件删除
            outputFile.delete();
            if (data.size() > 0) {
                // 读取表格数据最后一行的时候会有空的脏数据，不删不影响查询
                data.remove(data.size() - 1);
                Boolean success = loadMapper.addLoadLogs(data);
                if (!success) {
                    return new ServiceResult(null, BaseErrorCode.ERROR, "导入失败").asResult();
                }
            }
            workbook.close();
        }
        return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    private void getCellValueToSetBody(int cellNum, LinkedHashMap<Integer, String> map, Cell cell, MpDevopsLoadSceneLog log, String formattedDate) {
        String cellValue = Objects.requireNonNull(getCellValue(cell)).toString();
        switch (map.get(cellNum)) {
            case "工具id":
                log.setId(cellValue);
                break;
            case "工具名称":
                break;
            case "工具实例id":
                log.setInstanceId(cellValue);
                break;
            case "场景id":
                log.setSceneId(cellValue);
                break;
            case "阶段id":
                log.setStageId(cellValue);
                break;
            case "任务id":
                log.setTaskId(cellValue);
                break;
            case "作用类型":
                break;
            case "执行类型":
                break;
            case "结果类型":
                log.setResultType(cellValue);
                break;
            case "结果状态":
                log.setResultStatus(cellValue);
                break;
            case "结果日志":
                log.setResultLog(cellValue);
                break;
            case "记录时间":
                // 新增时会自动加上目前时间
                break;
            default:
                break;
        }
    }

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /*
     * 载荷执行结果导出
     * */
    public Boolean exportLoadExecutionResult(HttpServletRequest request, HttpServletResponse response,
                                             LoadResultExportRequest requestData) throws IOException {
        if (requestData.getExportType() == null || (requestData.getExportType() != 1 && requestData.getExportType() != 2 && requestData.getExportType() != 3)) {
            return false;
        }
        Date date = new Date();
        //格式化当前时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        //创建日期对象，用于生成Excel表格名称，时间截止到秒，避免Excel名称重复
        String day = sdf.format(date);
        String fileTempName = "resultLog_" + day;
        String sheetTempName = "resultLog";
        // 获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        String formattedDate = formatter.format(currentDate);
        //数据
        LoadResultSearchRequest requestParam = new LoadResultSearchRequest();
        BeanUtils.copyProperties(requestData, requestParam);
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResult(requestParam);
        //导出文件xlsx
        if (requestData.getExportType() != null && requestData.getExportType() == 1) {
            try {
//                String[] codes = {CommonStatusCodeEnum.RESULT_STATUS.getCode(), CommonStatusCodeEnum.ACTION_TYPE.getCode(),
//                        CommonStatusCodeEnum.RESULT_TYPE.getCode(), CommonStatusCodeEnum.CONTROL_TYPE.getCode()};
//                //code对应值
//                Map<String, String> nextLevelMapsByCodes = commonCodeService.convertNextLevelDatasByCodes(codes);
                //***********************************数据写入Excel***********************************
                String[] title = {"工具id", "工具名称", "工具实例id", "场景id", "阶段id", "任务id",
                        "作用类型", "执行类型", "结果类型", "结果状态", "结果日志", "记录时间"};
                int ol = list.size() + 1;
                String[][] content = new String[ol][];
                String tool_id = "";
                String tool_name = "";
                String instance_id = "";
                String scene_id = "";
                String stage_id = "";
                String task_id = "";
                String action_type = "";
                String control_type = "";
                String result_type = "";
                String result_status = "";
                String result_log = "";
                String create_time = "";
                for (int i = 0; i < ol; i++) {
                    content[i] = new String[title.length];
                    if (i == list.size()) {
                        content[i][0] = tool_id;
                        content[i][1] = tool_name;
                        content[i][2] = instance_id;
                        content[i][3] = scene_id;
                        content[i][4] = stage_id;
                        content[i][5] = task_id;
                        content[i][6] = action_type;
                        content[i][7] = control_type;
                        content[i][8] = result_type;
                        content[i][9] = result_status;
                        content[i][10] = result_log;
                        content[i][11] = create_time;
                    } else {
                        MpDevopsLoadSceneLog obj = list.get(i);
                        content[i][0] = obj.getId() != null ? obj.getId() : "";
                        content[i][1] = obj.getToolName() != null ? obj.getToolName() : "";
                        content[i][2] = obj.getInstanceId() != null ? obj.getInstanceId() : "";
                        content[i][3] = obj.getSceneId() != null ? obj.getSceneId() : "";
                        content[i][4] = obj.getStageId() != null ? obj.getStageId() : "";
                        content[i][5] = obj.getTaskId() != null ? obj.getTaskId() : "";
                        content[i][6] = obj.getActionType() != null ? obj.getActionType() : "";
                        content[i][7] = obj.getControlType() != null ? obj.getControlType() : "";
                        content[i][8] = obj.getResultType() != null ? obj.getResultType() : "";
                        content[i][9] = obj.getResultStatus() != null ? obj.getResultStatus() : "";
                        content[i][10] = obj.getResultLog() != null ? obj.getResultLog() : "";
                        content[i][11] = formattedDate;
                    }
                }
                //创建HSSFWorkbook
                HSSFWorkbook wb = getHSSFWorkbook(sheetTempName, title, content, null);
                Boolean setResponseHeader = this.setResponseHeader(response, fileTempName + ".xlsx");
                if (!setResponseHeader) {
                    return false;
                }
                OutputStream os = response.getOutputStream();
                wb.write(os);
                os.flush();
                os.close();
            } catch (Exception e) {
                return false;
            }
        } else if (requestData.getExportType() != null && requestData.getExportType() == 2) {
            // 将jsonList导出为json文件并通过HttpServletResponse响应
            exportJsonToResponse(response, list);
        } else if (requestData.getExportType() != null && requestData.getExportType() == 3) {
            // 将list导出为xml文件并通过HttpServletResponse响应
            exportXmlToResponse(response, list);
        }
        return true;
    }

    private void exportJsonToResponse(HttpServletResponse response, List<MpDevopsLoadSceneLog> list) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (MpDevopsLoadSceneLog obj : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", obj.getId());
            jsonObject.put("tool_name", obj.getToolName());
            jsonObject.put("instance_id", obj.getInstanceId());
            jsonObject.put("scene_id", obj.getSceneId());
            jsonObject.put("stage_id", obj.getStageId());
            jsonObject.put("task_id", obj.getTaskId());
            jsonObject.put("action_type", obj.getActionType());
            jsonObject.put("control_type", obj.getControlType());
            jsonObject.put("result_type", obj.getResultType());
            jsonObject.put("result_status", obj.getResultStatus());
            jsonObject.put("result_log", obj.getResultLog());
            jsonObject.put("create_time", obj.getCreateTime().toString());
            jsonList.add(jsonObject);
        }
        // 创建Jackson的ObjectMapper实例
        ObjectMapper mapper = new ObjectMapper();
        // 格式化输出
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=export.json");
        try {
            // 将jsonList转换为JSON并写入HttpServletResponse的输出流
            mapper.writeValue(response.getOutputStream(), jsonList);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportXmlToResponse(HttpServletResponse response, List<MpDevopsLoadSceneLog> list) {
        StringBuffer xmlnodes = new StringBuffer();
        if (list != null && list.size() > 0) {
            xmlnodes.append("<?" + "xml version=\"1.0\" encoding=\"UTF-8\" " + "?>");
            xmlnodes.append("<mpDevopsLoadSceneLogs>");
            for (int i = 0; i < list.size(); i++) {
                MpDevopsLoadSceneLog log = list.get(i);
                xmlnodes.append("<mpDevopsLoadSceneLog id='" + log.getId()
                        + "' resultType='" + log.getResultType()
                        + "' instanceId='" + log.getInstanceId()
                        + "' createTime='" + log.getCreateTime().toString()
                        + "' actionType='" + log.getActionType()
                        + "' resultStatus='" + log.getResultStatus()
                        + "' stageId='" + log.getStageId()
                        + "' sceneId='" + log.getSceneId()
                        + "' taskId='" + log.getTaskId()
                        + "' resultLog='" + log.getResultLog()
                        + "' controlType='" + log.getControlType()
                        + "'>" + log.getToolName() + "</mpDevopsLoadSceneLog>");
            }
            xmlnodes.append("</mpDevopsLoadSceneLogs>");
        }
        // 创建Jackson的XmlMapper实例
        XmlMapper xmlMapper = new XmlMapper();
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=export.xml");
        try {
            // 将list转换为XML并写入HttpServletResponse的输出流
            xmlMapper.writeValue(response.getOutputStream(), xmlnodes);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //发送响应流方法
    public Boolean setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook wb) {
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        //声明列对象
        HSSFCell cell = null;
        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        //创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }

    public static boolean isValidIPPortCombo(String url) {
        // 初步正则匹配URL结构
        Pattern pattern = Pattern.compile("^http://(?:[\\w-]+\\.)+[\\w-]+|http://(?:\\[?[\\da-fA-F:\\.]+\\]?|localhost)\\:?\\d+$");
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            return false; // 格式不匹配
        }

        try {
            // 分割URL获取IP和端口部分
            String[] parts = url.split("//")[1].split(":");
            String ipAddress = parts[0];
            int port;

            // IPv6地址需要特殊处理
            if (ipAddress.startsWith("[") && ipAddress.endsWith("]")) {
                ipAddress = ipAddress.substring(1, ipAddress.length() - 1);
            }

            // 验证IP地址
            InetAddress inetAddress = InetAddress.getByName(ipAddress);

            // 验证端口号
            if (parts.length > 1) {
                port = Integer.parseInt(parts[1]);
                if (port < 0 || port > 65535) {
                    return false; // 端口号不在合法范围内
                }
            } else {
                return false; // 缺少端口号
            }

            return true; // IP和端口都有效
        } catch (UnknownHostException | NumberFormatException e) {
            return false; // IP地址无效或端口号格式错误
        }
    }

    /*
     * 载荷数据同步设置保存
     * */
    public ServiceResult synchronousDataSource(MpDevopsLoadSource requestData) {
        String pk = requestData.getPk() != null ? requestData.getPk().toString() : "";
        String ip = requestData.getIp() != null ? requestData.getIp().toString() : "";
        String synchronizationProtocol = requestData.getSynchronizationProtocol() != null ? requestData.getSynchronizationProtocol() : "";
        //暂不做校验
        String authenticationType = requestData.getAuthenticationType() != null ? requestData.getAuthenticationType() : "";
        //认证方式(1:密码认证 2:cookie)
        if (authenticationType.equals("1")) {
            String userName = requestData.getUserName() != null ? requestData.getUserName() : "";
            String password = requestData.getPassword() != null ? requestData.getPassword() : "";
            //暂不做校验
        } else if (authenticationType.equals("2")) {
            String cookie = requestData.getCookie() != null ? requestData.getCookie() : "";
            //暂不做校验
        }

        //ip校验
        boolean validIPPortCombo = isValidIPPortCombo(ip);
        if (!validIPPortCombo) {
            return new ServiceResult(null, BaseErrorCode.ERROR, "ip格式设置不正确,请传如下格式:http://10.108.1.10:5002").asResult();
        }

        if (pk.equals("")) {
            Boolean addSynchronousDataSource = loadMapper.addSynchronousDataSource(requestData);
            if (!addSynchronousDataSource) {
                return new ServiceResult(null, BaseErrorCode.ERROR, "载荷数据同步设置保存失败").asResult();
            }
        } else {
            Boolean synchronousDataSource = loadMapper.synchronousDataSource(requestData);
            if (!synchronousDataSource) {
                return new ServiceResult(null, BaseErrorCode.ERROR, "载荷数据同步设置新增失败").asResult();
            }
        }
        return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
    }

    /*
     * 载荷数据同步设置查看
     * */
    public MpDevopsLoadSource getSynchronousDataSource(JSONObject requestData) {
        MpDevopsLoadSource result = loadMapper.getSynchronousDataSource();
        return result;
    }

    public String getLoadTransformServiceUrl() {
        // 从环境变量中读取
        return LOAD_TRANSFORM_URL;
    }

    /*
     * 根据载荷执行结果工具列表
     * */
    public List<MpDevopsLoad> getToolListByloadIds(IdsRequest requestData) {
        List<String> ids = requestData.getIds();
        //根据ids查询工具
        List<MpDevopsLoad> data = new ArrayList<>();
        if (ids != null && ids.size() > 0) {
            data = loadMapper.getToolListByloadIds(ids);
        }
        Map<String, String> nextLevelMapsByCodes = commonCodeService.getNextLevelMapsByCodes(CommonStatusCodeEnum.CODE.getCode());
        for (MpDevopsLoad load : data) {
            load.setRunTableName(nextLevelMapsByCodes.get(load.getRunTable()));
            if (StringUtils.hasText(load.getActionType())) {
                load.setActionTypeConvertName(load.getActionType(), nextLevelMapsByCodes);
            }
            load.setControlTypeName(nextLevelMapsByCodes.get(load.getControlType()));
        }
        return data;
    }

    public List<JSONObject> getLoadResultBySceneId(String sceneId) {
        List<String> loadIds = loadMapper.getLoadsBySceneId(sceneId);
        List<JSONObject> loadResultBySceneId = null;
        if (loadIds != null && loadIds.size() > 0) {
            //查询该场景绑定的载荷及记录
            loadResultBySceneId = loadMapper.getLoadResultByLoads(loadIds);
            return loadResultBySceneId;
        } else {
            return loadResultBySceneId;
        }
    }

    public LinkedList<JSONObject> getTop5Loads(String sceneId) {
        LoadResultSearchRequest requestData = new LoadResultSearchRequest();
        requestData.setSceneId(sceneId);
        List<MpDevopsLoadSceneLog> loadExecutionResult = getLoadExecutionResultList(requestData);
        //计算id出现次数最多的五条
        Map<String, Integer> map = new HashMap<>();
        if (loadExecutionResult != null && loadExecutionResult.size() > 0) {
            for (int i = 0; i < loadExecutionResult.size(); i++) {
                String id = loadExecutionResult.get(i).getId();
                if (map.containsKey(id)) {
                    map.put(id, map.get(id) + 1);
                } else {
                    map.put(id, 1);
                }
            }
        }
        List<String> idList = new ArrayList<>();
        //根据map的value值大小取出前五条
        if (map != null && map.size() > 0) {
            //排序
            List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            //取前五条
            if (list.size() > 5) {
                list = list.subList(0, 5);
            } else {
                list = list.subList(0, list.size());
            }
            //取出list中所有的key
            for (int i = 0; i < list.size(); i++) {
                idList.add(list.get(i).getKey());
            }
        }
        LinkedList<JSONObject> returnList = new LinkedList<>();
        if (idList != null && idList.size() > 0) {
            List<MpDevopsLoad> loadByIds = loadMapper.getLoadByIds(idList);
            for (MpDevopsLoad load : loadByIds) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("load_name", load.getToolName());
                jsonObject.put("attack_num", map.get(load.getId()));
                returnList.add(jsonObject);
            }
        }
        return returnList;
    }

    public List<MpDevopsLoadSceneLog> getLoadResultByNodeIds(String[] nodeIds) {
        //节点表
        List<CloudPluginSceneElementModel> loadList = csceneInstanceService.getLoadResultByNodeIds(nodeIds);
        List<String> loadIds = loadList.stream()
                .map(CloudPluginSceneElementModel::getLoadToolId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<MpDevopsLoadSceneLog> getLoadResultByPks = new ArrayList<>();
        if (loadIds != null && loadIds.size() > 0) {
            getLoadResultByPks = loadMapper.getLoadResultByLoadIds(loadIds);
        }
        if (getLoadResultByPks != null && getLoadResultByPks.size() > 0) {
            String[] codes = {CommonStatusCodeEnum.RESULT_STATUS.getCode(), CommonStatusCodeEnum.ACTION_TYPE.getCode(),
                    CommonStatusCodeEnum.RESULT_TYPE.getCode(), CommonStatusCodeEnum.CONTROL_TYPE.getCode()};
            Map<String, String> codeMap = commonCodeService.convertNextLevelDatasByCodes(codes);
            //将list中的resultStatus转换成codeName
            getLoadResultByPks = getLoadResultByPks.stream()
                    .map(log -> {
                        log.setControlTypeName(log.getControlType() != null ? codeMap.get(log.getControlType()) : log.getControlType());
                        if (StringUtils.hasText(log.getActionType())) {
                            log.setActionTypeConvertName(log.getActionType(), codeMap);
                        }
                        log.setResultTypeName(log.getResultType() != null ? codeMap.get(log.getResultType()) : log.getResultType());
                        log.setResultStatusName(log.getResultStatus() != null ? codeMap.get(log.getResultStatus()) : log.getResultStatus());
                        return log;
                    })
                    .collect(Collectors.toList());
        }
        return getLoadResultByPks;
    }

    public ServiceResult importLoadExecutionResultFile(MultipartFile file, String sceneId) throws MpRuntimeException, IOException {
        ServiceResult uploadResult = commonFileService.uploadFile
                (file.getInputStream(), file.getOriginalFilename(), FileUtil.getSuffix(file.getOriginalFilename()), CommonFileServiceImpl.LOAD_RESULT_FILE);
        if (uploadResult.isSuccess()) {
            loadMapper.importLoadExecutionResultFile(sceneId, uploadResult.getData());
        }
        return uploadResult;
    }

    public List<MpDevopsLoadSceneUploadLogFile> getLoadExecutionResultUploadFile(String sceneId) {
        List<MpDevopsLoadSceneUploadLogFile> fileList = loadMapper.getLoadExecutionResultUploadFile(sceneId);
        if (fileList != null && !fileList.isEmpty()) {
            for (MpDevopsLoadSceneUploadLogFile file : fileList) {
                String fileName = file.getFileName();
                if (fileName != null && fileName.contains(".")) {
                    // 提取文件后缀名
                    String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                    file.setFileType(fileType);
                } else {
                    // 如果没有后缀名，可以设置一个默认值或者保持null
                    file.setFileType("unknown");
                }
            }
        }
        return fileList;
    }

    public Boolean deleteLoadExecutionResultUploadFile(Integer pk) {
        MpDevopsLoadSceneUploadLogFile file = loadMapper.getFile(pk);
        Boolean flag = loadMapper.deleteLoadExecutionResultUploadFile(pk);
        if (flag) {
            try {
                FileUtil.del(file.getFile());
            } catch (Exception e) {
                throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
            }
        }
        return flag;
    }

    public Boolean batchAddLoadResult(List<MpDevopsLoadSceneLog> list) {
        return loadMapper.batchAddLoadResult(list);
    }
}

