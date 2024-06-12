package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.base.bo.MpSysCodeEnum;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.common.constant.codeEnum.CommonConvertStatusEnum;
import cn.cyberpeace.mp.common.constant.codeEnum.CommonFunctionCodeEnum;
import cn.cyberpeace.mp.common.constant.codeEnum.CommonStatusCodeEnum;
import cn.cyberpeace.mp.common.service.impl.CommonCodeServiceImpl;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSource;
import cn.cyberpeace.mp.cscene.repository.mapper.LoadMapper;
import cn.cyberpeace.mp.cscene.request.load.LoadRequest;
import cn.cyberpeace.mp.cscene.request.load.LoadResultSearchRequest;
import cn.cyberpeace.mp.cscene.request.load.OutLoadTaskUpdateRequest;
import cn.cyberpeace.mp.cscene.request.ojj.IdsRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.websocket.LoadResultWebSocketServer;
import cn.cyberpeace.mp.cscene.websocket.WebSocketServer;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE;

@Service
public class LoadServiceImpl {
    @Autowired
    private LoadMapper loadMapper;

    @Autowired
    private CommonCodeServiceImpl commonCodeService;

    @Autowired
    private OutLoadServiceImpl outLoadService;

    @Resource
    private CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper;

    @Resource
    private CloudPluginSceneElementMapper cloudPluginSceneElementMapper;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    /*
     * 载荷页面
     * */
    public JSONObject getLoadList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        requestData.put("pageSize", pageSize);
        requestData.put("offset", (pageNum - 1) * pageSize);
        List<MpDevopsLoad> list = loadMapper.getLoadList(requestData);
        //list每个成员的imageType赋值
        list.forEach(item -> {
            item.setImageType("load");
        });
        Integer total = loadMapper.getLoadListNum(requestData);
        JSONObject result = new JSONObject();
        result.put("source", list);
        result.put("total", total);
        return result;
    }

    //调用ojj服务
    public List getBindLoopholes(String id) {
        //todo 服务地址
        String url = "http://10.108.1.10:80/api/vuln/admin/vuln/load_code";
        url = url + "?load_id=" + id;
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        String token = JwtCommon.getToken();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
        headersMap.put("Authorization", token);

        String result = null;
        try {
            result = HttpUtils.sendHttpGet(url, headersMap, 2000);
        } catch (IOException e) {
            return null;
        }
        List list = new ArrayList();
        Map<String, Object> dataMap = JsonUtils.parseMap(result);
        if (dataMap != null && dataMap.get("code") != null &&
                dataMap.get("code").equals("VULN-000000") && dataMap.get("data") != null) {
            list = (List) dataMap.get("data");
        }
        return list;
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
        request.put("pagenum", 1);
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
        mpDevopsLoadSceneLog.setId(tool.get("id").toString());
        mpDevopsLoadSceneLog.setControlPort(tool.get("controlPort").toString());
        mpDevopsLoadSceneLog.setControlType(tool.get("controlType").toString());
        mpDevopsLoadSceneLog.setRunTable(tool.get("runTable").toString());
        mpDevopsLoadSceneLog.setActionType(tool.get("actionType").toString());
        mpDevopsLoadSceneLog.setCreatorId(tool.get("creatorId").toString());
        mpDevopsLoadSceneLog.setDefaultCmd(tool.get("defaultCmd").toString());
        mpDevopsLoadSceneLog.setToolDescription(tool.get("toolDescribe").toString());
        mpDevopsLoadSceneLog.setToolName(tool.get("toolName").toString());
        mpDevopsLoadSceneLog.setSource(tool.get("source").toString());
        mpDevopsLoadSceneLog.setToolPlatform(tool.get("toolPlatform").toString());
        mpDevopsLoadSceneLog.setReadMe(tool.get("readMe").toString());
        mpDevopsLoadSceneLog.setReadMeName(tool.get("readMeName").toString());
        mpDevopsLoadSceneLog.setAttachment(tool.get("attachment").toString());
        mpDevopsLoadSceneLog.setAttachmentName(tool.get("attachmentName").toString());
        mpDevopsLoadSceneLog.setCover(tool.get("cover").toString());
        mpDevopsLoadSceneLog.setCoverName(tool.get("coverName").toString());

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
        return mpDevopsLoadSceneLog;
    }

    /*
     * 修改载荷
     * */
    public Boolean updateLoadByPk(JSONObject requestData) {
        Boolean updateLoadByPk;
        if (requestData != null && requestData.get("pk") != null && !requestData.get("pk").toString().equals("")) {
            updateLoadByPk = loadMapper.updateLoadByPk(requestData.get("pk").toString(), requestData);
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
//        List<CloudPluginSceneElementModel> loadList = csceneInstanceService.listLoads(sceneId);
//        List<String> loadIds = loadList.stream()
//                .map(CloudPluginSceneElementModel::getLoadToolId)
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.toList());
//        List<MpDevopsLoad> getLoadByPks = new ArrayList<>();
//        if (loadIds != null && loadIds.size() > 0) {
//            getLoadByPks = loadMapper.getLoadByIds(loadIds);
//        }
//        return getLoadByPks;

        //暂用这个
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", 1);
        jsonObject.put("pageSize", 100);
        JSONObject loads = getLoadList(new JSONObject());
        List<MpDevopsLoad> getLoadByPks = (List) loads.get("source");
        return getLoadByPks;
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
        List<MpSysCodeEnum> nextLevelDatasByCodes = commonCodeService.getNextLevelDatasByCodes(codes);
        //将nextLevelDatasByCodes中的code和codeName转换成map
        Map<String, String> codeMap = nextLevelDatasByCodes.stream()
                .collect(Collectors.toMap(MpSysCodeEnum::getCode, MpSysCodeEnum::getCodeName));
        //将list中的resultStatus转换成codeName
        list = list.stream()
                .map(log -> {
                    log.setControlTypeName(log.getControlType() != null ? codeMap.get(log.getControlType()) : log.getControlType());
                    log.setActionTypeName(log.getActionType() != null ? codeMap.get(log.getActionType()) : log.getActionType());
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
        for (LoadRequest request : requestData) {
            if (request.getTaskId() == null || request.getTaskId().isEmpty() || request.getInstanceId() == null || request.getInstanceId().isEmpty()) {
                return false;
            }
        }
        return true;
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
        }
        FileInputStream fis = null;
        if (multipartFile.getOriginalFilename().endsWith("xlsx") || multipartFile.getOriginalFilename().endsWith("xls")) {
            // 创建临时文件对象（使用UUID作为文件名以避免冲突）
            File excelFile = File.createTempFile("temp_excel_" + TextUtils.uuid(), ".xlsx");
            // 将MultipartFile的内容转移到临时文件
            multipartFile.transferTo(excelFile);
            fis = new FileInputStream(excelFile);
        } else {
            return new ServiceResult(null, BaseErrorCode.ERROR, "文件格式错误,请上传xlsx、xls表格文件").asResult();
        }
        if (multipartFile.getOriginalFilename().endsWith("xlsx")) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            //数据集初始化
            List<List<Object>> data = new ArrayList<>();
            //当前行数
            int rowTime = 0;
            for (Row row : sheet) {
                if (rowTime == 0) {
                    //读取第一行
                    List<Object> rowData = new ArrayList<>();
                    //"工具名称", "记录时间", "阶段", "作用类型", "执行类型", "结果类型", "结果状态"
                    for (Cell cell : row) {
                        String cellValue = Objects.requireNonNull(getCellValue(cell)).toString();
                        rowData.add(cellValue);
                    }
                    data.add(rowData);
                } else {
                    //读取第一行标题
                    List<Object> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        rowData.add(getCellValue(cell));
                    }
                    data.add(rowData);
                }
                rowTime++;
            }
            //打印
            for (List<Object> row : data) {
                for (Object cellValue : row) {
                    System.out.print(cellValue + "\t");
                }
                System.out.println();
            }
            workbook.close();
        } else if (multipartFile.getOriginalFilename().endsWith("xls")) {
            Workbook workbook = new HSSFWorkbook(fis); // 使用HSSFWorkbook处理.xls文件
            Sheet datatypeSheet = workbook.getSheetAt(0); // 获取第一个工作表
            Iterator<Row> iterator = datatypeSheet.iterator();
            //数据集初始化
            List<List<Object>> data = new ArrayList<>();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                List<String> cellData = new ArrayList();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    String cellValue = Objects.requireNonNull(getCellValue(currentCell)).toString();
                    cellData.add(cellValue);
                }
                data.add(Collections.singletonList(cellData));
            }
            //打印
            for (List<Object> row : data) {
                for (Object cellValue : row) {
                    System.out.print(cellValue + "\t");
                }
                System.out.println();
            }
            workbook.close();
        }
        return new ServiceResult(null, BaseErrorCode.SUCCESS, "SUCCESS").asResult();
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
                                             LoadResultSearchRequest requestData) throws IOException {
        //导出文件xlsx
        try {
            List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResult(requestData);
            Map<String, String> nextLevelMapsByCodes = commonCodeService.getNextLevelMapsByCodes("1005");
            Date date = new Date();
            //格式化当前时间格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            //创建日期对象，用于生成Excel表格名称，时间截止到秒，避免Excel名称重复
            String day = sdf.format(date);
            //***********************************数据写入Excel***********************************
            String[] title = {"工具名称", "记录时间", "阶段", "作用类型", "执行类型", "结果类型", "结果状态"};
            String fileName = "载荷执行结果" + day + ".xls";
            String sheetName = "载荷执行结果";
            int ol = list.size() + 1;
            String[][] content = new String[ol][];
            String tool_name = "";
            String create_time = "";
            String stage_id = "";
            String action_type = "";
            String control_type = "";
            String result_type = "";
            String result_status = "";
            for (int i = 0; i < ol; i++) {
                content[i] = new String[title.length];
                if (i == list.size()) {
                    content[i][0] = tool_name;
                    content[i][1] = create_time;
                    content[i][2] = stage_id;
                    content[i][3] = action_type;
                    content[i][4] = control_type;
                    content[i][5] = result_type;
                    content[i][6] = result_status;
                } else {
                    MpDevopsLoadSceneLog obj = list.get(i);
                    content[i][0] = obj.getToolName() != null ? obj.getToolName() : "";
                    content[i][1] = obj.getCreateTime() != null ? obj.getCreateTime().toString() : "";
                    content[i][2] = obj.getStageId() != null ? obj.getStageId() : "";
                    content[i][3] = nextLevelMapsByCodes.get(obj.getActionType());
                    content[i][4] = nextLevelMapsByCodes.get(obj.getControlType());
                    content[i][5] = obj.getResultType() != null ? obj.getResultType() : "";
                    content[i][6] = nextLevelMapsByCodes.get(obj.getResultStatus());
                }
            }
            //创建HSSFWorkbook
            HSSFWorkbook wb = getHSSFWorkbook(sheetName, title, content, null);
            Boolean setResponseHeader = this.setResponseHeader(response, fileName);
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
        return true;
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
        return data;
    }

    public List<MpDevopsLoad> getToolByLoopholes(JSONObject requestData) {
        return null;
    }

    public List<MpDevopsLoad> getToolBytactics(JSONObject requestData) {
        return null;
    }

    /**
     * 获取阶段载荷访问地址
     */
    public Map<String, Object> getTaskNet(JSONObject requestData) {
        //新增执行结果日志
        Integer pk = addResultLog(requestData);
        Map<String, Object> result = outLoadService.getTaskNet(requestData);
        return result;
    }

    private Integer addResultLog(JSONObject requestData) {
        //log表新增初始数据
        requestData.put("resultStatus", "1005010103");
        requestData.put("resultType", "自动上报");
        Integer pk = loadMapper.addResult(requestData);
        return pk;
    }

    private Boolean updateResultLog(Integer pk, Map<String, Object> startInstance) {
        Boolean updateResultLog = true;
        if (startInstance == null || startInstance.get("code") == null || !startInstance.get("code").toString().equals("0")) {
            //startInstance方法执行失败
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resultStatus", "1005010102");
            updateResultLog = loadMapper.updateLoadByPk(pk.toString(), jsonObject);
        }
        return updateResultLog;
    }

    public Boolean updateLoadParam(OutLoadTaskUpdateRequest requestData) {
        if (requestData.getTaskId() == null || requestData.getTaskId().equals("")) {
            return false;
        }
        //todo 校验参数列表字段值
        String taskDescribe = requestData.getTaskDescribe();
        String execCommand = requestData.getExecCommand();
        Boolean updateTaskInfo = outLoadService.updateTaskInfo(requestData);
        return updateTaskInfo;
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

    //调用ojj方法
    public List getStagesBySceneId(String sceneId) {
        //todo 服务地址
        String url = "http://10.108.1.10:80/api/vuln/admin/vuln/load_code";
        url = url + "?scene_id=" + sceneId;
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        String token = JwtCommon.getToken();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
        headersMap.put("Authorization", token);

        String result = null;
        try {
            result = HttpUtils.sendHttpGet(url, headersMap, 2000);
        } catch (IOException e) {
            return null;
        }
        List list = new ArrayList();
        Map<String, Object> dataMap = JsonUtils.parseMap(result);
        if (dataMap != null && dataMap.get("code") != null &&
                dataMap.get("code").equals("VULN-000000") && dataMap.get("data") != null) {
            list = (List) dataMap.get("data");
        }
        return list;
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
        List<MpDevopsLoad> loadByIds = loadMapper.getLoadByIds(idList);
        LinkedList<JSONObject> returnList = new LinkedList<>();
        for (MpDevopsLoad load : loadByIds) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("load_name", load.getToolName());
            jsonObject.put("attack_num", map.get(load.getId()));
            returnList.add(jsonObject);
        }
        return returnList;
    }

    public ServiceResult loadTransform(String file, String transformType) {
        String newFile = "/api/mp/file/229\\\\882\\\\4fcbb45241f63fb79c3390b24987cc46.zip";
        return new ServiceResult(newFile, BaseErrorCode.SUCCESS).asResult();
    }
}

