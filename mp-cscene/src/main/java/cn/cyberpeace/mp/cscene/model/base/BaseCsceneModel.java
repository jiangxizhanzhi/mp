package cn.cyberpeace.mp.cscene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseCsceneModel extends BaseModel {
    @DefaultInt
    private Integer referenceType;
    @DefaultStr
    private String referenceMetaData;
    @DefaultStr
    private String createUser;
    @DefaultStr
    private String createUsername;
    @DefaultBool
    private Boolean deleted;
    @DefaultStr
    private String name;

    @Override
    public boolean hasDeleted() {
        return getDeleted();
    }

    @Override
    public boolean prepareDeleteFlag() {
        setDeleted(true);
        return true;
    }

    public static class ReferenceType {
        public static final int DEFAULT = 0;
        public static final int PRACTICE = 110;
        public static final int LESSON = 120;
        public static final int EXAM = 130;
        public static final int LESSON_PRACTICE = 140;
        public static final int INDIVIDUAL_TRAINING = 211;
        public static final int LIVE_VULN = 212;
        public static final int TEAM_TRAINING = 213;
        public static final int READ_VS_BLUE = 214;
        public static final int WEAPON_TEST = 216;
        public static final int SYSTEM_TEST = 217;
        public static final int SOFT_TEST = 221;
        public static final int INFILTRATION = 310;
        public static final int COURSE_TRAINING = 150;
        public static final int SKILLS_ASSESSMENT = 231;
        public static final int TACTICAL_ASSESSMENT = 232;
        public static final int COMPREHENSIVE_ASSESSMENT = 233;
        public static final int TACTICAL_TRAINING = 234;
        public static final int COMPREHENSIVE_TRAINING = 235;
        public static final int RANGE_TEST = 236;

        private ReferenceType() {}
    }
}
