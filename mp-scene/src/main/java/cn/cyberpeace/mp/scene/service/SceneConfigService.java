package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.ThirdPartyElementBO;
import cn.cyberpeace.mp.scene.core.bo.ThirdPartyElementConnectBO;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.core.sceneconfig.ThirdPartyTopoSceneConfigHandler;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.swing.plaf.multi.MultiPanelUI;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SceneConfigService {

    @Resource
    private SceneElementSnapshotMapper snapshotMapper ;

    public ServiceResult<SceneConfigDataBO> get(String id, boolean isAreaMode, String areaId,
                                                SceneConfigContextBO context) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(id, areaId, isAreaMode, context);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneConfigHandler.toData());
    }

    public ServiceResult<SceneConfigDataBO> sync(SceneConfigDataBO sceneConfigDataBO, boolean isAreaMode,
                                                 SceneConfigContextBO context) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(sceneConfigDataBO, isAreaMode, context);
            sceneConfigHandler.validate();
            if (sceneConfigHandler.getScene() != null) {
                sceneConfigHandler.getScene().setCreateTime(null);
            }
            sceneConfigHandler.save();
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneConfigHandler.toData());
    }

    public ServiceResult<Boolean> delete(String id, boolean logic, SceneConfigContextBO context) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(id, null, false, context);
            if (logic) {
                sceneConfigHandler.logicDelete();
            } else {
                sceneConfigHandler.delete();
            }
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(false, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDelete(String[] ids, boolean logic, SceneConfigContextBO context) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(delete(id, logic, context).getSource())) {
                result += 1;
            }
        }

        if (result < ids.length) {
            log.error("Service bulk delete scene configs {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    public SceneConfigDataBO importThirdPartyScene(MultipartFile file) {
        try {
            String fileName = StringUtils.substringBefore(file.getOriginalFilename(), ".");
            ThirdPartyElementReader thirdPartyElementReader = new ThirdPartyElementReader();
            EasyExcel.read(file.getInputStream(), ThirdPartyElementBO.class, thirdPartyElementReader)
                    .autoCloseStream(true).sheet(0).headRowNumber(2);
            List<ThirdPartyElementBO> thirdPartyElementBOS = thirdPartyElementReader.getCachedElements();

            ThirdPartyElementConnectReader thirdPartyElementConnectReader = new ThirdPartyElementConnectReader();
            EasyExcel.read(file.getInputStream(), ThirdPartyElementConnectBO.class, thirdPartyElementConnectReader)
                    .autoCloseStream(true).sheet(1).headRowNumber(1);
            List<ThirdPartyElementConnectBO> thirdPartyElementConnectBOS = thirdPartyElementConnectReader.getCachedConnects();

            ThirdPartyTopoSceneConfigHandler thirdPartyTopoSceneConfigHandler = new ThirdPartyTopoSceneConfigHandler(
                    fileName, thirdPartyElementBOS, thirdPartyElementConnectBOS);
            return thirdPartyTopoSceneConfigHandler.save();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("读取导入文件过程异常，请确认文件格式无误再尝试导入"));
        }
    }

}
