package cn.cyberpeace.mp.situationaware.model.enums;

/**
  *@Desciption 探针脚本类型
  *@Author zhangjin
  *@Date 8/31/21 11:51 上午
**/
public enum ScriptType {
    SHELL(0),
    PYTHON(1),
    BAT(2)
    ;

    private int typeVal;

    ScriptType(int typeVal) {
        this.typeVal = typeVal;
    }

    public int getTypeVal() {
        return typeVal;
    }
}
