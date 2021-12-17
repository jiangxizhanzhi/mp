package cn.cyberpeace.mp.front.admin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.dto.target.TargetActivDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.front.admin.dto.ActivMenuDTO;
import cn.cyberpeace.mp.front.admin.model.ActivEditionMenuModel;
import cn.cyberpeace.mp.front.admin.model.ActivKeyMenuModel;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class ActivMenuService {

    @Autowired
    private TargetService targetService;

    @Autowired
    private ActivKeyMenuService keyMenuService;

    @Autowired
    private ActivEditionMenuService editionMenuService;

    public void process(MultipartFile jsonFile) {
        String s = readJsonStrFromMultipartFile(jsonFile);
        List<ActivMenuDTO> menus = JSON.parseArray(s, ActivMenuDTO.class);
        List<TargetActivDto> targetActivDtos = new ArrayList<>();

        for (ActivMenuDTO menu : menus) {
            // 以authority内的值为id
            menu.setId(menu.getAuthority());
            // 一开始parentid为null
            recursion(targetActivDtos, menu, "");
        }

        List<TargetModel> targets = targetActivDtos2TargetModels(targetActivDtos);
        log.info("targetActivDtos.size:{}, targets.size:{}", targetActivDtos.size(), targets.size());

        List<ActivKeyMenuModel> keyMenus = targetActivDtos2KeyMenus(targetActivDtos);
        List<ActivEditionMenuModel> editionMenus = targetActivDtos2EditionMenus(targetActivDtos);
        log.info("keyMenus.size:{}", keyMenus.size());
        log.info("editionMenus.size:{}", editionMenus.size());


//        for (int i = 0; i < targets.size(); i++) {
//            targetService.create(targets.get(i));
//        }

    }

    private TargetActivDto activMenuDto2TargetActivDto(ActivMenuDTO menuDTO, String parentId) {
        TargetActivDto target = new TargetActivDto();
        target.setId(menuDTO.getId());
        target.setType(TargetModel.TYPE_MENU);
        target.setName(menuDTO.getName());
        target.setIdentifier(menuDTO.getValue());
        target.setPath(menuDTO.getPath_name());
        target.setParent(parentId);
        target.setActive(menuDTO.getActive());
        target.setEdition(menuDTO.getEdition());
        return target;
    }


    private void recursion(List<TargetActivDto> targets, ActivMenuDTO menu, String parentId) {
        List<ActivMenuDTO> children = menu.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            for (ActivMenuDTO child : children) {
                child.setId(child.getAuthority());
                recursion(targets, child, menu.getId());
            }
        }
        TargetActivDto target = activMenuDto2TargetActivDto(menu, parentId);
        targets.add(target);
    }


    private String readJsonStrFromPath(String filePath) {
//        filePath = "/Users/sternberg/work/saining/all_permissions_json_activ.json";
        String jsonStr = "";
        try {
            File jsonFile = new File(filePath);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
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

    private String readJsonStrFromMultipartFile(MultipartFile file) {
        String jsonStr = "";
        try {
            Reader reader = new InputStreamReader(file.getInputStream(), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<TargetModel> targetActivDtos2TargetModels(List<TargetActivDto> activDtos) {
        List<TargetModel> targetModels =
                activDtos.stream().map(a -> {
                    TargetModel model = new TargetModel();
                    BeanUtils.copyProperties(a, model);
                    return model;
                }).collect(Collectors.toList());

        return targetModels;
    }


    /**
     * 从menuDto中抽取activ_key和menu_id的对应关系
     *
     * @param targetActivDtos
     */
    private List<ActivKeyMenuModel> targetActivDtos2KeyMenus(List<TargetActivDto> targetActivDtos) {
        List<ActivKeyMenuModel> keyMenus = new ArrayList<>();
        targetActivDtos.forEach(targetActivDto -> {
            targetActivDto.getActive().forEach(activKey -> {
                ActivKeyMenuModel keyMenu = new ActivKeyMenuModel();
                keyMenu.setActivKey(activKey);
                keyMenu.setMenu(targetActivDto.getId());
                keyMenus.add(keyMenu);
            });
        });
        return keyMenus;
    }


    /**
     * 从menuDto中抽取edition和menu_id的对应关系
     *
     * @param targetActivDtos
     */
    private List<ActivEditionMenuModel> targetActivDtos2EditionMenus(List<TargetActivDto> targetActivDtos) {
        List<ActivEditionMenuModel> editionMenus = new ArrayList<>();
        targetActivDtos.forEach(targetActivDto -> {
            targetActivDto.getEdition().forEach(edition -> {
                ActivEditionMenuModel editionMenu = new ActivEditionMenuModel();
                editionMenu.setEdition(edition);
                editionMenu.setMenu(targetActivDto.getId());
                editionMenus.add(editionMenu);
            });
        });
        return editionMenus;
    }

}
