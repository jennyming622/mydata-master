package test;

public class testmask {

	public static void main(String[] args) {
		String userName = "林維2德";
		System.out.println(userName.length());
		if(userName!=null&&userName.length()>=3) {
			int len = userName.length();
			System.out.println(userName.substring(0, 1));
			for(int i=1;i<len-1;i++) {
				System.out.println("*");
			}
			System.out.println(userName.substring(len-1));
			
		}
	}

	public String maskName(String userName) {
		String ret = "";
		if(userName!=null&&userName.length()>=3) {
			ret = userName.substring(0, 1);
			for(int i=1;i<userName.length()-1;i++) {
				ret = ret +"*";
			}
			ret = ret + userName.substring(userName.length()-1);
		}else if(userName!=null&&userName.length()==2) {
			ret = userName.substring(1, 2);
		}else if(userName!=null&&userName.length()==1) {
			ret = "*";
		}
		return ret;
	}
}
