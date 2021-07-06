package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.test.TmpMenuDTO;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AllPermissionTest {

    public static void main(String[] args) {
        String path = UserPermissionServiceTest.class.getClassLoader().getResource("all_permissions_json.json").getPath();
        String s = readJsonFile(path);
//        JSONArray arr = JSON.parseArray(s);
        List<TmpMenuDTO> menus = JSON.parseArray(s, TmpMenuDTO.class);
        List<TargetModel> targets = new ArrayList<>();

        for (TmpMenuDTO menu : menus) {
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            menu.setId(id);
            // 一开始parentid为null
            recursion(targets, menu, null);
        }

        System.out.println(targets);

    }


    public static TargetModel convert(TmpMenuDTO menuDTO, String parentId) {
        TargetModel targetModel = new TargetModel();
        targetModel.setType(TargetModel.TYPE_MENU);
        targetModel.setName(menuDTO.getName());
        targetModel.setIdentifier(menuDTO.getValue());
        targetModel.setPath(menuDTO.getPath_name());
        targetModel.setParent(parentId);
        return targetModel;
    }


    public static void recursion(List<TargetModel> targets, TmpMenuDTO menu, String parentId) {
        List<TmpMenuDTO> children = menu.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            for (TmpMenuDTO child : children) {
                String id = UUID.randomUUID().toString().replaceAll("-", "");
                child.setId(id);
                recursion(targets, child, menu.getId());
            }
        }
        TargetModel target = convert(menu, parentId);
        targets.add(target);
    }


    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
