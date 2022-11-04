package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/4
 */
@Getter
@Setter
public class TestStatusResult extends IxiaResponse {
    private Integer progress;
    private Integer time;

    @Getter
    @Setter
    private static class values {
        private String sctpFlowsConcurrent;
        private String timetonext;
        private String tcpServerEstablished;
        private String appAttempted;
        private String tcpClientStateClosing;
        private String ethTxFrameRate;
        private String tcpServerStateCloseWait;
        private String superflowSuccessful;
        private String tcpServerStateEstablished;
        private String concurrentAppFlows;
        private String rxFrames;
        private String sslHandshakesFinishedRate;
        private String tcpClientStateTimeWait;
        private String tcpClientClosed;
        private String tcpClosedByReset;
        private String ethRxFrameRate;
        private String tcpServerStateFinWait1;
        private String tcpServerStateFinWait2;
        private String tcpServerConcurrent;
        private String ethTxFrames;
        private String sslHandshakesTimeoutRate;
        private String tcpClientStateSynReceived;
        private String cpu_usage;
        private String appTxFrameData;
        private String appTxFrameDataRate;
        private String txFrameDataRate;
        private String superflowFailed;
        private String appAborted;
        private String conf_mem_usage;
        private String rxFrameDataRate;
        private String tcpAttemptRate;
        private String superflowConfiguredFrameDataRate;
        private String tcpClientStateCloseWait;
        private String totalStrikes;
        private String tcpServerStateSynReceived;
        private String tcpClientConcurrent;
        private String tcpServerStateClosing;
        private String tcpServerEstablishRate;
        private String tcpClientStateEstablished;
        private String tcpClientCloseRate;
        private String appServerDataValid;
        private String tcpTxFrameDataRate;
        private String mem_usage;
        private String sslHandshakesStartedRate;
        private String superFlowsConcurrent;
        private String totalSkipped;
        private String totalAllowed;
        private String tcpClientStateFinWait2;
        private String tcpClientStateFinWait1;
        private String appFlowRate;
        private String tcpAvgSessionDuration;
        private String disk_usage;
        private String superflowRxFrameData;
        private String ethRxFrameDataRate;
        private String tcpClientEstablishRate;
        private String appRxFrameData;
        private String tcpAvgCloseTime;
        private String tcpServerClosedByReset;
        private String appAttemptedRate;
        private String totalBlocked;
        private String superflowTxFrameDataRate;
        private String superflowTxFrameData;
        private String appUnsuccessfulRate;
        private String superflowOriginalFrameDataRate;
        private String ethTxFrameDataRate;
        private String appSuccessfulRate;
        private String appRxFrameDataRate;
        private String appIncomplete;
        private String superflowIterations;
        private String tcpFlowsConcurrent;

    }/*
                "ethRxFrames": "2437",
                "totalFlowsConcurrent": "0",
                "tcpRxFrameDataRate": "0",
                "totalFalsePositiveBlocked": "0",
                "txFrames": "2441",
                "componentActiveFlowsConcurrent": "0",
                "tcpClientStateSynSent": "0",
                "appUnsuccessful": "0",
                "tcpServerCloseRate": "0",
                "tcpAttempted": "61",
                "sslEncryptedDataRate": "0",
                "superflowRxFrameDataRate": "0",
                "tcpClientEstablished": "61",
                "totalErrored": "0",
                "appServerDataNotValid": "0",
                "sslDecryptedDataRate": "0",
                "tcpServerStateTimeWait": "0",
                "sslHandshakesAbortedRate": "0",
                "tcpClientClosedByReset": "0",
                "tcpServerClosed": "61",
                "appSuccessful": "122",
                "udpFlowsConcurrent": "0"
    },
        "testStuck": null
    }*/
}
