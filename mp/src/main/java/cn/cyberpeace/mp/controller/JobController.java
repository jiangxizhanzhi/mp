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

/**
 * 作业控制器，负责提供与定时任务相关的API接口。
 * @ClassName JobController
 * @Description 负责定时任务的控制器
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@RestController
@RequestMapping("/job/activate")
@Api(value = "定时任务", tags = "触发")
@AuthTarget(TargetEnum.COLLECTION_ANALYSE)
public class JobController {

    /**
     * 自动注入CopyEsCollectionDataScheduleJob实例，用于执行数据复制定时任务。
     */
    @Autowired
    private CopyEsCollectionDataScheduleJob copyEsCollectionDataScheduleJob;

    /**
     * 执行复制ES集合数据的定时任务。
     * <p>
     * 该接口用于触发CopyEsCollectionDataScheduleJob的执行，以实现将ES集合数据复制到其他目标的功能。
     * 接口调用成功后，返回"success"字符串。
     *
     * @return 返回字符串"success"，表示触发任务执行成功。
     */
    @ApiOperation(value = "es数据存储定时任务", notes = "启动")
    @GetMapping("/copyEsCollectionDataJob")
    public String copyEsCollectionData() {
        copyEsCollectionDataScheduleJob.execute();
        return "success";
    }
}
