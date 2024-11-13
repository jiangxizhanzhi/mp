package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.GXPTUserMapper;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.common.service.impl.CommonConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE;

/**
 * 共性平台方法调用
 */
@Slf4j
@Service
public class GXPTUserServiceImpl {

    @Autowired
    private GXPTUserMapper gxptUserMapper;

    @Autowired
    private CommonConfigServiceImpl commonConfigService;

    /*  account	否	String	账号
            name	否	String	姓名
            passWord	否	String	密码
            idCard	否	String	身份证号
            troops	否	String	所属部队
            educationalDegree	否	String	毕业大学
            nation	否	String	民族
            militaryRank	否	String	军衔
            poloticalOutliik	否	String	政治面貌
            firstDegree	否	String	第一学历(0.未知、1.博士后、2.博士、3.研究生、4.本科 5.专科、6.高中、7.初中)
            health	否	String	是否健康 2-不健康 1-健康
            birthday	否	String	生日时间
            recruitmentTime	否	String	毕业时间
            jobTime	否	String	参加工作时间
            height	否	String	身高
            mhDescribe	否	String	简述
            oncePassword	否	String	用于校验两次输入密码是否一致(不用传值)
            userSex	否	String	性别 1-男 2-女
            identitys	否	List	权限*/
    public Map<String, Object> addUser(UserModel userModel) {
        String url = commonConfigService.getUrlByKey("addUser").getServiceUrl();

        Map<String, Object> formMap = new HashMap();
        formMap.put("account", userModel.getId());
        formMap.put("name", userModel.getName());
        formMap.put("passWord", userModel.getPassword());
        formMap.put("onePassWord", userModel.getPassword());
        formMap.put("idCard", userModel.getIdCard());
        formMap.put("troops", "");
        formMap.put("educationalDegree", "");
        formMap.put("nation", "");
        formMap.put("militaryRank", "");
        formMap.put("poloticalOutliik", "");
        formMap.put("firstDegree", "");
        formMap.put("health", "");
        formMap.put("birthday", "");
        formMap.put("sex", "");
        formMap.put("recruitmentTime", "");
        formMap.put("jobTime", "");
        formMap.put("height", "");
        formMap.put("identity", "");
        formMap.put("mhDescribe", "");
        formMap.put("oncePassword", null);
        formMap.put("userSex", "");
        formMap.put("identitys", new ArrayList<String>());

        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());

        String result = null;
        try {
            result = HttpUtils.sendHttpPost(url, formMap, headersMap);
        } catch (Exception e) {
            return null;
        }
        Map<String, Object> dataMap;
        dataMap = JsonUtils.parseMap(result);
        return dataMap;
    }

    public void updateUser(UserModel user, Map<String, Object> resultMap) {
        try {
            gxptUserMapper.updateUser(user.getId(), resultMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 共性平台删除用户
    public Map<String, Object> delete(String userId) {
        String url = commonConfigService.getUrlByKey("deteleUser").getServiceUrl();
        //根据userid查询info(共性平台的该用户的id)
        String info = gxptUserMapper.getInfoById(userId);
        url = url + "?id=" + info;
        //请求头设置
        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put(ACCEPT_LANGUAGE, ServletUtils.getAcceptLanguage());
        String result = null;
        try {
            result = HttpUtils.sendHttpGet(url, headersMap, 2000);
        } catch (Exception e) {
            log.info("gxpt delete user error: {}", e.getMessage());
        }
        Map<String, Object> dataMap = null;
        if(result != null){
            dataMap = JsonUtils.parseMap(result);
        }
        return dataMap;
    }

    public void bulkDelete(String[] userIds) {
        for(String userId : userIds){
            delete(userId);
        }
    }
}
