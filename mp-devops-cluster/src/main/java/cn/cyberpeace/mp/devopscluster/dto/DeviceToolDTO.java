package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.model.DeviceToolModel;

import java.util.List;

public class DeviceToolDTO extends DeviceToolModel {

    /**
     * 攻击方式
     */
    private List<String> attackTypeList;

    /**
     * 攻击返回类型集合
     */
    private List<String> attackDataTypeList;


    public enum attackMethodEnum {
        //        中断、拒止、欺骗、嗅探、降效等7种
        A1(1,"中断"),
        b1(2,"拒止");

        private int code;
        private String name;

        attackMethodEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
