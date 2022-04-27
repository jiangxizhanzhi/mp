package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/probes")
@Api(value = "探针管理", tags = "探针管理")
public class ProbeManagerController {

    @Autowired
    private ProbeService probeService;


    @GetMapping("/page")
    @ApiOperation(value = "分页查询探针，不含探针脚本内容")
    public ListObjServiceResult<ProbeDto> page(ProbePageQueryRequest pageQueryRequest) {
        PageInfo<ProbeDto> pageInfo = probeService.page(pageQueryRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @GetMapping
    @ApiOperation(value = "查询探针详情，包含探针脚本内容")
    public ServiceResult<List<ProbeDto>> getByIds(@RequestParam String[] ids) {

        List<ProbeDto> probeDtos = probeService.listByIds(Arrays.asList(ids));
        return ServiceResult.success(probeDtos);
    }

    /**
     * 新增探针
     *
     * @param probeRequest 新增探针参数
     * @return ServiceResult<Void>
     */
    @PostMapping
    @ApiOperation(value = "新增自定义探针")
    public ServiceResult addProbe(@RequestBody @Validated(AddGroup.class) ProbeRequest probeRequest) {
        if (StringUtils.isBlank(probeRequest.getScriptUrl())) {
            String httpUrl = probeService.uploadScriptContentToFDFS(probeRequest.getScriptContent());
            probeRequest.setScriptUrl(httpUrl);
        }
        probeService.addProbe(probeRequest);
        return ServiceResult.successful();
    }


    @PatchMapping
    @ApiOperation(value = "修改自定义探针")
    public ServiceResult modifyProbe(@RequestBody @Validated(UpdateGroup.class) ProbeRequest probeRequest) {
        ProbeDto probeDto = probeService.getById(probeRequest.getId());
        // 判断脚本内容是否有变动
        if (!Objects.equals(probeDto.getScriptContent(), probeRequest.getScriptContent())) {
            if (StringUtils.isBlank(probeRequest.getScriptUrl())) {
                String httpUrl = probeService.uploadScriptContentToFDFS(probeRequest.getScriptContent());
                probeRequest.setScriptUrl(httpUrl);
            }
        } else {
            probeRequest.setScriptContent(null);
            probeDto.setScriptUrl(null);
        }

        probeService.modifyProbe(probeRequest);
        return ServiceResult.successful();
    }


    @DeleteMapping()
    @ApiOperation(value = "删除探针")
    public ServiceResult deleteByIds(@RequestParam String[] ids) {

        int cnt = probeService.deleteByIds(Arrays.asList(ids));
        return ServiceResult.success(cnt);
    }

    @ApiOperation(value = "导入脚本文件，返回脚本内容", notes = "")
    @PostMapping("/script/import")
    @ResponseBody
    public ServiceResult<String> upload(MultipartFile file) {
        String content = probeService.readScriptFileContent(file);
        return ServiceResult.success(content);
    }
}
