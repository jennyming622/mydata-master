package tw.gov.ndc.emsg.mydata.entity;

public class Param {
	private String name;
	private String desc;
	private String nameDesc;
	private String type;
	private Integer isOption;
	private YearParam yearParam;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getNameDesc() {
		return nameDesc;
	}
	public void setNameDesc(String nameDesc) {
		this.nameDesc = nameDesc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getIsOption() {
		return isOption;
	}
	public void setIsOption(Integer isOption) {
		this.isOption = isOption;
	}
	public YearParam getYearParam() {
		return yearParam;
	}
	public void setYearParam(YearParam yearParam) {
		this.yearParam = yearParam;
	}
}
