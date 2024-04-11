package cn.cyberpeace.mp.cscene.service.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoad;
import cn.cyberpeace.mp.cscene.model.MpDevopsLoadSceneLog;
import cn.cyberpeace.mp.cscene.repository.mapper.LoadMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoadServiceImpl {

    @Autowired
    private LoadMapper loadMapper;

    @Autowired
    OutLoadServiceImpl outLoadService;

    /*
     * 载荷页面
     * */
    public JSONObject getLoadList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        requestData.put("pageSize", pageSize);
        requestData.put("offset", (pageNum - 1) * pageSize);
        List<MpDevopsLoad> list = loadMapper.getLoadList(requestData);
        Integer total = loadMapper.getLoadListotal(requestData);
        JSONObject result = new JSONObject();
        result.put("source", list);
        result.put("total", total);
        return result;
    }

    /*
     * 载荷数据同步
     * */
    @Transactional
    public Boolean synchronousData() {
        JSONObject requestData = new JSONObject();
        List<LinkedHashMap> toolList = outLoadService.getToolList(requestData);
        if (toolList == null || toolList.isEmpty()) {
            return false;
        }
        List<JSONObject> list = new ArrayList<>();
        for (LinkedHashMap tool : toolList) {
            if (tool != null) {
                list.add(convertToolToJSONObject(tool));
            }
        }
        try {
            Boolean deleteData = loadMapper.deleteData();
            if (deleteData) {
                return loadMapper.addLoads(list);
            }
        } catch (Exception e) {
            // 记录错误日志、回滚事务等
            throw e; // 或者包装成自定义异常抛出
        }
        return true;
    }

    private JSONObject convertToolToJSONObject(LinkedHashMap tool) {
        JSONObject jsonObject = new JSONObject(true);

        jsonObject.put("id", tool.get("id"));
        jsonObject.put("controlPort", tool.get("controlPort"));
        jsonObject.put("controlType", tool.get("controlType"));
        jsonObject.put("creatorId", tool.get("creatorId"));
        jsonObject.put("defaultCmd", tool.get("defaultCmd"));
        jsonObject.put("imageName", tool.get("imageName"));
        jsonObject.put("toolDescription", tool.get("toolDescribe")); //这里的键名与原始数据中的键名不一致
        jsonObject.put("toolName", tool.get("toolName"));
        jsonObject.put("toolPlatform", tool.get("toolPlatform"));
        jsonObject.put("readMe", tool.get("toolReadme"));

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
    public Boolean addLoad(JSONObject requestData) {
        Boolean addLoad;
        if (requestData != null) {
            addLoad = loadMapper.addLoad(requestData);
        } else {
            return false;
        }
        return addLoad;
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
    public List getLoadBySceneId(String sceneId) {
        //todo 需要场景载荷关联表
        JSONObject loadList = getLoadList(new JSONObject());
        return (List) loadList.get("source");
    }

    /*
     * 载荷执行结果统计
     * */
    public Map<String, Integer> getLoadExecutionResultStatistics(JSONObject requestData) {
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestData);
        Map<String, Integer> map = new HashMap();
        //请求次数
        map.put("askTimes", 0);
        //成功次数
        map.put("successTimes", 0);
        //失败次数
        map.put("failTimes", 0);
        //已有结果
        map.put("haveResult", 0);
        //暂无结果
        map.put("noResultForTheTime", 0);
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
    public Boolean deleteLoadExecutionResultByPk(JSONObject requestData) {
        Boolean deleteLoadExecutionResultByPk;
        if (requestData != null && requestData.get("pk") != null && !requestData.get("pk").toString().equals("")) {
            deleteLoadExecutionResultByPk = loadMapper.deleteLoadExecutionResultByPk(requestData.get("pk").toString());
        } else {
            return false;
        }
        return deleteLoadExecutionResultByPk;
    }

    /*
     * 修改载荷执行结果
     * */
    public Boolean updateLoadExecutionResultByPk(JSONObject requestData) {
        Boolean updateLoadExecutionResultByPk;
        if (requestData != null && requestData.get("pk") != null && !requestData.get("pk").toString().equals("")) {
            updateLoadExecutionResultByPk = loadMapper.updateLoadExecutionResultByPk(requestData.get("pk").toString(), requestData);
        } else {
            return false;
        }
        return updateLoadExecutionResultByPk;
    }

    /*
     * 新增载荷执行结果
     * */
    public Boolean addLoadExecutionResult(JSONObject requestData) {
        Boolean addLoadExecutionResult;
        if (requestData != null) {
            addLoadExecutionResult = loadMapper.addLoadExecutionResult(requestData);
        } else {
            return false;
        }
        return addLoadExecutionResult;
    }

    /*
     * 载荷执行结果导入
     * */
    public Boolean importLoadExecutionResult(MultipartFile file) {
        if (file != null && file.getName() != null && !file.getName().equals("")) {
            if (file.getOriginalFilename().endsWith("xlsx") || file.getOriginalFilename().endsWith("xls")) {
                return true;
            }
        }
        return false;
    }

    /*
     * 载荷执行结果导出
     * */
    public Boolean exportLoadExecutionResult(HttpServletRequest request, HttpServletResponse response,
                                             JSONObject requestData) throws IOException {
        //导出文件xlsx
        try {
            List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestData);
            Date date = new Date();
            //格式化当前时间格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            //创建日期对象，用于生成Excel表格名称，时间截止到秒，避免Excel名称重复
            String day = sdf.format(date);
            //***********************************数据写入Excel***********************************
            String[] title = {"主键", "id", "工具名称", "工具描述", "工具平台", "作用类型", "运行平台", "控制类型"};
            String fileName = "载荷执行结果" + day + ".xls";
            String sheetName = "载荷执行结果";
            int ol = list.size() + 1;
            String[][] content = new String[ol][];
            String pk = "";
            String id = "";
            String tool_name = "";
            String tool_description = "";
            String tool_platform = "";
            String action_type = "";
            String run_table = "";
            String control_type = "";
            for (int i = 0; i < ol; i++) {
                content[i] = new String[title.length];
                if (i == list.size()) {
                    content[i][0] = pk;
                    content[i][1] = id;
                    content[i][2] = tool_name;
                    content[i][3] = tool_description;
                    content[i][4] = tool_platform;
                    content[i][5] = action_type;
                    content[i][6] = run_table;
                    content[i][7] = control_type;
                } else {
                    MpDevopsLoadSceneLog obj = list.get(i);
                    content[i][0] = obj.getPk().toString();
                    content[i][1] = obj.getId();
                    content[i][2] = obj.getToolName();
                    content[i][3] = obj.getToolDescription();
                    content[i][4] = obj.getToolPlatform();
                    content[i][5] = obj.getActionType();
                    content[i][6] = obj.getRunTable();
                    content[i][7] = obj.getControlType();
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
        String loadId = requestData.get("loadId") != null ? requestData.get("loadId").toString() : "";
        //界面类型
        String interfaceType = requestData.get("interfaceType") != null ? requestData.get("interfaceType").toString() : "";
        //部署类型
        String deployment = requestData.get("deployment") != null ? requestData.get("deployment").toString() : "";
        List<JSONObject> paramList = requestData.get("paramList") != null ? (List<JSONObject>) requestData.get("paramList") : new ArrayList();
        String paramListString = paramList != null ? paramList.toString() : "";
        Boolean stageBindLoad = loadMapper.stageBindLoad(addType, sceneId, stageId, loadId, interfaceType, deployment, paramListString);
        return stageBindLoad;
    }

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

    public JSONObject getSynchronousDataSource(JSONObject requestData) {
        JSONObject result = loadMapper.getSynchronousDataSource();
        return result;
    }

    public List<JSONObject> getNamesByCode(JSONObject requestData) {
        return loadMapper.getNamesByCode(requestData);
    }

    public List<MpDevopsLoad> getToolListByResult(JSONObject requestData) {
        List<MpDevopsLoadSceneLog> list = loadMapper.getLoadExecutionResultStatistics(requestData);
        List<String> newList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (!newList.contains("list.get(i).getId()")) {
                newList.add(list.get(i).getId());
            }
        }
        //根据ids查询工具
        List<MpDevopsLoad> data = new ArrayList<>();
        if (newList != null && newList.size() > 0) {
            data = loadMapper.getLoadListByIds(newList);
        }
        return data;
    }

//    public JSONObject getThirdTypesAndToolsByTopoId(String topoId, String cloudType) {
//        JSONObject requestData = new JSONObject();
//        requestData.put("sceneId",topoId);
//        getToolListByResult(requestData);
//        loadMapper.getToolsByTopoId();
//
//        return null;
//    }
}

