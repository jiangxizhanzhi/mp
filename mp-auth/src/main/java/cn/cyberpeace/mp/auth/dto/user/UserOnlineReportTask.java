package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.service.UserOnlineReportService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class UserOnlineReportTask implements Runnable {


    private UserOlReportCntDto userOlReportCntDto;

    private UserOnlineReportService userOnlineReportService;

    public UserOnlineReportTask(UserOlReportCntDto userOlReportCntDto, UserOnlineReportService userOnlineReportService) {
        this.userOlReportCntDto = userOlReportCntDto;
        this.userOnlineReportService = userOnlineReportService;
    }

    @Override
    public void run() {
        // 抽取每个user的数据，给每个user生成一条在线报告记录
        userOnlineReportService.createOrUpdate(userOlReportCntDto);
    }

}
