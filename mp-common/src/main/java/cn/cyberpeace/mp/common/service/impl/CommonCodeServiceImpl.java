package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.bo.MpSysCodeEnum;
import cn.cyberpeace.mp.common.repository.CommonCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl {

    @Autowired
    public CommonCodeMapper commonCodeMapper;

    public List<MpSysCodeEnum> getNextLevelDataByCode(String code) {
        return commonCodeMapper.getNextLevelDataByCode(code);
    }

    public List<MpSysCodeEnum> getNextAllDataByCode(String code) {
        return commonCodeMapper.getNextAllDataByCode(code);
    }

    public List<MpSysCodeEnum> getNextAllTreeDataByCode(String code, Boolean includeSelf) {
        List<MpSysCodeEnum> nextAllDataByCode = getNextAllDataByCode(code);
        //根据parentCode字段将data数据按照父子关系处理返回新的List<MpSysCodeEnum>数据
        List<MpSysCodeEnum> dtoTreeList = getDtoTreeList(nextAllDataByCode, code);
        if(dtoTreeList != null && dtoTreeList.size()!=0 && includeSelf != null && !includeSelf){
            return dtoTreeList.get(0).getChildren();
        }
        return dtoTreeList;
    }

    /**
     * 将普通的entity的集合转成存在树状结构的集合
     *
     * @param list
     * @return
     */
    public static List<MpSysCodeEnum> getDtoTreeList(List<MpSysCodeEnum> list, String parentCode) {
        List<MpSysCodeEnum> dtos = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return dtos;
        }
        List<MpSysCodeEnum> topCodeList = new ArrayList<>();
        MpSysCodeEnum topCode = new MpSysCodeEnum();
        for (MpSysCodeEnum menu : list) {
            if (parentCode.equals(menu.getCode())) {
                //最上级
                topCode = menu;
            }
            if (parentCode.equals(menu.getParentCode())) {
                //找到顶层目录
                MpSysCodeEnum menuDto = new MpSysCodeEnum();
                //将实体类的数据转成dto
                BeanUtils.copyProperties(menu, menuDto);
                //获取该实体类下的所有子级
                dtos.add(findChildren(menuDto, list));
            }
        }
        topCode.setChildren(dtos);
        topCodeList.add(topCode);
        return topCodeList;
    }

    /**
     * 获取dto类下面的所有子级,存到dto中的children中
     *
     * @param menuDto
     * @param list
     * @return
     */
    private static MpSysCodeEnum findChildren(MpSysCodeEnum menuDto, List<MpSysCodeEnum> list) {
        for (MpSysCodeEnum menu : list) {
            if (menuDto.getCode().equals(menu.getParentCode())) {
                //此时menu是menuDto下的子级
                if (menuDto.getChildren() == null) {
                    menuDto.setChildren(new ArrayList<>());
                }
                MpSysCodeEnum dto = new MpSysCodeEnum();
                BeanUtils.copyProperties(menu, dto);
                //递归
                menuDto.getChildren().add(findChildren(dto, list));
            }
        }
        return menuDto;
    }

    /**
     * 根据codes查找下级
     */
    public List<MpSysCodeEnum> getNextLevelDatasByCodes(String[] codes) {
        return commonCodeMapper.getNextLevelDatasByCodes(codes);
    }

    @Cacheable(key = "'nextLevelDatasByCodes' + #codes", value = "nextLevelDatasByCodes")
    public Map<String,String> convertNextLevelDatasByCodes(String[] codes) {
        List<MpSysCodeEnum> nextLevelDatasByCodes = getNextLevelDatasByCodes(codes);
        //将nextLevelDatasByCodes中的code和codeName转换成map
        Map<String, String> codeMap = nextLevelDatasByCodes.stream()
                .collect(Collectors.toMap(MpSysCodeEnum::getCode, MpSysCodeEnum::getCodeName));
        return codeMap;
    }



    @Cacheable(key = "'getNextLevelMapsByCodes' + #code", value = "getNextLevelMapsByCodes")
    public Map<String,String> getNextLevelMapsByCodes(String code) {
        Map<String,String> map = new HashMap<>();
        List<MpSysCodeEnum> nextLevelDatasByCodes = commonCodeMapper.getNextAllDataByCode(code);
        if(nextLevelDatasByCodes != null && nextLevelDatasByCodes.size() > 0){
            //map收集nextLevelDatasByCodes中每个成员中的code和codeName
            for (MpSysCodeEnum mpSysCodeEnum : nextLevelDatasByCodes) {
                if(mpSysCodeEnum.getCode()!=null && !mpSysCodeEnum.getCode().equals("")){
                    map.put(mpSysCodeEnum.getCode(),mpSysCodeEnum.getCodeName());
                }
            }
        }
        return map;
    }

    public List<MpSysCodeEnum> getNextLevelTreeDatasByCodes(String[] codes) {
        List<MpSysCodeEnum> nextLevelDatasByCodes = commonCodeMapper.getNextLevelDatasByCodes(codes);
        List<MpSysCodeEnum> localLevelDatasByCodes = commonCodeMapper.getNextLevelTreeDatasByCodes(codes);
        Map<String,List<MpSysCodeEnum>> childrenMap = new HashMap<>();
        //map收集nextLevelDatasByCodes中每个成员中的code和本身
        for (MpSysCodeEnum mpSysCodeEnum : nextLevelDatasByCodes) {
            if (mpSysCodeEnum.getParentCode() != null && !mpSysCodeEnum.getParentCode().isEmpty()) {
                childrenMap.computeIfAbsent(mpSysCodeEnum.getParentCode(), k -> new ArrayList<>()).add(mpSysCodeEnum);
            }
        }

        // 填充子节点
        for (MpSysCodeEnum mpSysCodeEnum : localLevelDatasByCodes) {
            if (mpSysCodeEnum.getCode() != null && !mpSysCodeEnum.getCode().isEmpty()) {
                mpSysCodeEnum.setChildren(childrenMap.getOrDefault(mpSysCodeEnum.getCode(), Collections.emptyList()));
            }
        }

        // 返回处理后的局部层级数据
        return localLevelDatasByCodes.stream()
                .filter(data -> data.getCode() != null && !data.getCode().isEmpty())
                .collect(Collectors.toList());
    }
}

