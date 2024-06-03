package cn.cyberpeace.mp.base.constant;

/**
 * target静态类
 */
public enum TargetEnum {
    AUDIT_LOG(new String[]{"audit-log"}),
    BUSINESS_SERVICE_MONITORING(new String[]{"business-service-monitoring"}),
    CAREER_SYSTEM(new String[]{"career-system"}),
    CLASS_CARD(new String[]{"class-card"}),
    CLOUD_PLATFORM_MONITORING(new String[]{"cloud-platform-monitoring"}),
    CLUSTER_MANAGEMENT(new String[]{"cluster-management"}),
    COLLECTION_ANALYSE(new String[]{"collection-analyse"}),
    COLLECTION_TEMPLATE(new String[]{"collection-template"}),
    COURSE_FOLDER(new String[]{"course-folder"}),
    COURSES(new String[]{"courses"}),
    CR_INDIVIDUAL_TRAINING(new String[]{"cr-individual-training"}), // 后台单兵训练
    CR_MISSION_ARRANGE_FOLDER(new String[]{"cr-mission-arrange-folder"}),
    CR_TEAM_TRAINING(new String[]{"cr-team-training"}), // 后台团队训练
    CT_AD_LIST(new String[]{"ct-ad-list"}),
    CT_AD_QUESTION_LIST(new String[]{"ct-ad-question-list"}),
    CT_ARENA_LIST(new String[]{"ct-arena-list"}),
    CT_ATTACK_DEFENSE_LIST(new String[]{"ct-attack-defense-list"}),
    CT_CFS_LIST(new String[]{"ct-cfs-list"}),
    CT_CFS_QUESTION_LIST(new String[]{"ct-cfs-question-list"}),
    CT_CONTEST_ARENA_LIST(new String[]{"ct-contest-arena-list"}),
    CT_CONTEST_CFS_LIST(new String[]{"ct-contest-cfs-list"}),
    CT_CONTEST_EXAM_LIST(new String[]{"ct-contest-exam-list"}),
    CT_CONTEST_OPERATION_LIST(new String[]{"ct-contest-operation-list"}),
    CT_EXAMS_LIST(new String[]{"ct-exams-list"}),
    CT_FLAG_CONTEST_LIST(new String[]{"ct-flag-contest-list"}),
    CT_FLAG_LIST(new String[]{"ct-flag-list"}),
    CT_OPERATION_LIST(new String[]{"ct-operation-list"}),
    CT_OPERATION_QUESTION_LIST(new String[]{"ct-operation-question-list"}),
    CUSTOM_PROBE(new String[]{"custom-probe"}),
    DYNAMIC_TRAFFIC_LIST(new String[]{"dynamic-traffic-list"}),
    EXERCISES(new String[]{"exercises"}),
    FLAGS_SOLVE(new String[]{"flags-solve"}),
    FRONT_ALL(new String[]{"front_all"}),
    GHDB_LIBRARY(new String[]{"ghdb-library"}),
    GROUP_TRAINING(new String[]{"group-training"}),
    IMAGE_MANAGE(new String[]{"image-manage"}),
    DASHBOARD(new String[]{"dashboard"}),
    INSTANCE_MONITORING(new String[]{"instance-monitoring"}),
    LEARN_STATISTICS(new String[]{"learn-statistics"}),
    NETWORK_MANAGEMENT(new String[]{"network-management"}),
    NO_AUTH(new String[]{"no_auth"}),
    ADMIN(new String[]{"admin"}),
    OJ_ALL_COURSE(new String[]{"oj-all-course"}),
    OJ_COURSE(new String[]{"oj-course"}),
    OJ_KNOWLEDGE_COURSE(new String[]{"oj-knowledge-course"}),
    OJ_PRACTICE(new String[]{"oj-practice"}),
    OJ_PRACTICE_CAPTURE_FLAG(new String[]{"oj-practice-capture-flag"}),
    PERSONAL_CENTER(new String[]{"personal-center"}),
    PERSONAL_FOLDER(new String[]{"personal-folder"}),
    RANGE_TEST(new String[]{"range-test"}),
    RANGE_TEST_SELF(new String[]{"range-test-self"}), //前台靶场测评
    REAL_EQUIPMENT_MANAGEMENT(new String[]{"real-equipment-management"}),
    REAL_MANAGE(new String[]{"real-manage"}),
    RED_VS_BLUE(new String[]{"red-vs-blue"}), // 前台红蓝对抗
    RED_BLUE(new String[]{"red-blue"}), // 后台红蓝对抗
    RED_VS_BLUE_WEB_FOLDER(new String[]{"red-vs-blue-web-folder"}),
    RESOURCE_USAGE_MANAGEMENT(new String[]{"resource-usage-management"}),
    SERVICE_MANAGE(new String[]{"service-manage"}),
    SINGLE_LIST(new String[]{"single-list"}),
    STATIC_TRAFFIC_LIST(new String[]{"static-traffic-list"}),
    SYSTEM_ALARM(new String[]{"system-alarm"}),
    SYSTEM_CONFIG(new String[]{"system-config"}),
    SYSTEM_LOG(new String[]{"system-log"}),
    SYSTEM_NOTICE_FOLDER(new String[]{"system-notice-folder"}),
    THEORY_BASE(new String[]{"theory-base"}),
    TOOL(new String[]{"tool"}),
    TOOL_EXP(new String[]{"tool-exp"}),
    TOOL_MAGAZINE(new String[]{"tool-magazine"}),
    TOOL_SHELL(new String[]{"tool-shell"}),
    TOPOLOGY_MANAGEMENT(new String[]{"topology-management"}),
    TRAFFIC_ANALYSE_LIST(new String[]{"traffic-analyse-list"}),
    TRAFFIC_SCHEME_LIST(new String[]{"traffic-scheme-list"}),
    UNIVERSAL_PROBE(new String[]{"universal-probe"}),
    USB_EMULATION(new String[]{"usb-emulation"}),
    USER_MANAGE(new String[]{"user-manage"}),
    VULNERABILITY(new String[]{"vulnerability"}),
    VULNERABILITY_LIBRARY(new String[]{"vulnerability-library"}),

    CSCENE_TARGET_GROUP(new String[]{
            COURSE_FOLDER.getTargets()[0], COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0],
            CT_AD_LIST.getTargets()[0], CT_AD_QUESTION_LIST.getTargets()[0], CT_ARENA_LIST.getTargets()[0], CT_ATTACK_DEFENSE_LIST.getTargets()[0], CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_ARENA_LIST.getTargets()[0],
            CT_CONTEST_CFS_LIST.getTargets()[0], CT_CONTEST_EXAM_LIST.getTargets()[0], CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_EXAMS_LIST.getTargets()[0], CT_FLAG_CONTEST_LIST.getTargets()[0], CT_FLAG_LIST.getTargets()[0],
            CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], EXERCISES.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0], OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0],
            PERSONAL_FOLDER.getTargets()[0], RANGE_TEST.getTargets()[0], RED_BLUE.getTargets()[0], RED_VS_BLUE_WEB_FOLDER.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0]
    }),

    CSCENE_FLOW_TASK_GROUP(new String[]{TargetEnum.GROUP_TRAINING.getTargets()[0], TargetEnum.RANGE_TEST.getTargets()[0], TargetEnum.RED_VS_BLUE_WEB_FOLDER.getTargets()[0]}),

    CSCENE_INSTANCE_GROUP(new String[]{CT_AD_QUESTION_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], FLAGS_SOLVE.getTargets()[0], IMAGE_MANAGE.getTargets()[0], INSTANCE_MONITORING.getTargets()[0], RANGE_TEST_SELF.getTargets()[0]}),

    CSCENE_INSTANCE_CONNECT_GROUP(new String[]{COURSE_FOLDER.getTargets()[0], COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0],
            CR_TEAM_TRAINING.getTargets()[0], CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0],
            CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], FLAGS_SOLVE.getTargets()[0],
            GROUP_TRAINING.getTargets()[0], OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0], PERSONAL_FOLDER.getTargets()[0], RANGE_TEST.getTargets()[0],
            RED_VS_BLUE_WEB_FOLDER.getTargets()[0], RED_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0]}),

    CSCENE_MONITOR_GROUP(new String[]{TargetEnum.DASHBOARD.getTargets()[0], TargetEnum.GROUP_TRAINING.getTargets()[0], TargetEnum.INSTANCE_MONITORING.getTargets()[0], TargetEnum.RANGE_TEST_SELF.getTargets()[0], TargetEnum.RED_VS_BLUE.getTargets()[0], TargetEnum.SINGLE_LIST.getTargets()[0]}),

    CSCENE_PROTOCOL_GROUP(new String[]{COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0],
            CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0], CT_CONTEST_OPERATION_LIST.getTargets()[0],
            CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0],
            GROUP_TRAINING.getTargets()[0], OJ_ALL_COURSE.getTargets()[0], OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0],
            PERSONAL_FOLDER.getTargets()[0], RANGE_TEST_SELF.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0]}),


    DEVOPS_TARGET_GROUP(new String[]{IMAGE_MANAGE.getTargets()[0], COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0], CT_AD_LIST.getTargets()[0], CT_AD_QUESTION_LIST.getTargets()[0],
            CT_ARENA_LIST.getTargets()[0], CT_ATTACK_DEFENSE_LIST.getTargets()[0], CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_ARENA_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0],
            CT_CONTEST_EXAM_LIST.getTargets()[0], CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_EXAMS_LIST.getTargets()[0], CT_FLAG_CONTEST_LIST.getTargets()[0], CT_FLAG_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0],
            CT_OPERATION_QUESTION_LIST.getTargets()[0], EXERCISES.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0],
            OJ_ALL_COURSE.getTargets()[0], OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0], PERSONAL_FOLDER.getTargets()[0],
            RANGE_TEST_SELF.getTargets()[0], RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0]
    }),

    DEVOPS_DYNAMIPS_TARGET_GROUP(new String[]{
            IMAGE_MANAGE.getTargets()[0],
            COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0], CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0],
            CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0], OJ_ALL_COURSE.getTargets()[0],
            OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0], PERSONAL_FOLDER.getTargets()[0], RANGE_TEST_SELF.getTargets()[0], RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0],
    }),

    DEVOPS_IMAGE_GROUP(new String[]{
            COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0], CT_AD_LIST.getTargets()[0], CT_AD_QUESTION_LIST.getTargets()[0], CT_ARENA_LIST.getTargets()[0], CT_ATTACK_DEFENSE_LIST.getTargets()[0],
            CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_ARENA_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0], IMAGE_MANAGE.getTargets()[0], CT_CONTEST_EXAM_LIST.getTargets()[0],
            CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_EXAMS_LIST.getTargets()[0], CT_FLAG_CONTEST_LIST.getTargets()[0], CT_FLAG_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0],
            EXERCISES.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0], RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], TOPOLOGY_MANAGEMENT.getTargets()[0],
    }),

    DEVOPS_SNAPSHOT_GROUP(new String[]{
            COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0], CT_AD_LIST.getTargets()[0], CT_AD_QUESTION_LIST.getTargets()[0], CT_ARENA_LIST.getTargets()[0], CT_ATTACK_DEFENSE_LIST.getTargets()[0],
            CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_ARENA_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0], CT_CONTEST_EXAM_LIST.getTargets()[0], CT_CONTEST_EXAM_LIST.getTargets()[0],
            CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_EXAMS_LIST.getTargets()[0], CT_FLAG_CONTEST_LIST.getTargets()[0], CT_FLAG_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0],
            EXERCISES.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0], OJ_ALL_COURSE.getTargets()[0], OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0], PERSONAL_FOLDER.getTargets()[0], RANGE_TEST_SELF.getTargets()[0],
            RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0],
    }),

    DEVOPS_EQUIPMENT_PAGE_GROUP(new String[]{
            COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0], CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0], CT_CONTEST_OPERATION_LIST.getTargets()[0],
            CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0], OJ_ALL_COURSE.getTargets()[0], OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0],
            PERSONAL_FOLDER.getTargets()[0], RANGE_TEST_SELF.getTargets()[0], RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0],
    }),

    DEVOPS_CATEGORY_GROUP(new String[]{
            COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0], CT_AD_QUESTION_LIST.getTargets()[0], CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0],
            CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0], IMAGE_MANAGE.getTargets()[0], OJ_ALL_COURSE.getTargets()[0],
            OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0], PERSONAL_FOLDER.getTargets()[0], RANGE_TEST_SELF.getTargets()[0], RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0],
    }),

    FLOW_GROUP(new String[]{
            COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0], CT_AD_LIST.getTargets()[0], CT_AD_QUESTION_LIST.getTargets()[0], CT_ARENA_LIST.getTargets()[0], CT_ATTACK_DEFENSE_LIST.getTargets()[0], CT_CFS_LIST.getTargets()[0],
            CT_CFS_QUESTION_LIST.getTargets()[0], CT_CONTEST_ARENA_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0], CT_CONTEST_EXAM_LIST.getTargets()[0], CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_EXAMS_LIST.getTargets()[0],
            CT_FLAG_CONTEST_LIST.getTargets()[0], CT_FLAG_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0], EXERCISES.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0],
            OJ_ALL_COURSE.getTargets()[0], OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0], PERSONAL_FOLDER.getTargets()[0], RANGE_TEST_SELF.getTargets()[0], RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0],
    }),


    SITUATION_AWARE_GROUP(new String[]{
            COURSES.getTargets()[0], CR_INDIVIDUAL_TRAINING.getTargets()[0], CR_TEAM_TRAINING.getTargets()[0],
            CT_AD_LIST.getTargets()[0], CT_AD_QUESTION_LIST.getTargets()[0], CT_ARENA_LIST.getTargets()[0],
            CT_ATTACK_DEFENSE_LIST.getTargets()[0], CT_CFS_LIST.getTargets()[0], CT_CFS_QUESTION_LIST.getTargets()[0],
            CT_CONTEST_ARENA_LIST.getTargets()[0], CT_CONTEST_CFS_LIST.getTargets()[0], CT_CONTEST_EXAM_LIST.getTargets()[0],
            CT_CONTEST_OPERATION_LIST.getTargets()[0], CT_EXAMS_LIST.getTargets()[0], CT_FLAG_CONTEST_LIST.getTargets()[0],
            CT_FLAG_LIST.getTargets()[0], CT_OPERATION_LIST.getTargets()[0], CT_OPERATION_QUESTION_LIST.getTargets()[0],
            EXERCISES.getTargets()[0], FLAGS_SOLVE.getTargets()[0], GROUP_TRAINING.getTargets()[0], OJ_ALL_COURSE.getTargets()[0],
            OJ_COURSE.getTargets()[0], OJ_PRACTICE_CAPTURE_FLAG.getTargets()[0], PERSONAL_FOLDER.getTargets()[0], RANGE_TEST_SELF.getTargets()[0],
            RED_BLUE.getTargets()[0], RED_VS_BLUE.getTargets()[0], SINGLE_LIST.getTargets()[0], VULNERABILITY_LIBRARY.getTargets()[0],
    }),


    ;


    private final String[] targets;

    TargetEnum(String[] targets) {
        this.targets = targets;
    }

    public String[] getTargets() {
        return this.targets;

    }


}
