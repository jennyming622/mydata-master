package pressure_test;

public class risPressure {

	public static void main(String[] args) {
		/**
		 * API.7QovE2Gev6 個人戶籍資料 post https://rwa.moi.gov.tw:1443/mydata/rwp201 
		 * {
				"Authorization":"#{accessToken}",
				"sris-consumerAdminId":"00000000",
				"X-Consumer-Custom-ID":"A41000000G",
				"Content-Type":"application/json"
			}
		 * 
		 * API.UDauDOLyZg 現戶全戶戶籍資料 post https://rwa.moi.gov.tw:1443/mydata/rwp101 
			{
				"Authorization":"#{accessToken}",
				"sris-consumerAdminId":"00000000",
				"X-Consumer-Custom-ID":"A41000000G",
				"Content-Type":"application/json"
			}
		 * 
		 * API.Kr1C3b1ijJ 親屬關係資料 post https://rwa.moi.gov.tw:1443/mydata/rwe31a
			{
				"Authorization":"#{accessToken}",
				"sris-consumerAdminId":"00000000",
				"X-Consumer-Custom-ID":"A41000000G",
				"Content-Type":"application/json"
			}
		 * 
		 * API.idPhotoRev 戶政國民身分證影像 post https://rwa.moi.gov.tw:1443/mydata/rwup01
			{
				"Authorization":"#{accessToken}",
				"sris-consumerAdminId":"00000000",
				"X-Consumer-Custom-ID":"A41000000G",
			        "Content-Type":"application/json"
			}
		 * 
		 */
		/**
		 * 211	RjEyODIwNTA0NTE5OTEwMTA5	陳家葳	F128205045 gfnm2zcekconybznfslff1muttcooaoqrhzvsaf8t8vf73b48dbeu1llj858kxzc
		 * 291	SDEyMDk4MzA1MzE5NzAwOTA5	林維德	H120983053 2wwto8yfeffkrn6rzg2t3fribqeareiwmowytoybs6cwqkl4olt8kr2urfsw9dl0
		 * 323	SDEyNDA3NDE1MzE5ODkxMTI3	詹前瑨	H124074153 fzxzxzk07uxewmeimw8ezjli1mhnc9m9gqfbq6ptcmhus1ybmwgqdbai7rt7kw0x
		 * 325	SDIyMzcxODA0MTE5OTEwNTMw	李星玟	H223718041 1mm2ckikljatr9ni8k8k8hwfbkgxvagnq4d2t6uo50a3n4eeucqv2rwqwuefafge
		 * 
		 * ris_review_one ris_review_all ris_review_cog ris_IDPhoto_review
		 * BLI.inqInsDtl BLI.inqLIBf BLI.inqNPBf BLI.inqPrePayCh BLI.inqBnfBf
		 * 
		 * API.UZQkKbsOpz 勞工保險被保險人投保資料（明細）post https://edesk.bli.gov.tw/na/ws/mydata/insureData
			{
				"Content-Type":"application/zip",
				"inTyp":"A",
				"Authorization": "Bearer #{accessToken}",
				"transaction_uid": "#{transactionUid}"
			}
		 * API.dgqt6aTYJy 勞保年金給付資料 post https://edesk.bli.gov.tw/na/ws/mydata/inqLIBf
			{
				"Content-Type":"application/zip",
				"Authorization": "Bearer #{accessToken}",
				"transaction_uid": "#{transactionUid}"
			}
		 * API.EZ6XjKf1B4 國民年金給付資料 post https://edesk.bli.gov.tw/na/ws/mydata/inqNPBf
			{
				"Content-Type":"application/zip",
				"Authorization": "Bearer #{accessToken}",
				"transaction_uid": "#{transactionUid}"
			}
		 * API.yqkllwwTYl 勞工提繳異動資料 post https://edesk.bli.gov.tw/na/ws/mydata/inqPrepayChange
			{
				"Content-Type":"application/zip",
				"Authorization": "Bearer #{accessToken}",
				"transaction_uid": "#{transactionUid}"
			}
		 * API.GCWlth5Qn4 勞保就保農保給付明細資料 post https://edesk.bli.gov.tw/na/ws/mydata/inqBnfBf
			{
				"Content-Type":"application/zip",
				"Authorization": "Bearer #{accessToken}",
				"transaction_uid": "#{transactionUid}",
				"payTyp" : ""
			}
		 *
		 *
		 * 34 API.zH584wn59r 個人投退保資料  post https://cloudicweb.nhi.gov.tw/NDCMydata/api/PersonalInfoList
			{"accessToken":"#{accessToken}"}
			personal.insurance.query
		 * 35 API.1qIr0nM0BT 保費繳納紀錄 post https://cloudicweb.nhi.gov.tw/NDCMydata/api/PersonalPaidList
			{"accessToken":"#{accessToken}"}
			personal.paidlist.query
		 */
		int loop = 1;
		int delay = 1;
		
		
		
	}

}
