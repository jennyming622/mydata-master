package test;

import java.util.UUID;

public class testPermissionTicketAndSecretKey {

	public static void main(String[] args) {
		String permission_ticket = UUID.randomUUID().toString();
		String secret_key = UUID.randomUUID().toString().replace("-", "");
		System.out.println(permission_ticket);
		System.out.println(secret_key);
		String permission_ticket1 = UUID.randomUUID().toString();
		String permission_ticket2 = UUID.randomUUID().toString();
		String downloadSnListStr = permission_ticket1+","+permission_ticket2;
		System.out.println(downloadSnListStr);
		//String[] downloadSnArray = downloadSnListStr.split(",");
		String[] downloadSnArray = permission_ticket.split(",");
		
		if(downloadSnArray!=null&&downloadSnArray.length>0) {
			for(String s:downloadSnArray) {
				System.out.println("downloadSn:"+s);
			}
		}
	}

}
