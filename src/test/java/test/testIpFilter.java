package test;

public class testIpFilter {

	public static void main(String[] args) {
		String ip ="223.200.4.";
		String[] ipAddressSplitList = ip.split("[.]");
		for(String s:ipAddressSplitList) {
			System.out.println(s);
		}
		System.out.println("----------------:"+ipAddressSplitList.length);
		if(ipAddressSplitList!=null&&ipAddressSplitList.length==3) {
			System.out.println("true="+ip);
		}else {
			System.out.println("false="+ip);
		}
	}

}
