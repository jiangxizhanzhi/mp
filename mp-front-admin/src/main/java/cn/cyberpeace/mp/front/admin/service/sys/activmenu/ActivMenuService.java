package cn.cyberpeace.mp.front.admin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.target.TargetActivDto;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.front.admin.dto.ActivMenuDTO;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationItem;
import cn.cyberpeace.mp.front.admin.dto.activation.ActivationResponse;
import cn.cyberpeace.mp.front.admin.model.MenuExtraInfoModel;
import cn.cyberpeace.mp.front.admin.service.sys.SysActivationService;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private MenuReImportSupportService reImportSupportService;

    @Autowired
    private SysActivationService activationService;

    @Autowired
    private TargetService targetService;

    @Autowired
    private MenuExtraInfoService menuExtraInfoService;

    /**
     * 适用场景：重新导入menu.json
     * 1. 清空原有menu, keymenu, editionmenu表。
     * 2. 如果导入menu的时候，系统已经激活，则需要根据查询的激活项目，激活版本，再结合menu自身的active[],edition[]属性，决定visible，再重新导入所有menu。
     * 3. 如果导入menu的时候，系统还未激活，则所有visible默认为1
     *
     * @param jsonFile
     * @return
     */
    public boolean reImportMenus(MultipartFile jsonFile) {
        log.info("reImportMenus...");
        String s = readJsonStrFromMultipartFile(jsonFile);
        List<ActivMenuDTO> menus = JSON.parseArray(s, ActivMenuDTO.class);
        List<TargetModel> targetModels = new ArrayList<>();
        List<MenuExtraInfoModel> menuExtraInfoModels = new ArrayList<>();

        for (ActivMenuDTO menu : menus) {
            // 以authority内的值为id
            menu.setId(menu.getAuthority());
            // 一开始parentid为null
            recursion(targetModels, menuExtraInfoModels, menu, "");
        }

        // todo 根据系统激活与否，激活项目，版本，决定visible属性
        ActivationResponse content = activationService.status();
//        ActivationResponse content = mockActivationContent();
        if (Objects.nonNull(content)) {
            // 系统已激活，暂时忽略激活状态过期的场景
            processMenuVisibility(targetModels, menuExtraInfoModels, content);
        }

        log.info("targetModels.size:{}", targetModels.size());

        boolean success = reImportSupportService.rebuildAllMenusAndExtraInfos(targetModels, menuExtraInfoModels);
        return success;
    }


    /**
     * 适用场景：系统中的总menu不变，需要根据重新激活的激活项，激活版本，重新设置所有菜单的visible属性，这一步需要用到menu_extra_info表
     * 重新设置所有menu的visible属性
     *
     * @return
     */
    public boolean resetMenusVisibility(ActivationResponse content) {
        log.info("resetMenusVisibility...");
        if (Objects.isNull(content)) {
            return false;
        }
        List<TargetModel> allTargets = targetService.listAllIgnoreVisible(AuthConstant.DEFAULT_TENANT, TargetModel.TYPE_MENU);
        List<MenuExtraInfoModel> menuExtraInfos = menuExtraInfoService.listAll();

        processMenuVisibility(allTargets, menuExtraInfos, content);

        boolean success = reImportSupportService.rebuildAllMenus(allTargets);
        return success;
    }

    /**
     * 转换 ActivMenuDTO -> TargetActivDto
     *
     * @param menuDTO
     * @param parentId
     * @return
     */
    private TargetModel activMenuDto2TargetActivDto(ActivMenuDTO menuDTO, String parentId) {
        TargetModel target = new TargetModel();
        target.setId(menuDTO.getId());
        target.setType(TargetModel.TYPE_MENU);
        target.setName(menuDTO.getName());
        target.setIdentifier(menuDTO.getValue());
        target.setPath(menuDTO.getPath_name());
        target.setParent(parentId);
        return target;
    }

    private MenuExtraInfoModel activMenuDto2MenuExtraInfo(ActivMenuDTO menuDTO, String parentId) {
        MenuExtraInfoModel menuExtraInfo = new MenuExtraInfoModel();
        menuExtraInfo.setMenu(menuDTO.getId());

        if (CollectionUtil.isNotEmpty(menuDTO.getActive())) {
            menuExtraInfo.setActivKeys(menuDTO.getActive().stream().collect(Collectors.joining(",")));
        }

        if (CollectionUtil.isNotEmpty(menuDTO.getEdition())) {
            menuExtraInfo.setEditions(menuDTO.getEdition().stream().collect(Collectors.joining(",")));
        }

        return menuExtraInfo;
    }


    private void recursion(List<TargetModel> targets, List<MenuExtraInfoModel> menuExtraInfos, ActivMenuDTO menu, String parentId) {
        List<ActivMenuDTO> children = menu.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            for (ActivMenuDTO child : children) {
                child.setId(child.getAuthority());
                recursion(targets, menuExtraInfos, child, menu.getId());
            }
        }
        TargetModel target = activMenuDto2TargetActivDto(menu, parentId);
        MenuExtraInfoModel menuExtraInfo = activMenuDto2MenuExtraInfo(menu, parentId);
        targets.add(target);
        menuExtraInfos.add(menuExtraInfo);
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
     * 根据系统激活状态更改menu visible属性值
     * <p>
     * 逻辑：
     * 如果一个menu edition[]跟激活状态的edition对应得上，并且active[]内有激活项中value为1的key，或者no_auth的，则最终的visible=1
     */
    private void processMenuVisibility(List<TargetModel> targetModels, List<MenuExtraInfoModel> menuExtraInfos, ActivationResponse activationResponse) {
        log.info("targetModels:{}", targetModels);
        log.info("menuExtraInfos:{}", menuExtraInfos);
        log.info("activationResponse:{}", activationResponse);
        List<ActivationItem> items = activationResponse.getItems();

        String edition = String.valueOf(activationResponse.getEdition());
        List<String> positiveKeys = new ArrayList<>();
        positiveKeys.add("no_auth");
        items.stream().forEach(item -> {
            if (Objects.nonNull(item.getType()) && Objects.equals(item.getType(), 1)) {
                if (Objects.nonNull(item.getValue()) && Objects.equals("1", item.getValue())) {
                    positiveKeys.add(item.getKey());
                }
            }
        });

        log.info("positiveKeys:{}", positiveKeys);

        menuExtraInfos.forEach(mi -> {

            String menuId = mi.getMenu();
            Optional<TargetModel> targetModelOptional = targetModels.stream().filter(t -> Objects.equals(t.getId(), menuId)).findFirst();

            if (!targetModelOptional.isPresent()) {
                // 不存在的直接返回
                return;
            }

            boolean editionMatch = false;
            boolean itemMatch = false;

            // 版本是否符合
            if (StringUtils.isNotEmpty(mi.getEditions())) {
                List<String> editions = Arrays.asList(mi.getEditions().split(","));
                editionMatch = editions.contains(edition);

                if (editionMatch) {
                    log.info("edition matched!");
                }
            }

            if (StringUtils.isNotEmpty(mi.getActivKeys())) {
                List<String> activKeys = Arrays.asList(mi.getActivKeys().split(","));

                for (String activKey : activKeys) {
                    // 包含no_auth的菜单
                    if (Objects.equals(activKey.trim(), TargetActivDto.NO_AUTH)) {
                        itemMatch = true;
                    }
                }

                if (!itemMatch) {
                    // 包含激活项中value为1的
                    for (String activKey : activKeys) {
                        for (String positiveKey : positiveKeys) {
                            if (Objects.equals(activKey.trim(), positiveKey.trim())) {
                                itemMatch = true;
                            }
                        }
                    }
                }

                if (itemMatch) {
                    log.info("item matched!");
                }
            }

            log.info("final result... editionMatch:{}, itemMatch:{}, menuId:{}", editionMatch, itemMatch, mi.getMenu());
            TargetModel targetModel = targetModelOptional.get();

            // 版本匹配，激活项匹配，则最终的visible=1
            if (editionMatch && itemMatch) {
                log.info("edition and item matched! menuId:{}", targetModel.getId());
                targetModel.setVisible(TargetModel.VISIBILITY_YES);
            } else {
                targetModel.setVisible(TargetModel.VISIBILITY_NO);
            }
        });

    }

}
