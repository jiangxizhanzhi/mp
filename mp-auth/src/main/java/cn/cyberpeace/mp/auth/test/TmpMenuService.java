package cn.cyberpeace.mp.auth.test;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.hutool.core.collection.CollectionUtil;
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

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class TmpMenuService {

    @Autowired
    private TargetService targetService;

    public void process() {
//        String path = "/Users/sternberg/work/saining/all_permissions_json_0908.json";
        String path = "";
        String s = readJsonFile(path);
        List<TmpMenuDTO> menus = JSON.parseArray(s, TmpMenuDTO.class);
        List<TargetModel> targets = new ArrayList<>();

        for (TmpMenuDTO menu : menus) {
//            String id = IdGenerator.generateUUID();
            menu.setId(menu.getAuthority());
            // 一开始parentid为null
            recursion(targets, menu, "");
        }

        log.info("size:{}", targets.size());

        /*for (TargetModel target : targets) {
            // 需要把 cn.cyberpeace.mp.base.service.fields.BaseModelFields.readonlyFields这个里面的`BaseModel::getId`拿掉，不然塞入的id不起作用
            targetService.create(target);
        }*/

        for (int i = 0; i < targets.size(); i++) {
            targetService.create(targets.get(i));
        }
    }

    private TargetModel convert(TmpMenuDTO menuDTO, String parentId) {
        TargetModel targetModel = new TargetModel();
        targetModel.setId(menuDTO.getId());
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
//                String id = IdGenerator.generateUUID();
                child.setId(child.getAuthority());
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
