package test;

public class testUrlPase {

	public static void main(String[] args) {
		String baseUrl = "https://www.baidu.com?a=123&b=456&c=789";
		String[] urlArray = baseUrl.split("[?]",1000);
		
		String urlBase = "";
		String requery = "";
		if(urlArray.length > 0){
			urlBase = urlArray[0];
			if(urlArray.length > 1){
				requery =  urlArray[1];
			}
		}
		String newUrl = urlBase+"?uid=12345678&"+requery;
		System.out.println("urlBase="+urlBase);
		System.out.println("requery="+requery);
		System.out.println("newUrl="+newUrl);
		System.out.println("urlBase="+urlBase+";requery="+requery+";newUrl="+newUrl);
		
	}

}
