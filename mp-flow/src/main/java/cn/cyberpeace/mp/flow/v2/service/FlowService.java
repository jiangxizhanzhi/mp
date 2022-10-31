package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowFields;
import cn.cyberpeace.mp.flow.v2.service.param.FlowQueryParams;
import lombok.extern.slf4j.Slf4j;
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
        String strProtocols = null;
        String scriptPath = "";
        String pcapPath = "";
        OutputStream out = null;
        BufferedReader input = null;
        File tmp_file = null;

        try {
            if (StringUtils.isBlank(scriptPath)) {
                scriptPath  = PRE_PATH.concat("protocol_analyse.py");
            }

            String fileName = System.currentTimeMillis() + multipartFile.getOriginalFilename();
            pcapPath = PRE_PATH.concat(fileName);
            tmp_file = new File(pcapPath);
            out = new FileOutputStream(tmp_file);
            byte[] ss = multipartFile.getBytes();
            for (byte s : ss) {
                out.write(s);
            }

            Process process = Runtime.getRuntime().exec(String.format("python %s %s", scriptPath, pcapPath));
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

            if (Objects.nonNull(tmp_file) && tmp_file.exists()) {
                try {
                    tmp_file.delete();
                } catch (Exception e) {
                    log.error(pcapPath + "delete error");
                }
            }
        }

        return protocolList;
    }

}
