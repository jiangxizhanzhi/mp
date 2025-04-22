package cn.cyberpeace.mp.base.constant;

public enum ExcelHeader {

	FILE_COUNT(new String[]{"时间", "数量"}),
	FILE_LIST(new String[]{"主机名称", "时间", "文件名称", "动作", "级别", "级别编码", "文件"}),
	REGISTRY_LIST(new String[]{"主机名称", "时间", "注册表名称", "动作", "级别", "级别编码", "修改前", "修改后"}),
	LOG_LIST(new String[]{"主机名称", "时间", "应用名", "事件名称", "级别", "级别编码", "规则描述", "category","规则编码"}),
	PROBE_LIST(new String[]{"主机名称", "探针名称", "详情", "时间"}),
	;

	private  String[] cnName;

	ExcelHeader(String[] cnName) {
		this.cnName = cnName;
	}

	public String[] getCnName() {
		return cnName;
	}

}
