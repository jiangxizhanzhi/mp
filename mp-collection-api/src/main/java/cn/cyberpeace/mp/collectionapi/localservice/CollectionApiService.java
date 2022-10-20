package cn.cyberpeace.mp.collectionapi.localservice;


import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.collectionapi.config.ContestConfig;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;

@Component
@LocalCallServer
@Slf4j
public class CollectionApiService {
    /**
     * 手动触发更新战队信息
     * @param filePath 包含战队配置的json文件路径
     */
    @LocalCallFunc
    public void updateTeams(String filePath) {
        try {
            File file = ResourceUtils.getFile(filePath);
            String content = FileUtil.readString(file, "utf8");
            ContestConfig.teams = JSON.parseArray(content, ContestConfig.Team.class);
        } catch (Exception e) {
            log.error("update teams error:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 手动触发更新设备信息
     * @param filePath 包含设备配置的json文件路径
     */
    @LocalCallFunc
    public void updateEquipments(String filePath) {
        try {
            File file = ResourceUtils.getFile(filePath);
            String content = FileUtil.readString(file, "utf8");
            ContestConfig.equipments = JSON.parseArray(content, ContestConfig.Equipment.class);
        } catch (Exception e) {
            log.error("update equipments error:{}", ExceptionUtils.getStackTrace(e));
        }
    }
}
