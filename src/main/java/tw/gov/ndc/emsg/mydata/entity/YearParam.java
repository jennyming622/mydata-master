package tw.gov.ndc.emsg.mydata.entity;

public class YearParam {
	private Integer start; // 開始年份 0 表示當年
	private Integer years; // 往前推幾年

	public YearParam(Integer start, Integer years) {
		this.start = start;
		this.years = years;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

	public Integer getEnd() {
		return years - start - 1;
	}
	// 109
	// 110 109 108 107 106 105
}
