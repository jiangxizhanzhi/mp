package cn.cyberpeace.mp.common.service.impl;

import cn.cyberpeace.mp.base.bo.MpSysCodeEnum;
import cn.cyberpeace.mp.common.repository.CommonCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
            if (parentCode.equals(menu.getParentCode())) {
                topCode = menu;
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
                    menuDto.setChildren(new ArrayList<MpSysCodeEnum>());
                }
                MpSysCodeEnum dto = new MpSysCodeEnum();
                BeanUtils.copyProperties(menu, dto);
                //递归
                menuDto.getChildren().add(findChildren(dto, list));
            }
        }
        return menuDto;
    }
}

