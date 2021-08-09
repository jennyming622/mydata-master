package tw.gov.ndc.emsg.mydata.config;

import java.io.Serializable;

/**
 * 麵包屑Entity
 * @author Weder
 *
 */
public class Breadcrumb implements Serializable {
	private static final long serialVersionUID = -1852100814939307568L;
	private String name = "";
	private String url = "";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
