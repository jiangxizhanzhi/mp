package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.bo.target.TargetActivBo;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.common.constant.CommonConstant;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.request.queryparam.OptionQueryParams;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.frontadmin.constant.AdminConstant;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivMenuDTO;
import cn.cyberpeace.mp.frontadmin.dto.activation.ActivationItem;
import cn.cyberpeace.mp.frontadmin.dto.option.OptionMetaModel;
import cn.cyberpeace.mp.frontadmin.model.MenuExtraInfoModel;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ActivMenuService {

    @Autowired
    private MenuReImportSupportService reImportSupportService;

    @Autowired
    private SysActivationService activationService;

    @Autowired
    @Qualifier("targetServiceImpl")
    private TargetService targetService;

    @Autowired
    private MenuExtraInfoService menuExtraInfoService;

    @Autowired
    private UserCacheService userCacheService;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;


    
    public boolean reImportMenus(MultipartFile jsonFile) {
        log.info("reImportMenus...");
        String s = readJsonStrFromMultipartFile(jsonFile);
        List<ActivMenuDTO> menus = JSON.parseArray(s, ActivMenuDTO.class);

        List<TargetModel> targetModels = new ArrayList<>();
        List<MenuExtraInfoModel> menuExtraInfoModels = new ArrayList<>();

        for (ActivMenuDTO menu : menus) {
            
            menu.setId(menu.getAuthority());
            
            recursion(targetModels, menuExtraInfoModels, menu, "");
        }

        check(targetModels);

        
        ActivationResponse content = activationService.status();
        
        if (Objects.nonNull(content)) {
            
            processMenuVisibility(targetModels, menuExtraInfoModels, content);
        }

        boolean success = reImportSupportService.rebuildAllMenusAndExtraInfos(targetModels, menuExtraInfoModels);
        evictCache();
        return success;
    }


    private void check(List<TargetModel> targetModels) {
        var notEqualList = targetModels.stream().filter(t -> !Objects.equals(t.getId(), t.getIdentifier())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(notEqualList)) {
            log.error("json file error! value not equals with authority:{}", notEqualList.get(0).getId());
            throw new MpRuntimeException("json file error! value not equals with authority:" + notEqualList.get(0).getId());
        }

        
        Map<String, Long> map = targetModels.stream().collect(Collectors.groupingBy(t -> t.getId(), Collectors.counting()));
        Map.Entry<String, Long> entry = map.entrySet().stream().filter(e -> e.getValue() > 1).findFirst().orElse(null);
        if (Objects.nonNull(entry)) {
            log.error("json file has duplicated:{}", entry.getKey());
            throw new MpRuntimeException("json file error! duplicated:" + entry.getKey());
        }
    }


    
    public boolean resetMenusVisibility(ActivationResponse content) {
        if (Objects.isNull(content)) {
            return false;
        }
        List<TargetModel> allTargets = targetService.listAllIgnoreVisible(AuthConstant.DEFAULT_TENANT, TargetModel.TYPE_MENU);
        if (CollectionUtil.isEmpty(allTargets)) {
            log.error("targets is null! please check!");
            throw new MpRuntimeException("targets is null!");
        }
        List<MenuExtraInfoModel> menuExtraInfos = menuExtraInfoService.listAll();

        processMenuVisibility(allTargets, menuExtraInfos, content);

        boolean success = reImportSupportService.rebuildAllMenus(allTargets);
        evictCache();
        return success;
    }


    
    public void resetOptionsVisibility(ActivationResponse content) {
        if (Objects.isNull(content)) {
            return;
        }

        Map<String, String> activeItemMap = new HashMap<>();
        for (ActivationItem item : content.getItems()) {
            activeItemMap.put(item.getKey(), item.getValue());
        }

        doResetOptionsVisibility(activeItemMap);
    }


    
    public void doResetOptionsVisibility(Map<String, String> activeItemMap) {
        if (CollectionUtil.isEmpty(activeItemMap)) {
            return;
        }

        List<String> activedKeys = activeItemMap.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), "1")).map(Map.Entry::getKey).collect(Collectors.toList());
        OptionQueryParams queryParams = new OptionQueryParams();
        ListObjServiceResult<OptionModel> optionModelResult = optionService.list(queryParams);
        List<OptionModel> optionModels = optionModelResult.getSource().getObjs();

        if (CollectionUtil.isEmpty(optionModels)) {
            log.error("optionModels is null! please check!");
            throw new MpRuntimeException("optionModels is null!");
        }

        List<OptionModel> toUpdateList = new ArrayList<>();
        LocalDateTime updatime = LocalDateTime.now();

        for (OptionModel optionModel : optionModels) {
            String metaStr = optionModel.getMeta();
            OptionMetaModel metaModel = JsonCommon.parse(metaStr, OptionMetaModel.class);
            if (StringUtils.isEmpty(metaModel.getActivKeys())) {
                continue;
            }
            String[] metaActiveKeyArr = metaModel.getActivKeys().split(",");
            List<String> metaActiveKeyList = Arrays.asList(metaActiveKeyArr);
            boolean isNoAuth = metaActiveKeyList.stream().filter(key -> Objects.equals(key, CommonConstant.OPTION_ACTIVE_KEY_NO_AUTH)).findAny().isPresent();
            if (isNoAuth) {
                metaModel.setVisible(OptionMetaModel.OPTION_VISIBLE_YES);
            } else {
                if (CollectionUtil.isEmpty(activedKeys)) {
                    metaModel.setVisible(OptionMetaModel.OPTION_VISIBLE_NO);
                } else {
                    boolean isVisible = metaActiveKeyList.stream().filter(key -> activedKeys.contains(key)).findAny().isPresent();
                    if (isVisible) {
                        metaModel.setVisible(OptionMetaModel.OPTION_VISIBLE_YES);
                    } else {
                        metaModel.setVisible(OptionMetaModel.OPTION_VISIBLE_NO);
                    }
                }
            }
            OptionModel toUpdateOptionModel = new OptionModel();
            toUpdateOptionModel.setId(optionModel.getId());
            String toUpdateMetaStr = JsonCommon.stringify(metaModel);
            toUpdateOptionModel.setMeta(toUpdateMetaStr);
            toUpdateOptionModel.setUpdateTime(updatime);
            toUpdateList.add(toUpdateOptionModel);
        }

        if (CollectionUtil.isNotEmpty(toUpdateList)) {
            optionService.bulkUpdate(toUpdateList);
        }
    }

    
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

        String jsonStr = "";
        try {
            File jsonFile = new File(filePath);
            StringBuffer sb;
            try (FileReader fileReader = new FileReader(jsonFile); Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");) {
                int ch = 0;
                sb = new StringBuffer();
                while ((ch = reader.read()) != -1) {
                    sb.append((char) ch);
                }
            }
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String readJsonStrFromMultipartFile(MultipartFile file) {
        String jsonStr = "";
        try (Reader reader = new InputStreamReader(file.getInputStream(), "utf-8")) {
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

    private List<TargetModel> targetActivDtos2TargetModels(List<TargetActivBo> activDtos) {
        List<TargetModel> targetModels =
                activDtos.stream().map(a -> {
                    TargetModel model = new TargetModel();
                    BeanUtils.copyProperties(a, model);
                    return model;
                }).collect(Collectors.toList());

        return targetModels;
    }

    
    private void processMenuVisibility(List<TargetModel> targetModels, List<MenuExtraInfoModel> menuExtraInfos, ActivationResponse activationResponse) {
        List<ActivationItem> items = activationResponse.getItems();

        String edition = String.valueOf(activationResponse.getEdition());
        List<String> positiveKeys = new ArrayList<>();
        positiveKeys.add(AdminConstant.MENU_NO_AUTH);
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
                
                return;
            }

            boolean editionMatch = false;
            boolean itemMatch = false;

            
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
                    
                    if (Objects.equals(activKey.trim(), TargetActivBo.NO_AUTH)) {
                        itemMatch = true;
                    }
                }

                if (!itemMatch) {
                    
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

            
            if (editionMatch && itemMatch) {
                log.info("edition and item matched! menuId:{}", targetModel.getId());
                targetModel.setVisible(TargetModel.VISIBILITY_YES);
            } else {
                targetModel.setVisible(TargetModel.VISIBILITY_NO);
            }
        });

    }


    private void evictCache() {
        userCacheService.evictAllUserCache();
        userCacheService.evictAllTargetsCache();
    }

}
