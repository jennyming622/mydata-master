package test;

public class testExtension {

	public static void main(String[] args) {
		String files = "myland_MCRjyu48.c1080220135027.zip";
		files = "en_1Vk0Y08x7l0Z0Nu5Xb20j60ML0i020180727080941.pdf";
		
		String[] filesList = files.split("[.]");
		if(filesList.length==0) {
			System.out.println("");
		}else {
			System.out.println(filesList[filesList.length-1]);
		}
	}

}
