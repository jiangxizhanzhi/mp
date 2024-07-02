package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.protocolcollection.MibFileInfo;
import cn.cyberpeace.mp.cscene.model.CsceneElementSoftVersionModel;
import cn.cyberpeace.mp.cscene.repository.CsceneElementSoftVersionMapper;
import cn.cyberpeace.mp.cscene.service.CsceneFileService;
import cn.cyberpeace.mp.cscene.service.ProtocolCollectionService;
import cn.cyberpeace.mp.cscene.vo.MibFileUploadResponse;
import cn.cyberpeace.mp.cscene.vo.SceneFileUploadResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName CsceneFileController
 * @Description
 * @Author maojun
 * @Date 2022/5/16
 * @Version 1.0.0
 */

@Api(value = "场景文件", tags = "CSCENE-场景文件")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/file")
@Data
@AuthTarget(TargetEnum.CSCENE_TARGET_GROUP)
public class CsceneFileController {

    @Autowired
    private CsceneFileService csceneFileService;

    @Autowired
    private ProtocolCollectionService protocolCollectionService;

    /**
     * attachmentUpload
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传场景中的附件")
    @ApiImplicitParam(name = "file", value = "附件", dataTypeClass = MultipartFile.class)
    @PostMapping("/attachment_upload")
    public ServiceResult<SceneFileUploadResponse> attachmentUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalType = new String[]{ "zip" };
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalType).asResult();
    }

    /**
     * 脚本文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传场景中的执行脚本")
    @ApiImplicitParam(name = "file", value = "脚本文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/script_upload")
    public ServiceResult<SceneFileUploadResponse> scriptUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalType = new String[]{ "sh" };
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalType).asResult();
    }

    /**
     * 上传场景中的图标
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传场景中的图标")
    @ApiImplicitParam(name = "file", value = "图标文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/icon_upload")
    public ServiceResult<SceneFileUploadResponse> iconUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "png", "svg", "jpg", "jpeg", "gif" };
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }

    /**
     * 上传节点秘钥
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传节点秘钥")
    @ApiImplicitParam(name = "file", value = "节点秘钥", dataTypeClass = MultipartFile.class)
    @PostMapping("/private_key_upload")
    public ServiceResult<SceneFileUploadResponse> privateKeyUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "pub", "txt" };
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }

    /**
     * 上传场景模板截图
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation("上传场景模板截图")
    @ApiImplicitParam(name = "file", value = "场景模板截图", dataTypeClass = MultipartFile.class)
    @PostMapping("/scene_template_screenshot_upload")
    public ServiceResult<SceneFileUploadResponse> sceneTemplateScreenshotUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "png" };
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        return csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix).asResult();
    }


    /**
     * sceneMibFileUpload
     *
     * @param file
     * @return
     */
    @ApiOperation("上传mib文件")
    @ApiImplicitParam(name = "file", value = "mib文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/mib_import")
    public ServiceResult<MibFileUploadResponse> sceneMibFileUpload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String[] legalSuffix = new String[]{ "mib" };
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        //上传文件到中间件
        ServiceResult<SceneFileUploadResponse> sceneMibFileUpload =
            csceneFileService.sceneUpload(inputStream, fileName, fileSuffix, legalSuffix);
        SceneFileUploadResponse sceneFileUploadResponse = sceneMibFileUpload.getSource();
        //上传失败响应
        if(Objects.isNull(sceneFileUploadResponse)){
            return new ServiceResult<>(null, sceneMibFileUpload.getServiceCode(), sceneMibFileUpload.getDetail());
        }
        //上传解析mib文件
        ServiceResult<List<MibFileInfo>> mibFile = protocolCollectionService.importMibFile(file);
        List<MibFileInfo> mibFileInfos = mibFile.getSource();
        //失败响应
        if(Objects.isNull(mibFileInfos)){
            return new ServiceResult<>(null, mibFile.getServiceCode(), mibFile.getDetail());
        }
        //成功响应结果
        MibFileUploadResponse mibFileUploadResponse =
            new MibFileUploadResponse(sceneFileUploadResponse, mibFileInfos);
        return new ServiceResult<>(mibFileUploadResponse).asResult();
    }


    @ApiOperation("上传虚拟机内的软件版本")
    @ApiImplicitParam(name = "file", value = "mib文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/upload_versions")
    public void uploadElementVersion(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "eid") String eid) {
        String fileName = eid;
        File x = new File(fileName);
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(eid));
            var list = FileUtils.readLines(x);
            List<CsceneElementSoftVersionModel> models = new ArrayList<>();
            for (String l : list) {
                String[] s = l.split(" ");
                String soft = s[0].split("/")[0];
                String version = s[1];
                CsceneElementSoftVersionModel model = new CsceneElementSoftVersionModel();
                model.setVersion(version);
                model.setSoft(soft);
                model.setEid(eid);
                models.add(model);
            }
            var mapper = BeanCommon.getBean(CsceneElementSoftVersionMapper.class);
            mapper.filterDelete(new HumanCondition().filterEqual(CsceneElementSoftVersionModel::getEid, eid));
            mapper.bulkCreate(models);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                x.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}
