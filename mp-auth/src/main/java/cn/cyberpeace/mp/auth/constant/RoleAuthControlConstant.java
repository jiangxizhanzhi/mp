package cn.cyberpeace.mp.auth.constant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.constant
 * @Author: hhuang
 * @CreateTime: 2023/03/25  11:34
 * @Description: TODO
 * @Version: 1.0
 */
public class RoleAuthControlConstant {

    //管理前缀
    public static final String BASE_API_PATH = "/devops/cluster";

    //靶标管理（TM 表示靶标管理）
    public static final String TM_TARGET_API_PATH = BASE_API_PATH + "/target";

    //镜像管理
    public static final String TM_IMAGES_API_PATH = BASE_API_PATH + "/images";

    public static final String TM_IMAGES_AUTHPOST_API_PATH = TM_IMAGES_API_PATH + "/auth";

    //服务管理
    public static final String TM_SERVICE_API_PATH = BASE_API_PATH + "/service";

    //实装管理
    public static final String TM_REALEQUIPMENT_API_PATH = BASE_API_PATH + "/real-equipment";

    //流量相关
    public static final String FLOW_BASE_API_PATH = "/flow";

    //流量信息
    public static final String FLOW_INFO_API_PATH = FLOW_BASE_API_PATH+"/info";

    //删除流量
    public static final String FLOW_INFO_DELETE_API_PATH = FLOW_INFO_API_PATH+"/batch_delete";

    //背景流量
    public static final String FLOW_INFO_BACKGROUND_API_PATH = FLOW_INFO_API_PATH+"/background";

    public static final String FLOW_INFO_ATTACK_API_PATH = FLOW_INFO_API_PATH+"/attack";

    //流量集合
    public static final String FLOW_COLLECTION_API_PATH = FLOW_BASE_API_PATH+"/collection";

    //流量集合（新增，更新）
    public static final String FLOW_COLLECTION_BULK_API_PATH = FLOW_COLLECTION_API_PATH+"/bulk";

    //删除集合关联信息
    public static final String FLOW_COLLECTION_DELETE_API_PATH = FLOW_COLLECTION_API_PATH+"/bulk_delete";
    //流量管理
    public static final String FLOW_COLLECTION_MG_API_PATH = FLOW_COLLECTION_API_PATH+"/collection_rel";

    //探针管理相关
    public static final String PROBE_MANAGE_API_PATH = "/probes";

    //采集模板
    public static final String PROBE_CT_API_PATH ="/collection/template";

    public static final String SYS_CONFIG_API_PATH = "/sys/config";

    //系统配置
    public static final String SYS_CONFIG_UPDATEALL_API_PATH = SYS_CONFIG_API_PATH + "/updateall";


    public static final String RUNTIMELOG_API_PATH = "/devops/log/runtime_log";

    //中台运行日志下载
    public static final String RUNTIMELOG_DOWNLOAD_API_PATH = RUNTIMELOG_API_PATH + "/download";

    //系统公告管理
    public static final String SYS_NOTICE_API_PATH = "/sys/notice";

    //云平台监控
    public static final String CPMONITOR_API_PATH = BASE_API_PATH + "/monitor";

    //云平台节点管理-关机、重启
    public static final String CPMONITOR_ELELIFTE_API_PATH = CPMONITOR_API_PATH + "/hypervisor/reboot-poweroff";

    //释放无效资源
    public static final String CPMONITOR_RELEASE_API_PATH = CPMONITOR_API_PATH + "/release_resource";

    //云平台服务重启
    public static final String CPMONITOR_RESTART_API_PATH = CPMONITOR_API_PATH + "/restart";


    /**
     * 是否为试用角色
     */
    public enum TRAIL_TYPE {
        TRUE(1),
        FALSE(0);
        private final Integer value;
        TRAIL_TYPE(Integer value) {
            this.value = value;
        }
        public Integer getType() {
            return value;
        }
    }


    /**
     * 试用角色控制禁用的请求（暂只处理不需要校验参数的内容）
     * TODO 临时使用配置，后续通用角色可改为数据库表
     */
    public enum FORBIDDEN_REQUEST {
        //IMAGES_QUERY(TM_IMAGES_API_PATH+"/page",HttpMethod.GET.name(),"imagesQuery"),
        //镜像管理相关
        //IMAGES_UPDATE(TM_IMAGES_API_PATH+"/update",HttpMethod.POST.name(),"imagesUpdate"),//ImageRequest String uuid
        //IMAGES_DELETE(TM_IMAGES_API_PATH+"/page", HttpMethod.DELETE.name(),"imagesDelete"),//ImagesDeleteRequest List<String> imageIds
        //IMAGES_INJECT_POST(TM_IMAGES_API_PATH+"/inject-file", HttpMethod.POST.name(),"imagesInjectPost"),//ImageInjectFileRequest String deviceUuid
        //IMAGES_INJECT_DELETE(TM_IMAGES_API_PATH+"/inject-file", HttpMethod.DELETE.name(),"imagesInjectDelete"),//ImageInjectFileDeleteRequest String injectUuid;
        //IMAGES_AUTH_POST(TM_IMAGES_AUTHPOST_API_PATH, HttpMethod.POST.name(), TM_IMAGES_API_PATH),
        //实装管理相关
        //REALEQUIPMENT_DELETE(TM_REALEQUIPMENT_API_PATH+"/delete", HttpMethod.DELETE.name(),TM_REALEQUIPMENT_API_PATH),//RealEquipmentDeleteRequest List<String> uuids
        //REALEQUIPMENT_UPDATE(TM_REALEQUIPMENT_API_PATH+"/update", HttpMethod.PUT.name(),TM_REALEQUIPMENT_API_PATH),//RealEquipmentRequest uuid

        //服务管理相关
        SERVICE_DELETE(TM_SERVICE_API_PATH, HttpMethod.DELETE.name(),TM_SERVICE_API_PATH),//List<String> uuids  InstallerDeleteRequest
        SERVICE_UPDATE(TM_SERVICE_API_PATH, HttpMethod.PATCH.name(),TM_SERVICE_API_PATH),//String uuid
        SERVICE_ADD(TM_SERVICE_API_PATH, HttpMethod.POST.name(),TM_SERVICE_API_PATH),

        //流量管理
        FLOW_INFO_DELETE(FLOW_INFO_DELETE_API_PATH, HttpMethod.DELETE.name(),FLOW_INFO_API_PATH),
        FLOW_INFO_BG_UPDATE(FLOW_INFO_BACKGROUND_API_PATH, HttpMethod.PATCH.name(),FLOW_INFO_API_PATH),
        FLOW_INFO_BG_ADD(FLOW_INFO_BACKGROUND_API_PATH, HttpMethod.POST.name(),FLOW_INFO_API_PATH),
        //攻击流量
        FLOW_INFO_AT_ADD(FLOW_INFO_ATTACK_API_PATH, HttpMethod.POST.name(),FLOW_INFO_API_PATH),
        FLOW_INFO_AT_UPDATE(FLOW_INFO_ATTACK_API_PATH, HttpMethod.PATCH.name(),FLOW_INFO_API_PATH),


        //流量集合
        FLOW_COLLECTION_ADD(FLOW_COLLECTION_BULK_API_PATH, HttpMethod.POST.name(),FLOW_COLLECTION_API_PATH),
        FLOW_COLLECTION_UPDATE(FLOW_COLLECTION_BULK_API_PATH, HttpMethod.PATCH.name(),FLOW_COLLECTION_API_PATH),
        FLOW_COLLECTION_DELETE(FLOW_COLLECTION_DELETE_API_PATH, HttpMethod.DELETE.name(),FLOW_COLLECTION_API_PATH),
        FLOW_COLLECTION_REL_ADD(FLOW_COLLECTION_MG_API_PATH, HttpMethod.POST.name(),FLOW_COLLECTION_API_PATH),
        FLOW_COLLECTION_REL_DELETE(FLOW_COLLECTION_MG_API_PATH, HttpMethod.DELETE.name(),FLOW_COLLECTION_API_PATH),


        //探针相关
        PROBE_MANAGE_ADD(PROBE_MANAGE_API_PATH, HttpMethod.POST.name(),PROBE_MANAGE_API_PATH),
        PROBE_MANAGE_UPDATE(PROBE_MANAGE_API_PATH, HttpMethod.PATCH.name(),PROBE_MANAGE_API_PATH),
        PROBE_MANAGE_DELETE(PROBE_MANAGE_API_PATH, HttpMethod.DELETE.name(),PROBE_MANAGE_API_PATH),

        //采集模板
        PROBE_CT_ADD(PROBE_CT_API_PATH, HttpMethod.POST.name(),PROBE_CT_API_PATH),
        PROBE_CT_UPDATE(PROBE_CT_API_PATH, HttpMethod.PATCH.name(),PROBE_CT_API_PATH),
        PROBE_CT_DELETE(PROBE_CT_API_PATH, HttpMethod.DELETE.name(),PROBE_CT_API_PATH),


        //系统更新设置
        SYS_CONFIG_POST(SYS_CONFIG_UPDATEALL_API_PATH, HttpMethod.POST.name(),SYS_CONFIG_API_PATH),

        //运行日志下载
        RUNTIMELOG_DOWNLOAD_GET(RUNTIMELOG_DOWNLOAD_API_PATH, HttpMethod.GET.name(), RUNTIMELOG_API_PATH),

        //系统公告新增
        SYS_NOTICE_POST(SYS_NOTICE_API_PATH, HttpMethod.POST.name(), SYS_NOTICE_API_PATH),

        //系统公告删除
        SYS_NOTICE_DELETE(SYS_NOTICE_API_PATH, HttpMethod.DELETE.name(), SYS_NOTICE_API_PATH),

        //云平台节点管理-关机、重启
        CPMONITOR_ELELIFTE_GET(CPMONITOR_ELELIFTE_API_PATH, HttpMethod.GET.name(), CPMONITOR_API_PATH),

        //云平台释放无效资源
        CPMONITOR_RELEASE_GET(CPMONITOR_RELEASE_API_PATH, HttpMethod.GET.name(), CPMONITOR_API_PATH),

        //云平台服务重启
        CPMONITOR_RESTART_POST(CPMONITOR_RESTART_API_PATH, HttpMethod.POST.name(), CPMONITOR_API_PATH);















        private final String url;

        private final String method;

        private final String model;
        FORBIDDEN_REQUEST(String url,String method,String model) {
            this.url = url;
            this.method = method;
            this.model = model;
        }

        public String getUrl() {
            return url;
        }

        public String getMethod() {
            return method;
        }

        public String getModel() {
            return model;
        }

        /**
         * @description: 是否为禁用请求
         * @param servletPath
         * @param method 
         * @throws 
         * @return boolean
         */
        public static boolean containsRquest(String servletPath,String method){
            if(StringUtils.isEmpty(servletPath)){
                return false;
            }
            Stream<FORBIDDEN_REQUEST> streams = Arrays.stream(FORBIDDEN_REQUEST.values());
            return streams.filter(fr -> servletPath.startsWith(fr.getUrl()) && fr.getMethod().equalsIgnoreCase(method)).findAny().isPresent();
        }

        /**
         * @description: 返回符合条件的FORBIDDEN_REQUEST
         * @param servletPath
         * @param method
         * @throws
         * @return FORBIDDEN_REQUEST
         */
        public static FORBIDDEN_REQUEST getForbiddenRequest(String servletPath,String method){
            Stream<FORBIDDEN_REQUEST> streams = Arrays.stream(FORBIDDEN_REQUEST.values());
            return streams.filter(fr -> fr.getUrl().equals(servletPath) && fr.getMethod().equalsIgnoreCase(method)).findFirst().get();
        }
    }


    private RoleAuthControlConstant() {}
}
