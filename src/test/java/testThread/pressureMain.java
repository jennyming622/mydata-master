package testThread;

import java.util.ArrayList;
import java.util.Date;
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
		/*for(String a:authorizationList) {
			pressureThread newThread = new pressureThread();
			newThread.setAuthorization(a);
			newThread.start();
			
		}*/
		List<pressureThread> finalList = new ArrayList<pressureThread>();
		for(int i=0;i<authorizationList.size();i++) {
			pressureThread newThread = new pressureThread();
			newThread.setAuthorization(authorizationList.get(i));
			newThread.start();
			finalList.add(newThread);
		}
		boolean check = true;
		while(check) {
			boolean tmpCheck = true;
			try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(new Date());
			for(pressureThread p:finalList) {
				System.out.println(p.getAuthorization()+","+p.getCode());
				if(p.getCode()==null) {
					tmpCheck = false;
				}
			}
			if(tmpCheck==true) {
				check = false;
			}
		}
		
	}

}
