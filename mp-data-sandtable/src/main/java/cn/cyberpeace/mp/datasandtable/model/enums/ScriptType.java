package cn.cyberpeace.mp.datasandtable.model.enums;

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
