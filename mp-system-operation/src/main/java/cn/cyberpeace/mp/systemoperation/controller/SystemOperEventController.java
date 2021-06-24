package cn.cyberpeace.mp.systemoperation.controller;


import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.datasandtable.model.FileEventModel;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventRequest;
import cn.cyberpeace.mp.datasandtable.model.WazuhFileEventResponse;
import cn.cyberpeace.mp.datasandtable.service.WazuhService;
import cn.cyberpeace.mp.systemoperation.constant.ControllerConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/event")
public class SystemOperEventController {

    @Autowired
    private WazuhService wazuhService;

    /**
     * 分页查询文件事件
     * @param wazuhFileEventRequest
     * @return
     */
    @PostMapping("pageable")
    public ServiceResult<WazuhFileEventResponse> queryFileEvents(@RequestBody @Valid WazuhFileEventRequest wazuhFileEventRequest) {
        ListObjResult<FileEventModel> listObjResult = wazuhService.queryFileEvents(wazuhFileEventRequest);
        WazuhFileEventResponse response = WazuhFileEventResponse.builder()
                .fileEventModelList(listObjResult.getObjs())
                .total(listObjResult.getTotal())
                .build();

        return ServiceResult.success(response);
    }


}
