package tw.gov.ndc.emsg.mydata.entity.ext;

import tw.gov.ndc.emsg.mydata.entity.AuthToken;
import tw.gov.ndc.emsg.mydata.entity.Member;

public class MemberExt extends Member{

	private AuthToken authToken;

	public AuthToken getAuthToken() {
		return authToken;
	}
	public void setAuthToken(AuthToken authToken) {
		this.authToken = authToken;
	}
	
}
