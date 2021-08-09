package tw.gov.ndc.emsg.mydata.entity;

public class PortalServiceCategoryExt extends PortalServiceCategory{

	private boolean hasUnit = false;

	private Integer count;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public boolean isHasUnit() {
		return hasUnit;
	}

	public void setHasUnit(boolean hasUnit) {
		this.hasUnit = hasUnit;
	}
}
