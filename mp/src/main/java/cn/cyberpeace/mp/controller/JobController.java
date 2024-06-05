package cn.cyberpeace.mp.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.job.schedule.CopyEsCollectionDataScheduleJob;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job/activate")
@Api(value = "定时任务", tags = "触发")
@AuthTarget(TargetEnum.COLLECTION_ANALYSE)
public class JobController {

    @Autowired
    private CopyEsCollectionDataScheduleJob copyEsCollectionDataScheduleJob;


    @ApiOperation(value = "es数据存储定时任务", notes = "启动")
    @GetMapping("/copyEsCollectionDataJob")
    public String copyEsCollectionData() {

        copyEsCollectionDataScheduleJob.execute();
        return "success";
    }
}
