package cn.cyberpeace.mp.collection.service;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/12/15 15:36
 */
@Slf4j
@Service
public class ScheduleService {

    // 每2分钟统计一次
    public static final long REFRESH_RATE = 5;

    public static List<String> teams = null;

    public static List<String> devices = null;

    public static String esUrl = "http://10.10.100.7:9205/arkime_sessions3-221215/_doc";

    public static String catagory = "Generic Protocol Command Decode";

    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 2, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100000), new ThreadPoolExecutor.CallerRunsPolicy());


    @Scheduled(initialDelay = 0, fixedRate = REFRESH_RATE, timeUnit = TimeUnit.MINUTES)
    public void schedule() throws InterruptedException {

        // 选手的ip
        if (CollectionUtil.isEmpty(teams)) {
            File source = new File("/Users/sternberg/work/nitai/mock/sourceIps_1");
            teams = FileUtil.readLines(source, "utf8");
        }
        // 设备方的ip
        if (CollectionUtil.isEmpty(devices)) {
            File target = new File("/Users/sternberg/work/nitai/mock/targetIps_1");
            devices = FileUtil.readLines(target, "utf8");
        }

        // 造的数据里面的时间戳 , 表示这个攻击的时间
        // 注意 造数据的时候要看下索引， arkime_sessions3-221210 这个索引是按天生成的
        // 如果需要指定某种类型的流量，可以改 参数里面, suricata.category 的值 , 这个值的类型在 international_message 里面有
        // 到时候让周豪现场选择5个类型发下
        long time = System.currentTimeMillis();

        List<String> docList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            for (String team : teams) {
                for (String device : devices) {

                    List<String> teamIps = JSONObject.parseArray(team, String.class);
                    List<String> deviceIps = JSONObject.parseArray(device, String.class);

                    for (String teamIp : teamIps) {
                        for (String deviceIp : deviceIps) {
                            var result = data(time, teamIp.trim(), deviceIp.trim(), catagory);
                            docList.add(result);
                        }
                    }
                }
            }
        }

        log.info("docList.size:{}", docList.size());
        CountDownLatch countDownLatch = new CountDownLatch(docList.size());
        doExecute(docList, countDownLatch);
        countDownLatch.await();
        log.info("耗时:{}", (System.currentTimeMillis() - time));
    }



    public void doExecute(List<String> docList, CountDownLatch countDownLatch) {
        for (String doc : docList) {
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpUtils.sendPost(esUrl, doc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
    }


    /*public static Object result(long time, String sourceIp, String targetIp, String catagory) {
        JSONObject result = (JSONObject) JSON.parse(data(catagory));
        {
            result.put("@timestamp", time);
        }
        {
            JSONObject source = result.getJSONObject("source");
            source.put("ip", sourceIp);
        }
        {
            JSONObject target = result.getJSONObject("destination");
            target.put("ip", targetIp);
        }
        return result;
    }*/

    public static String data(long time, String sourceIp, String targetIp, String catagory) {
        return "{\n" +
                "    \"firstPacket\": 1670553145804,\n" +
                "    \"lastPacket\": 1670553154978,\n" +
                "    \"length\": 9174,\n" +
                "    \"ipProtocol\": 1,\n" +
                "    \"@timestamp\": " + time + ",\n" +
                "    \"source\": {\n" +
                "        \"ip\": \"" + sourceIp + "\",\n" +
                "        \"port\": 0,\n" +
                "        \"bytes\": 930,\n" +
                "        \"packets\": 6,\n" +
                "        \"mac-cnt\": 1,\n" +
                "        \"mac\": [\n" +
                "            \"00:0c:29:f6:a6:59\"\n" +
                "        ]\n" +
                "    },\n" +
                "    \"destination\": {\n" +
                "        \"ip\": \"" + targetIp + "\",\n" +
                "        \"port\": 0,\n" +
                "        \"bytes\": 930,\n" +
                "        \"packets\": 6,\n" +
                "        \"mac-cnt\": 1,\n" +
                "        \"mac\": [\n" +
                "            \"e8:ab:f3:af:24:c2\"\n" +
                "        ]\n" +
                "    },\n" +
                "    \"dstRIR\": \"ARIN\",\n" +
                "    \"network\": {\n" +
                "        \"packets\": 12,\n" +
                "        \"bytes\": 1860\n" +
                "    },\n" +
                "    \"client\": {\n" +
                "        \"bytes\": 726\n" +
                "    },\n" +
                "    \"server\": {\n" +
                "        \"bytes\": 726\n" +
                "    },\n" +
                "    \"totDataBytes\": 1452,\n" +
                "    \"segmentCnt\": 1,\n" +
                "    \"node\": \"localhost\",\n" +
                "    \"packetPos\": [\n" +
                "        -119,\n" +
                "        827756112,\n" +
                "        356,\n" +
                "        3444,\n" +
                "        416,\n" +
                "        28377,\n" +
                "        356,\n" +
                "        5068,\n" +
                "        416,\n" +
                "        23783,\n" +
                "        356,\n" +
                "        8405,\n" +
                "        152\n" +
                "    ],\n" +
                "    \"fileId\": [\n" +
                "        119\n" +
                "    ],\n" +
                "    \"dstDscpCnt\": 1,\n" +
                "    \"dstDscp\": [\n" +
                "        1\n" +
                "    ],\n" +
                "    \"dstOuiCnt\": 1,\n" +
                "    \"dstOui\": [\n" +
                "        \"Huawei Technologies Co.,Ltd\"\n" +
                "    ],\n" +
                "    \"icmp\": {\n" +
                "        \"code\": [\n" +
                "            9,\n" +
                "            0\n" +
                "        ],\n" +
                "        \"type\": [\n" +
                "            0,\n" +
                "            8\n" +
                "        ]\n" +
                "    },\n" +
                "    \"protocolCnt\": 1,\n" +
                "    \"protocol\": [\n" +
                "        \"icmp\"\n" +
                "    ],\n" +
                "    \"srcDscpCnt\": 1,\n" +
                "    \"srcDscp\": [\n" +
                "        1\n" +
                "    ],\n" +
                "    \"srcOuiCnt\": 1,\n" +
                "    \"srcOui\": [\n" +
                "        \"VMware, Inc.\"\n" +
                "    ],\n" +
                "    \"suricata\": {\n" +
                "        \"actionCnt\": 1,\n" +
                "        \"action\": [\n" +
                "            \"allowed\"\n" +
                "        ],\n" +
                "        \"categoryCnt\": 1,\n" +
                "        \"category\": [\n" +
                "            \"" + catagory + "\"\n" +
                "        ],\n" +
                "        \"flowIdCnt\": 1,\n" +
                "        \"flowId\": [\n" +
                "            \"1056504447316917\"\n" +
                "        ],\n" +
                "        \"gidCnt\": 1,\n" +
                "        \"gid\": [\n" +
                "            1\n" +
                "        ],\n" +
                "        \"severityCnt\": 1,\n" +
                "        \"severity\": [\n" +
                "            3\n" +
                "        ],\n" +
                "        \"signatureCnt\": 1,\n" +
                "        \"signature\": [\n" +
                "            \"SURICATA ICMPv4 unknown code\"\n" +
                "        ],\n" +
                "        \"signatureIdCnt\": 1,\n" +
                "        \"signatureId\": [\n" +
                "            2200025\n" +
                "        ]\n" +
                "    }\n" +
                "}";
    }

}
