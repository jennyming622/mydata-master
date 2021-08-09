package pressure_test;

import java.util.ArrayList;
import java.util.List;

public class pressureMain {

	public static void main(String[] args) {
		
//		for(int i=0;i<200;i++) {
//			Thread newThread = new pressureThread();
//			newThread.start();
//		}
		List<String> authorizationList = new ArrayList<String>(); 
		authorizationList.add("mydata::gfnm2zcekconybznfslff1muttcooaoqrhzvsaf8t8vf73b48dbeu1llj858kxzc");
		authorizationList.add("mydata::2wwto8yfeffkrn6rzg2t3fribqeareiwmowytoybs6cwqkl4olt8kr2urfsw9dl0");
		authorizationList.add("mydata::fzxzxzk07uxewmeimw8ezjli1mhnc9m9gqfbq6ptcmhus1ybmwgqdbai7rt7kw0x");
		authorizationList.add("mydata::1mm2ckikljatr9ni8k8k8hwfbkgxvagnq4d2t6uo50a3n4eeucqv2rwqwuefafge");
		for(String a:authorizationList) {
			pressureThread25 newThread = new pressureThread25();
			newThread.setAuthorization(a);
			newThread.start();
		}
	}

}
