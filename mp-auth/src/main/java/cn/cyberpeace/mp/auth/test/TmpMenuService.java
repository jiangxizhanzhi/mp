package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class TmpMenuService {

    @Autowired
    private TargetService targetService;

    public void process() {
        String path = "/Users/sternberg/Downloads/all_permissions_json.json";
        String s = readJsonFile(path);
        List<TmpMenuDTO> menus = JSON.parseArray(s, TmpMenuDTO.class);
        List<TargetModel> targets = new ArrayList<>();

        for (TmpMenuDTO menu : menus) {
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            menu.setId(id);
            // 一开始parentid为null
            recursion(targets, menu, "");
        }

        log.info("size:{}", targets.size());

        for (TargetModel target : targets) {
            targetService.create(target);
        }
    }

    private TargetModel convert(TmpMenuDTO menuDTO, String parentId) {
        TargetModel targetModel = new TargetModel();
        targetModel.setType(TargetModel.TYPE_MENU);
        targetModel.setName(menuDTO.getName());
        targetModel.setIdentifier(menuDTO.getValue());
        targetModel.setPath(menuDTO.getPath_name());
        targetModel.setParent(parentId);
        return targetModel;
    }


    private void recursion(List<TargetModel> targets, TmpMenuDTO menu, String parentId) {
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


    private String readJsonFile(String fileName) {
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
