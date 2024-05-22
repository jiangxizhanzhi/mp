package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.common.service.impl.CommonCodeServiceImpl;
import cn.cyberpeace.mp.cscene.model.*;
import cn.cyberpeace.mp.cscene.repository.mapper.LoadMapper;
import cn.cyberpeace.mp.cscene.request.load.LoadRequest;
import cn.cyberpeace.mp.cscene.request.ojj.IdsRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.websocket.WebSocketServer;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

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
        //list中每个成员的category赋值2
        list.forEach(item -> {
            item.setCategory(2);
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
        jsonObject.put("code", "MP-synchronousData-by-outService");
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

        if (isNeedMessage) {
            //给所有当前登录的session推送消息
            WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                sendMessage(webSocketServer, session, jsonObject, data, 1, "正在连接载荷管理机:" + synchronousDataSource.getIp() + "......成功", true);
            });
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
                    sendMessage(webSocketServer, session, jsonObject, data, 2, "获取载荷列表失败,会话关闭！", false);
                });
            }
            return null;
        }

        //给所有当前登录的session推送消息
        if (isNeedMessage) {
            WebSocketServer.sessionPools.forEach((session, webSocketServer) -> {
                sendMessage(webSocketServer, session, jsonObject, data, 2, "正在获取同步载荷......完成！", true);
            });
        }

        //准备新增到数据库的载荷列表
        List<JSONObject> list = new ArrayList<>();
        for (LinkedHashMap tool : initialToolList) {
            if (tool != null) {
                tool.put("source", "1");
                list.add(convertToolToJSONObject(tool));
            }
        }
        List<JSONObject> sameLoads;

        //本地的载荷列表数据
        List<MpDevopsLoad> nowLoadList = loadMapper.getLoadList(new JSONObject());
        //收集id相同的数据,id相同的loadId不需要新增,只需要修改
        sameLoads = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < nowLoadList.size(); j++) {
                if (list.get(i).get("id") != null && nowLoadList.get(j).getId() != null) {
                    if (list.get(i).get("id").toString().equals(nowLoadList.get(j).getId())) {
                        sameLoads.add(list.get(i));
                        //删除相同项
                        list.remove(list.get(i));
                    }
                }
            }
        }
        //更新本地数据
        Boolean deleteData = true;
        if (sameLoads != null && sameLoads.size() > 0) {
            deleteData = loadMapper.batchUpdateData(sameLoads);
        }
        if (deleteData && list != null && !list.isEmpty()) {
            if (!loadMapper.addLoads(list)) {
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
                data.put("stage4", "共同步" + list.size() + "个载荷");
                sendMessage(webSocketServer, session, jsonObject, data, 4, "共同步" + list.size() + "个载荷以及更新" + sameLoads.size() + "条载荷", true);
                data.put("stage5", "载荷列表同步成功,会话关闭！");
                sendMessage(webSocketServer, session, jsonObject, data, 5, "载荷列表同步成功,会话关闭！", true);
            });
        }

        JSONObject requestData = new JSONObject();
        //同步设置来源
        requestData.put("source", synchronousDataSource);
        requestData.put("dataNum", sameLoads.size() + list.size());
        return requestData;
    }

    public void synchronousData(Session session) throws IOException {
        //推送的消息返回格式
        Map jsonObject = new LinkedHashMap<>();
        jsonObject.put("code", "MP-synchronousData-by-outService");
        Map data = new LinkedHashMap<>();
        WebSocketServer webSocketServer = WebSocketServer.sessionPools.get(session);
        //同步设置获取
        MpDevopsLoadSource synchronousDataSource = getSynchronousDataSource(new JSONObject());
        if (synchronousDataSource == null) {
            sendMessage(webSocketServer, session, jsonObject, data, 1, "获取载荷平台ip失败,会话关闭！", false);
        }

        sendMessage(webSocketServer, session, jsonObject, data, 1, "正在连接载荷管理机:" + synchronousDataSource.getIp() + "......成功", true);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        OutLoadServiceImpl outLoadService = BeanCommon.getBean(OutLoadServiceImpl.class);
        JSONObject request = new JSONObject();
        request.put("pagenum", 1);
        request.put("pagesize", 1000);
        List<LinkedHashMap> toolList = outLoadService.getToolList(request);
        if (toolList == null || toolList.isEmpty()) {
            sendMessage(webSocketServer, session, jsonObject, data, 2, "获取载荷列表失败,会话关闭！", false);
        }
        sendMessage(webSocketServer, session, jsonObject, data, 2, "正在获取同步载荷......完成！", true);
        List<JSONObject> list = new ArrayList<>();
        for (LinkedHashMap tool : toolList) {
            if (tool != null) {
                tool.put("source", "1");
                list.add(convertToolToJSONObject(tool));
            }
        }
        List<JSONObject> sameLoads;
        try {
            //本地的载荷列表数据
            List<MpDevopsLoad> nowLoadList = loadMapper.getLoadList(new JSONObject());
            //收集id相同的数据,id相同的loadId不需要新增,只需要修改
            sameLoads = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < nowLoadList.size(); j++) {
                    if (list.get(i).get("id") != null && nowLoadList.get(j).getId() != null) {
                        if (list.get(i).get("id").toString().equals(nowLoadList.get(j).getId())) {
                            sameLoads.add(list.get(i));
                            //删除相同项
                            list.remove(list.get(i));
                        }
                    }
                }
            }
            //更新本地数据
            Boolean deleteData = true;
            if (sameLoads != null && sameLoads.size() > 0) {
                deleteData = loadMapper.batchUpdateData(sameLoads);
            }
            if (deleteData && list != null && !list.isEmpty()) {
                if (!loadMapper.addLoads(list)) {
                    sendMessage(webSocketServer, session, jsonObject, data, 3, "载荷数据入库失败,会话关闭！", false);
                }
            }
        } catch (Exception e) {
            // 记录错误日志、回滚事务等
            throw e; // 或者包装成自定义异常抛出
        }
        data.put("stage3", "载荷数据入库成功！");
        sendMessage(webSocketServer, session, jsonObject, data, 3, "载荷数据入库成功！", true);
        data.put("stage4", "共同步" + list.size() + "个载荷");
        sendMessage(webSocketServer, session, jsonObject, data, 4, "共同步" + list.size() + "个载荷以及更新" + sameLoads.size() + "条载荷", true);
        data.put("stage5", "载荷列表同步成功,会话关闭！");
        sendMessage(webSocketServer, session, jsonObject, data, 5, "载荷列表同步成功,会话关闭！", true);
        webSocketServer.onClose(session);
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

    private JSONObject convertToolToJSONObject(LinkedHashMap tool) {
        JSONObject jsonObject = new JSONObject(true);

        jsonObject.put("id", tool.get("id").toString());
        jsonObject.put("controlPort", tool.get("controlPort"));
        jsonObject.put("controlType", tool.get("controlType"));
        jsonObject.put("runTable", tool.get("runTable"));
        jsonObject.put("actionType", tool.get("actionType"));
        jsonObject.put("creatorId", tool.get("creatorId"));
        jsonObject.put("defaultCmd", tool.get("defaultCmd"));
        jsonObject.put("toolDescription", tool.get("toolDescribe")); //这里的键名与原始数据中的键名不一致
        jsonObject.put("toolName", tool.get("toolName"));
        jsonObject.put("source", tool.get("source"));
        jsonObject.put("toolPlatform", tool.get("toolPlatform"));
        jsonObject.put("readMe", tool.get("readMe"));
        jsonObject.put("readMeName", tool.get("readMeName"));
        jsonObject.put("cover", tool.get("cover"));
        jsonObject.put("coverName", tool.get("coverName"));
        jsonObject.put("attachment", tool.get("attachment"));
        jsonObject.put("attachmentName", tool.get("attachmentName"));

        Object createTimeObj = tool.get("createTime");
        if (createTimeObj != null) {
            try {
                LocalDateTime createTime = LocalDateTime.parse(createTimeObj.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                jsonObject.put("createTime", createTime.toString());
            } catch (DateTimeParseException e) {
                // 记录错误日志，跳过此条数据或使用默认值
                LocalDateTime createTime = LocalDateTime.now();
                jsonObject.put("createTime", createTime.toString());
            }
        }

        Object updateTimeObj = tool.get("updateTime");
        if (updateTimeObj != null) {
            try {
                LocalDateTime updateTime = LocalDateTime.parse(updateTimeObj.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                jsonObject.put("updateTime", updateTime.toString());
            } catch (DateTimeParseException e) {
                // 记录错误日志，跳过此条数据或使用默认值
                LocalDateTime updateTime = LocalDateTime.now();
                jsonObject.put("updateTime", updateTime.toString());
            }
        }

        return jsonObject;
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
//        if(loadIds!=null && loadIds.size()>0){
//            getLoadByPks = loadMapper.getLoadByPks(loadIds);
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
    public Map<String, Integer> getLoadExecutionResultStatistics(JSONObject requestData) {
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestData);
        Integer successTimes = 0;
        Integer failTimes = 0;
        Integer noResultForTheTime = 0;
        for (MpDevopsLoadSceneLog m : list) {
            if (m.getResultStatus() != null && m.getResultStatus().equals("1005010101")) {
                successTimes++;
            } else if (m.getResultStatus() != null && m.getResultStatus().equals("1005010102")) {
                failTimes++;
            } else if (m.getResultStatus() != null && m.getResultStatus().equals("1005010103")) {
                noResultForTheTime++;
            }
        }
        Map<String, Integer> map = new HashMap();
        //请求次数
        map.put("ask_times", list.size());
        //成功次数
        map.put("success_times", successTimes);
        //失败次数
        map.put("fail_times", failTimes);
        //已有结果
        map.put("have_result", successTimes + failTimes);
        //暂无结果
        map.put("no_result_for_the_time", noResultForTheTime);
        for (int i = 0; i < list.size(); i++) {
            //todo 统计
        }
        return map;
    }

    /*
     * 载荷执行结果
     * */
    public List<MpDevopsLoadSceneLog> getLoadExecutionResult(JSONObject requestData) {
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestData);
        return list;
    }

    /*
     * 删除载荷执行结果
     * */
    public Boolean deleteLoadExecutionResultByPk(MpDevopsLoadSceneLog requestData) {
        Boolean deleteLoadExecutionResultByPk;
        if (requestData.getPk() != null && !requestData.getPk().equals("")) {
            deleteLoadExecutionResultByPk = loadMapper.deleteLoadExecutionResultByPk(requestData.getPk().toString());
        } else {
            return false;
        }
        return deleteLoadExecutionResultByPk;
    }

    /*
     * 修改载荷执行结果
     * */
    public Boolean updateLoadExecutionResultByPk(MpDevopsLoadSceneLog requestData) {
        Boolean updateLoadExecutionResultByPk;
        if (requestData.getPk() != null && !requestData.getPk().equals("")) {
            updateLoadExecutionResultByPk = loadMapper.updateLoadExecutionResultByPk(requestData.getPk().toString(), requestData);
        } else {
            return false;
        }
        return updateLoadExecutionResultByPk;
    }

    /*
     * 新增载荷执行结果
     * */
    public Boolean addLoadExecutionResult(MpDevopsLoadSceneLog requestData) {
        Boolean addLoadExecutionResult = loadMapper.addLoadExecutionResult(requestData);
        return addLoadExecutionResult;
    }

    /*
     * 批量新增载荷执行结果
     * */
    public Boolean updateLoadExecutionOutResult(List<LoadRequest> requestData) {
        Boolean addLoadExecutionOutResult;
        if (requestData.size() > 0) {
            addLoadExecutionOutResult = loadMapper.updateLoadExecutionOutResult(requestData);
        } else {
            return true;
        }
        return addLoadExecutionOutResult;
    }

    /*
     * 载荷执行结果导入
     * */
    public Boolean importLoadExecutionResult(MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && multipartFile.getName() != null && !multipartFile.getName().equals("")) {
            if (multipartFile.getOriginalFilename().endsWith("xlsx") || multipartFile.getOriginalFilename().endsWith("xls")) {
                // 创建临时文件对象（使用UUID作为文件名以避免冲突）
                File excelFile = File.createTempFile("temp_excel_", ".xlsx");
                // 将MultipartFile的内容转移到临时文件
                multipartFile.transferTo(excelFile);
                try (FileInputStream fis = new FileInputStream(excelFile)) {
                    Workbook workbook = new XSSFWorkbook(fis);
                    Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
                    //数据集初始化
                    List<List<Object>> data = new ArrayList<>();

                    //当前行数
                    int rowTime = 0;
                    for (Row row : sheet) {
                        if(rowTime == 0){
                            //读取第一行
                            List<Object> rowData = new ArrayList<>();
                            //"工具名称", "记录时间", "阶段", "作用类型", "执行类型", "结果类型", "结果状态"
                            for (Cell cell : row) {
                                String cellValue = getCellValue(cell).toString();
                                rowData.add(cellValue);
                            }
                            data.add(rowData);
                        }else{
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
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
                                             MpDevopsLoadSceneLog requestData) throws IOException {
        //导出文件xlsx
        try {
            JSONObject requestDataObject = JsonCommon.snakeParse(JsonCommon.stringify(requestData), JSONObject.class);
            List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestDataObject);
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

    /*
     * 阶段测试绑定载荷工具
     * */
    public Boolean stageBindLoad(String addType, JSONObject requestData) {
        String sceneId = requestData.get("sceneId") != null ? requestData.get("sceneId").toString() : "";
        String stageId = requestData.get("stageId") != null ? requestData.get("stageId").toString() : "";
        //载荷id(可以直接指定,可以根据靶标漏洞关联的载荷id,可以根据关联技战术筛选出的载荷id)
        String loadId = requestData.get("loadId") != null ? requestData.get("loadId").toString() : "";
        //界面类型
        String interfaceType = requestData.get("interfaceType") != null ? requestData.get("interfaceType").toString() : "";
        //部署类型
        String deployment = requestData.get("deployment") != null ? requestData.get("deployment").toString() : "";
        String paramList = requestData.get("paramList") != null ? requestData.get("paramList").toString() : "";

        //根据loadId创建任务
        JSONObject request = new JSONObject();
        request.put("taskName", requestData);
        request.put("taskDescribe", requestData);
        request.put("toolId", loadId);
        request.put("execCommand", paramList);
        Map<String, Object> task = outLoadService.createTask(request);
        String taskId;
        if (task != null && task.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) task.get("data");
            if (data != null && data.get("id") != null) {
                taskId = data.get("id").toString();
            } else {
                return false;
            }
        } else {
            return false;
        }
        //新建绑定关系
        Boolean stageBindLoad = loadMapper.stageBindLoad(addType, sceneId, stageId, taskId, loadId, interfaceType, deployment, paramList);
        return stageBindLoad;
    }

    /*
     * 载荷数据同步
     * */
    public Boolean synchronousDataSource(JSONObject requestData) {
        String pk = requestData.get("pk") != null ? requestData.get("pk").toString() : "";
        String ip = requestData.get("ip") != null ? requestData.get("ip").toString() : "";
        String synchronizationProtocol = requestData.get("synchronizationProtocol") != null ? requestData.get("synchronizationProtocol").toString() : "";
        String authenticationType = requestData.get("authenticationType") != null ? requestData.get("authenticationType").toString() : "";
        //认证方式(1:密码认证 2:cookie)
        if (authenticationType.equals("1")) {
            String userName = requestData.get("userName") != null ? requestData.get("userName").toString() : "";
            String password = requestData.get("password") != null ? requestData.get("password").toString() : "";
            if (ip.equals("") || synchronizationProtocol.equals("") || authenticationType.equals("") ||
                    userName.equals("") || password.equals("")) {
                return false;
            }
        } else if (authenticationType.equals("2")) {
            String cookie = requestData.get("cookie") != null ? requestData.get("cookie").toString() : "";
            if (ip.equals("") || synchronizationProtocol.equals("") || authenticationType.equals("") || cookie.equals("")) {
                return false;
            }
        } else {
            return false;
        }
        if (pk.equals("")) {
            Boolean addSynchronousDataSource = loadMapper.addSynchronousDataSource(requestData);
            return addSynchronousDataSource;
        } else {
            Boolean synchronousDataSource = loadMapper.synchronousDataSource(requestData);
            return synchronousDataSource;
        }
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

    public Boolean updateLoadParam(JSONObject requestData) {
        String pk = requestData.get("pk") != null ? requestData.get("pk").toString() : "";
        if (pk.equals("")) {
            return false;
        }
        //todo 校验参数列表字段值
        String paramList = requestData.get("paramList") != null ? requestData.get("paramList").toString() : "";
        Boolean updateBindByPk = loadMapper.updateBindByPk(pk, requestData);
        //todo 重新加载配置重启载荷服务
        outLoadService.updateTaskInfo(requestData);
        outLoadService.stopInstance(requestData);
        outLoadService.startInstance(requestData);
//        修改场景节点载荷参数
        HumanCondition humanCondition = new HumanCondition()
                .filterEqual(CloudPluginSceneElementModel::getResourceId, requestData.get("task_id"))
                .filterEqual(CloudPluginSceneElementModel::getLoadResourceInstanceId, requestData.get("load_id"));
        List<CloudPluginSceneElementModel> list = cloudPluginSceneElementMapper.list(humanCondition);
        list.forEach(cloudPluginSceneElementModel -> {
            cloudPluginSceneElementModel.setLoadExecCommand(paramList);
            cloudPluginSceneElementMapper.update(cloudPluginSceneElementModel);
//            根据场景找到对应的场景模板再根据scene_element修改场景模板节点
            HumanCondition selectHumanCondition = new HumanCondition()
                    .link(CsceneInstanceModel.class)
                    .link(CsceneModel.class)
                    .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getCscene,
                            CsceneModel.class, CsceneModel::getId)
                    .link(SceneConfigModel.class)
                    .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                            SceneConfigModel.class, SceneConfigModel::getId)
                    .link(CloudPluginSceneConfigElementModel.class)
                    .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                            CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                    .currentLink(CsceneInstanceModel.class)
                    .filterEqual(CsceneInstanceModel::getScene, requestData.get("scene_id"))
                    .currentLink(CloudPluginSceneConfigElementModel.class)
                    .filterEqual(CloudPluginSceneConfigElementModel::getSceneElement, cloudPluginSceneElementModel.getSceneElement())
                    .select(new HumanCondition().toDefaultSelectFields(CloudPluginSceneConfigElementModel.class));

            for (CloudPluginSceneConfigElementModel cloudPluginSceneConfigElementModel : cloudPluginSceneConfigElementMapper.list(selectHumanCondition)) {
                cloudPluginSceneConfigElementModel.setLoadExecCommand(paramList);
                cloudPluginSceneConfigElementMapper.update(cloudPluginSceneConfigElementModel);
            }
        });
        return updateBindByPk;
    }

    public List stageBindLoadList(String sceneId) {
        List<MpDevopsLoadStageBind> stageBindLoadList = loadMapper.stageBindLoadList(sceneId);
        //返回数据
        List<Map<String, Object>> resultList = new ArrayList<>();
        //根据stageId分类
        if (stageBindLoadList != null && stageBindLoadList.size() > 0) {
            Map<String, List<MpDevopsLoadStageBind>> map = new HashMap<>();
            for (int i = 0; i < stageBindLoadList.size(); i++) {
                String stageId = stageBindLoadList.get(i).getStageId();
                if (map.containsKey(stageId)) {
                    map.get(stageId).add(stageBindLoadList.get(i));
                } else {
                    List<MpDevopsLoadStageBind> list = new ArrayList<>();
                    list.add(stageBindLoadList.get(i));
                    map.put(stageId, list);
                }
            }
            //map转换成List<Map>
            if (map != null && map.size() > 0) {
                for (Map.Entry<String, List<MpDevopsLoadStageBind>> entry : map.entrySet()) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("stageId", entry.getKey());
                    resultMap.put("data", entry.getValue());
                    resultList.add(resultMap);
                }
            }
        }
        return resultList;
    }

    public Boolean deleteLoadBind(JSONObject requestData) {
        String pk = requestData.get("pk") != null ? requestData.get("pk").toString() : "";
        if (pk.equals("")) {
            return false;
        }
        Boolean deleteLoadByPk = loadMapper.deleteLoadBindByPk(pk);
        //todo 删除载荷服务,关闭实例

        return deleteLoadByPk;
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
        List<MpDevopsLoadSceneLog> loadExecutionResult = getLoadExecutionResult(new JSONObject());
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
}

