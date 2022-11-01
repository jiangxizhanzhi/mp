package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowQueryParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FlowService extends BaseModelServiceImpl<FlowModel, FlowMapper, FlowFields, FlowQueryParams> {

    public static final String PRE_PATH = "/root/.mp/resources/flows/protocol/";


    @Autowired
    public FlowService(FlowMapper modelMapper, FlowFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public void loadListFilterCondition(HumanCondition humanCondition, FlowQueryParams queryParams) {
        var categories = queryParams.getCategories();
        if (categories != null && !categories.isEmpty()) {
            humanCondition.filterIn(FlowModel::getCategory, categories.toArray());
        }

        var resourceTypes = queryParams.getResourceTypes();
        if (resourceTypes != null && !resourceTypes.isEmpty()) {
            humanCondition.filterIn(FlowModel::getResourceType, resourceTypes.toArray());
        }

        Integer interactiveType = queryParams.getInteractiveType();
        if (Objects.nonNull(interactiveType)) {
            humanCondition.filterEqual(FlowModel::getInteractiveType, interactiveType);
        }
    }


    /**
     * pcap协议解析
     * @param multipartFile
     * @return
     */
    public List<String> pcapAnalyse(MultipartFile multipartFile) {
        List<String> protocolList = new ArrayList<>();
        String strProtocols;
        String scriptPath = PRE_PATH.concat("protocol_analyse.py");
        String pcapPath = "";
        OutputStream out = null;
        BufferedReader input = null;
        File tmpFile = null;

        try {
            String fileName = System.currentTimeMillis() + BaseConstants.SHORT_DASH + multipartFile.getOriginalFilename();
            pcapPath = PRE_PATH.concat(fileName);
            tmpFile = new File(pcapPath);
            out = new FileOutputStream(tmpFile);
            byte[] ss = multipartFile.getBytes();
            for (byte s : ss) {
                out.write(s);
            }

            String pythonCmd = String.format("python %s %s", scriptPath, pcapPath);
            log.info("============python analyse cmd: {}", pythonCmd);

            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor();
            strProtocols = "";
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                strProtocols = line;
            }
            log.info("============pcapRead result: {}", strProtocols);

            if (StringUtils.isNotBlank(strProtocols)) {
                String[] protocolArray = strProtocols.split(BaseConstants.COMMA);
                protocolList = Arrays.asList(protocolArray);
            }
        } catch (Exception e) {
            log.error("exec error:", e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(input);

            if (Objects.nonNull(tmpFile) && tmpFile.exists()) {
                try {
                    tmpFile.delete();
                } catch (Exception e) {
                    log.error(pcapPath + "delete error");
                }
            }
        }

        return protocolList;
    }

    /**
     * 根据协议结合过滤pcap包
     * @param pcapPath 原pcap的fdfs地址
     * @param protocolList 保留的协议集合
     * @return 过滤后的pcap包fdfs地址
     */
    public String pcapFilter(String pcapPath, List<String> protocolList) {
        String outPcapPath = "";
        String scriptPath = PRE_PATH.concat("protocol_filter.py");
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File srcFile = null;
        File dstFile = null;
        String strProtocols = StringUtils.join(protocolList, BaseConstants.COMMA);

        try {
            // 下载原pcap文件
            byte[] pcapByte = FdfsCommon.downloadRawContent(pcapPath);
            String fileName = FilenameUtils.getName(pcapPath);;
            String srcPcapPath = PRE_PATH.concat(fileName);
            srcFile = new File(srcPcapPath);
            fos = new FileOutputStream(srcFile);
            bos = new BufferedOutputStream(fos);
            bos.write(pcapByte);

            // 根据协议过滤pcap生成新的包
            String dstPcapPath = "dst_".concat(fileName);
            String pythonCmd = String.format("python %s %s %s %s", scriptPath, srcPcapPath, dstPcapPath, strProtocols);
            log.info("============python filter cmd: {}", pythonCmd);
            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor();
            dstFile = new File(dstPcapPath);
            // 上传新pcap包
            outPcapPath = FdfsCommon.upload(dstPcapPath);
        } catch (Exception e) {
            log.error("pcap file filter error", e);
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(bos);

            if (Objects.nonNull(srcFile) && srcFile.exists()) {
                try {
                    srcFile.delete();
                } catch (Exception e) {
                    log.error("pcap src file delete error");
                }
            }

            if (Objects.nonNull(dstFile) && dstFile.exists()) {
                try {
                    dstFile.delete();
                } catch (Exception e) {
                    log.error("pcap dst file delete error");
                }
            }
        }

        return outPcapPath;
    }


}
