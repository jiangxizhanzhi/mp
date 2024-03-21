package cn.cyberpeace.mp.frontadmin.service.sys.activmenu;

import cn.cyberpeace.mp.auth.bo.target.TargetActivBo;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.MenuTemplateModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.repository.MenuTemplateMapper;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
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
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
    @Qualifier("targetServiceImpl")
    private TargetService targetService;

    @Autowired
    private MenuExtraInfoService menuExtraInfoService;

    @Autowired
    private UserCacheService userCacheService;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @Autowired
    private MenuTemplateMapper menuTemplateMapper;


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
        List<MenuTemplateModel> menuTemplateModels = new ArrayList<>();

        //英文字段如果有就用原来有的，如果没有暂时置为“”
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(MenuTemplateModel::getTemplateId, "1");
        List<MenuTemplateModel> menuTemplateModelsOld = menuTemplateMapper.list(humanCondition);
        Map<String, String> enNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(menuTemplateModelsOld)) {
            for (MenuTemplateModel temp : menuTemplateModelsOld) {
                enNameMap.put(temp.getMenuId(), temp.getEnName());
            }
        }
        for (ActivMenuDTO menu : menus) {
            // 以authority内的值为id
            menu.setId(menu.getAuthority());
            // 一开始parentid为null
            recursion(targetModels, menuTemplateModels, menuExtraInfoModels, menu, "", enNameMap);
        }

        check(targetModels);

        // 根据系统激活与否，激活项目，版本，决定visible属性
        ActivationResponse content = activationService.status();
        // ActivationResponse content = mockActivationContent();
        if (Objects.nonNull(content)) {
            // 系统已激活，暂时忽略激活状态过期的场景
            processMenuVisibility(targetModels, menuExtraInfoModels, content);
        }

        boolean success = reImportSupportService.rebuildAllMenusAndExtraInfos(targetModels, menuTemplateModels, menuExtraInfoModels);
        evictCache();
        return success;
    }


    private void check(List<TargetModel> targetModels) {
        List<TargetModel> notEqualList = new ArrayList<>();
        for (TargetModel targetModel : targetModels) {
            if (!Objects.equals(targetModel.getId(), targetModel.getIdentifier())) {
                notEqualList.add(targetModel);
            }
        }
        if (CollectionUtils.isNotEmpty(notEqualList)) {
            log.error("json file error! value not equals with authority:{}", notEqualList.get(0).getId());
            throw new MpRuntimeException("json file error! value not equals with authority:" + notEqualList.get(0).getId());
        }

        // 检查是否有重复
        Map<String, Long> map = new HashMap<>();
        for (TargetModel t : targetModels) {
            map.merge(t.getId(), 1L, Long::sum);
        }
        Map.Entry<String, Long> entry = null;
        for (Map.Entry<String, Long> e : map.entrySet()) {
            if (e.getValue() > 1) {
                entry = e;
                break;
            }
        }
        if (Objects.nonNull(entry)) {
            log.error("json file has duplicated:{}", entry.getKey());
            throw new MpRuntimeException("json file error! duplicated:" + entry.getKey());
        }
    }


    /**
     * 适用场景：系统中的总menu不变，需要根据重新激活的激活项，激活版本，重新设置所有菜单的visible属性，这一步需要用到menu_extra_info表
     * 重新设置所有menu的visible属性
     *
     * @return
     */
    public boolean resetMenusVisibility(ActivationResponse content) {
        if (Objects.isNull(content)) {
            return false;
        }
        List<TargetModel> allTargets = targetService.listAllIgnoreVisible(AuthConstant.DEFAULT_TENANT, TargetModel.TYPE_MENU);
        if (CollectionUtils.isEmpty(allTargets)) {
            log.error("targets is null! please check!");
            throw new MpRuntimeException("targets is null!");
        }
        List<MenuExtraInfoModel> menuExtraInfos = menuExtraInfoService.listAll();

        processMenuVisibility(allTargets, menuExtraInfos, content);

        boolean success = reImportSupportService.rebuildAllMenus(allTargets);
        evictCache();
        return success;
    }


    /**
     *
     * 重新设置所有option的visible属性
     *
     * @return
     */
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


    /**
     * 重置Option的meta属性，添加修改visible属性
     * @param activeItemMap
     */
    public void doResetOptionsVisibility(Map<String, String> activeItemMap) {
        if (MapUtils.isEmpty(activeItemMap)) {
            return;
        }

        List<String> activedKeys = new ArrayList<>();
        for (Map.Entry<String, String> entry : activeItemMap.entrySet()) {
            if (Objects.equals(entry.getValue(), "1")) {
                String stringStringEntryKey = entry.getKey();
                activedKeys.add(stringStringEntryKey);
            }
        }
        OptionQueryParams queryParams = new OptionQueryParams();
        ListObjServiceResult<OptionModel> optionModelResult = optionService.list(queryParams);
        List<OptionModel> optionModels = optionModelResult.getSource().getObjs();

        if (CollectionUtils.isEmpty(optionModels)) {
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
            boolean isNoAuth = false;
            for (String s : metaActiveKeyList) {
                if (Objects.equals(s, CommonConstant.OPTION_ACTIVE_KEY_NO_AUTH)) {
                    isNoAuth = true;
                    break;
                }
            }
            if (isNoAuth) {
                metaModel.setVisible(OptionMetaModel.OPTION_VISIBLE_YES);
            } else {
                if (CollectionUtils.isEmpty(activedKeys)) {
                    metaModel.setVisible(OptionMetaModel.OPTION_VISIBLE_NO);
                } else {
                    boolean isVisible = false;
                    for (String key : metaActiveKeyList) {
                        if (activedKeys.contains(key)) {
                            isVisible = true;
                            break;
                        }
                    }
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

        if (CollectionUtils.isNotEmpty(toUpdateList)) {
            optionService.bulkUpdate(toUpdateList);
        }
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

    private MenuTemplateModel activMenuDto2TemplateModel(ActivMenuDTO menuDTO, String parentId, Map<String, String> enNameMap) {
        MenuTemplateModel menuTemplateModel = new MenuTemplateModel();
        menuTemplateModel.setTemplateId("1");
        menuTemplateModel.setMenuId(menuDTO.getId());
        menuTemplateModel.setParent(parentId);
        menuTemplateModel.setIdentifier(menuDTO.getValue());
        menuTemplateModel.setType(TargetModel.TYPE_MENU);
        menuTemplateModel.setName(menuDTO.getName());
        menuTemplateModel.setEnName(enNameMap.get(menuDTO.getId()) == null ? "" : enNameMap.get(menuDTO.getId()));
        menuTemplateModel.setPath(menuDTO.getPath_name());
        return menuTemplateModel;
    }

    private MenuExtraInfoModel activMenuDto2MenuExtraInfo(ActivMenuDTO menuDTO, String parentId) {
        MenuExtraInfoModel menuExtraInfo = new MenuExtraInfoModel();
        menuExtraInfo.setMenu(menuDTO.getId());

        if (CollectionUtils.isNotEmpty(menuDTO.getActive())) {
            StringJoiner joiner = new StringJoiner(",");
            for (String s : menuDTO.getActive()) {
                joiner.add(s);
            }
            menuExtraInfo.setActivKeys(joiner.toString());
        }

        if (CollectionUtils.isNotEmpty(menuDTO.getEdition())) {
            StringJoiner joiner = new StringJoiner(",");
            for (String s : menuDTO.getEdition()) {
                joiner.add(s);
            }
            menuExtraInfo.setEditions(joiner.toString());
        }

        return menuExtraInfo;
    }




    private void recursion(List<TargetModel> targets, List<MenuTemplateModel> menuTemplateModels, List<MenuExtraInfoModel> menuExtraInfos, ActivMenuDTO menu, String parentId, Map<String, String> enNameMap) {
        List<ActivMenuDTO> children = menu.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            for (ActivMenuDTO child : children) {
                child.setId(child.getAuthority());
                recursion(targets, menuTemplateModels, menuExtraInfos, child, menu.getId(), enNameMap);
            }
        }
        TargetModel target = activMenuDto2TargetActivDto(menu, parentId);
        MenuTemplateModel menuTemplateModel = activMenuDto2TemplateModel(menu, parentId, enNameMap);
        MenuExtraInfoModel menuExtraInfo = activMenuDto2MenuExtraInfo(menu, parentId);
        targets.add(target);
        menuTemplateModels.add(menuTemplateModel);
        menuExtraInfos.add(menuExtraInfo);
    }


    private String readJsonStrFromPath(String filePath) {
//        filePath = "/Users/sternberg/work/saining/all_permissions_json_activ.json";
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
                new ArrayList<>();
        for (TargetActivBo a : activDtos) {
            TargetModel targetModel = getTargetModel(a);
            targetModels.add(targetModel);
        }

        return targetModels;
    }

    private static TargetModel getTargetModel(TargetActivBo a) {
        TargetModel model = new TargetModel();
        BeanUtils.copyProperties(a, model);
        return model;
    }

    /**
     * 根据系统激活状态更改menu visible属性值
     * <p>
     * 逻辑：
     * 如果一个menu edition[]跟激活状态的edition对应得上，并且active[]内有激活项中value为1的key，或者no_auth的，则最终的visible=1
     */
    private void processMenuVisibility(List<TargetModel> targetModels, List<MenuExtraInfoModel> menuExtraInfos, ActivationResponse activationResponse) {
        List<ActivationItem> items = activationResponse.getItems();

        String edition = String.valueOf(activationResponse.getEdition());
        List<String> positiveKeys = new ArrayList<>();
        positiveKeys.add(AdminConstant.MENU_NO_AUTH);
        for (ActivationItem item : items) {
            if (Objects.nonNull(item.getType()) && Objects.equals(item.getType(), 1)) {
                if (Objects.nonNull(item.getValue()) && Objects.equals("1", item.getValue())) {
                    positiveKeys.add(item.getKey());
                }
            }
        }

        log.info("positiveKeys:{}", positiveKeys);

        for (MenuExtraInfoModel mi : menuExtraInfos) {
            String menuId = mi.getMenu();
            Optional<TargetModel> targetModelOptional = Optional.empty();
            for (TargetModel t : targetModels) {
                if (Objects.equals(t.getId(), menuId)) {
                    targetModelOptional = Optional.of(t);
                    break;
                }
            }

            if (!targetModelOptional.isPresent()) {
                // 不存在的直接返回
                continue;
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
                    if (Objects.equals(activKey.trim(), TargetActivBo.NO_AUTH)) {
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
        }

    }


    private void evictCache() {
        userCacheService.evictAllUserCache();
        userCacheService.evictAllTargetsCache();
    }

}
