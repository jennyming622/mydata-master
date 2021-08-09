package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.web.AuController.UlogApiExtComparator;

public class testSort {
	
	public static Map<String,String> map = new HashMap<String,String>();
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f1 = new File("/Users/mac/Desktop/tmp/13000.1.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(f1))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	String[] s1 = line.split("[,]+");
		    	map.put(s1[1], s1[2]);
		    }
		}
		List<String> list = new ArrayList<String>(); 
		
//		  list.add("使用牌照稅身心障礙減免申請"); list.add("強化社會安全網急難紓困方案");
//		  list.add("敬老卡或愛心卡申辦服務"); list.add("身心障礙者及老人預防走失溫馨手鍊或項鍊");
//		  list.add("重陽節敬老禮金金融帳戶申請"); list.add("長照服務2.0線上申請");
		 
//		list.add("嘉義縣");
//		list.add("桃園市");
//		list.add("雲林縣");
		
//		list.add("手語翻譯及同步聽打服務申請表");
//		list.add("身心障礙者專用停車位識別證申請表");
//		list.add("重劃費用負擔證明申請書");

//		list.add("執行救護服務證明申請");
//		list.add("敬老愛心卡申請");
//		list.add("申請使用牌照稅身心障礙減免申請");
//		list.add("申辦減免房屋稅規劃申請");
//		list.add("預防走失-愛心手鍊申請");

//		list.add("執行救護服務證明申請");
//		list.add("愛心卡、愛心陪伴卡申請");
//		list.add("敬老卡申請");

		//list.add("教育部國民及學前教育署");
		//list.add("雲林縣");
//		list.add("中國信託銀行");
//		list.add("台中商業銀行");
//		list.add("台北富邦商業銀行");
//		list.add("台新國際商業銀行");
//		list.add("彰化商業銀行");
//		list.add("玉山商業銀行");
//		list.add("臺灣銀行");
//		list.add("遠東國際商業銀行");

//		list.add("信用卡線上申請");
//		list.add("信貸線上申請");
//		list.add("客戶資料維護");
//		list.add("房貸線上申請");
//		list.add("車貸線上申請");

//		list.add("圖書證申請");
//		list.add("救護證明申請");
//		list.add("身障停車證申請");
//		list.add("重大傷病婦女生活補助費");
//		list.add("就讀大專院校學生助學金");

//		list.add("一站式整合服務平台認證會員註冊服務");
//		list.add("勞資爭議調解申請");
//		list.add("土地徵收補償費保管款申請");
//		list.add("土地無套繪證明申請");
//		list.add("展翅高飛脫貧方案申請");
//		list.add("役男免役證明書申請");
//		list.add("替代役退役證明書補發");
//		list.add("水產動物增殖放流申請");
//		list.add("火災證明申請");
//		list.add("義務役在營服兵役證明書申請");
//		list.add("臺南市漁船以外船舶進出漁港申請");
//		list.add("車輛行車事故鑑定之覆議申請");
//		list.add("車輛行車事故鑑定申請");
//		list.add("違規罰鍰退款申請");

//		list.add("公益彩券經銷商資格第一階段預約申請");
//		list.add("勞資爭議調解申請");
//		list.add("土地無套繪證明申請");
//		list.add("地下水水權展限登記申請");
//		list.add("地面水水權展限登記申請");
//		list.add("寵物登記申請");
//		list.add("寵物除戶申請");
//		list.add("役男免役證明書");
//		list.add("徵收補償費保管款領款申請");
//		list.add("替代役退役證明書補發");
//		list.add("浪我回嘉認領養寵物申請");
//		list.add("火災調查申請(房屋或車輛)");
//		list.add("火災證明申請(房屋或車輛)");
//		list.add("特定寵物兔絕育申報");
//		list.add("義務役在營服兵役證明書");
//		list.add("臨時施工使用道路申請");
//		list.add("自願捺印指紋建檔申請");
//		list.add("舉辦活動使用道路申請");
//		list.add("農業設施動力用電證明申請");
//		list.add("雜草叢生致影響環境衛生通報");
//		list.add("飼主/寵物資料變更申請");

//		list.add("弱勢家庭幸福存款資產累積脫貧方案申請表");
//		list.add("敬老愛心卡補卡申請");
//		list.add("申請核(換、補)發身心障礙證明");
//		list.add("防走失手環申請");
//		list.add("敬老卡線上申請");
//		list.add("數位縣民註冊驗證服務申請");

		
//		list.add("一般道路車禍調解案件申請");
//		list.add("停車場月票退費申請");
//		list.add("新竹市數位市民整合服務線上申請");
//		list.add("網球證申辦");
//		list.add("羽球證申辦");
//		list.add("路邊停車退費申請");
//		list.add("非一般道路車禍調解案件申請");

//		list.add("啟用申請");
//		list.add("過戶申請");

		
//		list.add("交通部航港局");
//		list.add("台灣中油股份有限公司");
//		list.add("台灣自來水股份有限公司");
//		list.add("台灣電力股份有限公司");
//		list.add("嘉義縣");
//		list.add("國立故宮博物院");
//		list.add("宜蘭縣政府計畫處");
//		list.add("客家委員會客家文化發展中心");
//		list.add("文化部");
//		list.add("新竹市政府");
//		list.add("新竹縣");
//		list.add("桃園市");
//		list.add("澎湖縣");
//		list.add("科技部");
//		list.add("臺南市");
//		list.add("花蓮縣");
//		list.add("財政部財政資訊中心");
//		list.add("雲林縣");
//		list.add("雲林縣政府社會處");

//		list.add("圖書證申請");
//		list.add("房屋所有權人預約申請門牌證明書");
//		list.add("救護證明申請");
//		list.add("虎尾鎮都市計畫土地使用分區(或公共設施用地)證明申請書");
//		list.add("身障停車證申請");

//		list.add("國民年金所得未達一定標準減免申請書");
//		list.add("弱勢家庭幸福存款資產累積脫貧方案申請表");
//		list.add("敬老愛心卡補卡申請");
//		list.add("申請核(換、補)發身心障礙證明");
//		list.add("防走失手環申請");

//		list.add("一般道路車禍調解案件申請");
//		list.add("停車場月票退費申請");
//		list.add("新竹市數位市民整合服務線上申請");
//		list.add("網球證申辦");
//		list.add("羽球證申辦");
//		list.add("路邊停車退費申請");
//		list.add("非一般道路車禍調解案件申請");

//		list.add("執行救護服務證明申請");
//		list.add("執行救護服務證明申請書(配偶或直系親屬申請)");
//		list.add("火災證明申請書(房屋)");
//		list.add("火災證明申請書(車輛)");

//		list.add("一站式整合服務平台認證會員註冊服務");
//		list.add("勞資爭議調解申請");
//		list.add("國民兵身分證明書補發");
//		list.add("土地徵收補償費保管款申請");
//		list.add("土地無套繪證明申請");
//		list.add("展翅高飛脫貧方案申請");
//		list.add("役男免役證明書申請");
//		list.add("替代役退役證明書補發");
//		list.add("水產動物增殖放流申請");
//		list.add("火災證明申請");
//		list.add("義務役在營服兵役證明書申請");
//		list.add("臺南市漁船以外船舶進出漁港申請");
//		list.add("車輛行車事故鑑定之覆議申請");
//		list.add("車輛行車事故鑑定申請");
//		list.add("違規罰鍰退款申請");

//		list.add("公益彩券經銷商資格第一階段預約申請");
//		list.add("勞資爭議調解申請");
//		list.add("嘉義縣土地（或建築改良物）一併徵收申請");
//		list.add("國民兵身分證明書補發");
//		list.add("土地使用分區申請");
//		list.add("土地無套繪證明申請");
//		list.add("地下水水權展限登記申請");
//		list.add("地面水水權展限登記申請");
//		list.add("寵物登記申請");
//		list.add("寵物除戶申請");
//		list.add("役男免役證明書");
//		list.add("徵收補償費保管款領款申請");
//		list.add("替代役退役證明書補發");
//		list.add("水產動物增殖放流申請");
//		list.add("浪我回嘉認領養寵物申請");
//		list.add("火災調查申請(房屋或車輛)");
//		list.add("火災證明申請(房屋或車輛)");
//		list.add("特定寵物兔絕育申報");
//		list.add("第二類漁港非漁業用船隻進港申請");
//		list.add("義務役在營服兵役證明書");
//		list.add("臨時施工使用道路申請");
//		list.add("自願捺印指紋建檔申請");
//		list.add("舉辦活動使用道路申請");
//		list.add("農業設施動力用電證明申請");
//		list.add("雜草叢生致影響環境衛生通報");
//		list.add("非嘉義縣籍漁船寄港申請");
//		list.add("飼主/寵物資料變更申請");

//		
//		list.add("六歲以下幼童參加全民健康保險費自付額補助");
//		list.add("就讀大專院校學生助學金");
//		list.add("重大傷病婦女生活補助費");

		
//		list.add("中醫助孕養胎調理申請");
//		list.add("低收入戶及中低收入戶之體外受精(俗稱試管嬰兒)補助");
//		list.add("低收入戶房屋稅減免");
//		list.add("使用牌照稅身心障礙減免申請");
//		list.add("執行救護服務證明申請");
//		list.add("執行救護服務證明申請");
//		list.add("強化社會安全網急難紓困方案");
//		list.add("愛心卡、愛心陪伴卡申請");
//		list.add("手語翻譯及同步聽打服務申請表");
//		list.add("敬老卡或愛心卡申辦服務");
//		list.add("敬老卡申請");
//		list.add("敬老愛心卡申請");
//		list.add("申請使用牌照稅身心障礙減免申請");
//		list.add("申請領取住宅用火災警報器");
//		list.add("申辦減免房屋稅規劃申請");
//		list.add("育有2至4歲兒童育兒津貼申請");
//		list.add("育有未滿二歲兒童育兒津貼申請");
//		list.add("身心障礙停車識別證申請");
//		list.add("身心障礙者及老人預防走失溫馨手鍊或項鍊");
//		list.add("身心障礙者專用停車位識別證");
//		list.add("身心障礙者專用停車位識別證申請");
//		list.add("身心障礙者專用停車位識別證申請");
//		list.add("身心障礙者專用停車位識別證申請表");
//		list.add("身心障礙者日間及住宿式照顧費用補助");
//		list.add("身心障礙者日間照顧及住宿式照顧費用補助等候補助床位申請");
//		list.add("身心障礙者日間照顧及住宿式照顧轉安置申請");
//		list.add("身心障礙者生活補助");
//		list.add("身心障礙者租賃房屋租金補助");
//		list.add("身心障礙者職業重建服務申請轉介");
//		list.add("身心障礙者輔具補助費用申請（免評估輔具）");
//		list.add("重劃費用負擔證明申請書");
//		list.add("重陽節敬老禮金金融帳戶申請");
//		list.add("長照服務2.0線上申請");
//		list.add("預防走失-愛心手鍊申請");

//		list.add("全國高級中等學校具中低或低收入戶資格學生於線上申請學雜費減免服務");
//		list.add("斗六市立幼兒園新生入學登記作業(每年5月開放登記)");
//		list.add("農民學院課程報名服務");

//		list.add("信用卡申請補件服務");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請");
//		list.add("信用卡線上申請(註：一站式認證模式)");
//		list.add("信用卡線上申辦補件服務");
//		list.add("信用卡額度調整申請補件服務");
//		list.add("信貸線上申請");
//		list.add("信貸線上申請");
//		list.add("信貸線上申請");
//		list.add("信貸線上申請加值服務");
//		list.add("個人信貸線上申請");
//		list.add("卡友貸申請補件服務");
//		list.add("客戶資料維護");
//		list.add("就學貸款申請(上學期8月1日至9月底；下學期1月15至2月底)");
//		list.add("房貸線上申請");
//		list.add("房貸線上申請");
//		list.add("數位存款帳戶開戶");
//		list.add("永久額度調整");
//		list.add("線上申請補件服務");
//		list.add("貸款業務申請");
//		list.add("車貸線上申請");
		
		
//		list.add("一站式整合服務平台認證會員註冊服務");
//		list.add("一般道路車禍調解案件申請");
//		list.add("事業廢水處理專責單位或專責人員離職、異動及因故未能執行業務之報備申請");
//		list.add("停車場月票退費申請");
//		list.add("公用天然氣申請停用(限竹苗地區)");
//		list.add("公用天然氣申請復用(限竹苗地區)");
//		list.add("公用天然氣申請終止(限竹苗地區)");
//		list.add("公用天然氣申請變更通訊地址(限竹苗地區)");
//		list.add("公用天然氣申請過戶(限竹苗地區)");
//		list.add("公益彩券經銷商資格第一階段預約申請");
//		list.add("六歲以下幼童參加全民健康保險費自付額補助");
//		list.add("勞資爭議調解申請");
//		list.add("勞資爭議調解申請");
//		list.add("台灣電力公司-低壓用戶恢復部分契約容量");
//		list.add("台灣電力公司-低壓用戶暫停部分契約容量");
//		list.add("台灣電力公司-加裝防護線管申請");
//		list.add("台灣電力公司-取消住商型簡易時間電價");
//		list.add("台灣電力公司-復電申請");
//		list.add("台灣電力公司-暫停全部用電");
//		list.add("台灣電力公司-線路遷移申請");
//		list.add("台灣電力公司-表燈(或低壓電力)廢止用電");
//		list.add("台灣電力公司-表燈時間電價申請(含住商型簡易時間電價)");
//		list.add("台灣電力公司-過戶(變更用電戶名)");
//		list.add("啟用申請");
//		list.add("嘉義縣土地（或建築改良物）一併徵收申請");
//		list.add("國民兵身分證明書補發");
//		list.add("國民兵身分證明書補發");
//		list.add("國民年金所得未達一定標準減免申請書");
//		list.add("圖書證申請");
//		list.add("土地使用分區申請");
//		list.add("土地徵收補償費保管款申請");
//		list.add("土地無套繪證明申請");
//		list.add("土地無套繪證明申請");
//		list.add("地下水水權展限登記申請");
//		list.add("地政士開業(變更)登記申請");
//		list.add("地面水水權展限登記申請");
//		list.add("執行救護服務證明申請");
//		list.add("執行救護服務證明申請書(配偶或直系親屬申請)");
//		list.add("客發中心線上場地租借");
//		list.add("寵物登記申請");
//		list.add("寵物除戶申請");
//		list.add("就讀大專院校學生助學金");
//		list.add("展翅高飛脫貧方案申請");
//		list.add("弱勢家庭幸福存款資產累積脫貧方案申請表");
//		list.add("役男免役證明書");
//		list.add("役男免役證明書申請");
//		list.add("徵收補償費保管款領款申請");
//		list.add("房屋所有權人預約申請門牌證明書");
//		list.add("房屋稅稅籍相關證明書申請");
//		list.add("故宮-北部院區線上報名系統");
//		list.add("救護證明申請");
//		list.add("敬老卡線上申請");
//		list.add("敬老愛心卡補卡申請");
//		list.add("數位縣民註冊驗證服務申請");
//		list.add("文化部青年創作獎勵線上申辦(每年10月)");
//		list.add("新竹市數位市民整合服務線上申請");
//		list.add("替代役退役證明書補發");
//		list.add("替代役退役證明書補發");
//		list.add("核發社會工作師執業執照");
//		list.add("毒性化學物質專業技術管理人員設置及變更");
//		list.add("水產動物增殖放流申請");
//		list.add("水產動物增殖放流申請");
//		list.add("浪我回嘉認領養寵物申請");
//		list.add("火災調查申請(房屋或車輛)");
//		list.add("火災證明申請");
//		list.add("火災證明申請(房屋或車輛)");
//		list.add("火災證明申請書(房屋)");
//		list.add("火災證明申請書(車輛)");
//		list.add("特定寵物兔絕育申報");
//		list.add("申請核(換、補)發身心障礙證明");
//		list.add("科技部學術研發服務網研究人員註冊服務");
//		list.add("第二類漁港非漁業用船隻進港申請");
//		list.add("網球證申辦");
//		list.add("義務役在營服兵役證明書");
//		list.add("義務役在營服兵役證明書申請");
//		list.add("羽球證申辦");
//		list.add("臨時施工使用道路申請");
//		list.add("自願捺印指紋建檔申請");
//		list.add("臺南市漁船以外船舶進出漁港申請");
//		list.add("舉辦活動使用道路申請");
//		list.add("船員卸任職申請");
//		list.add("虎尾鎮都市計畫土地使用分區(或公共設施用地)證明申請書");
//		list.add("路邊停車退費申請");
//		list.add("身障停車證申請");
//		list.add("車輛行車事故鑑定之覆議申請");
//		list.add("車輛行車事故鑑定申請");
//		list.add("農業設施動力用電證明申請");
//		list.add("過戶申請");
//		list.add("違規罰鍰退款申請");
//		list.add("重大傷病婦女生活補助費");
//		list.add("門牌補發申請表");
//		list.add("防走失手環申請");
//		list.add("雜草叢生致影響環境衛生通報");
//		list.add("非一般道路車禍調解案件申請");
//		list.add("非嘉義縣籍漁船寄港申請");
//		list.add("飼主/寵物資料變更申請");

		
		tcomparator comparator = new tcomparator();
		Collections.sort(list, comparator);
		for(String s:list) {
			System.out.println(s);
		}
		
	}
	
	public static class tcomparator implements Comparator<String> {
		public int compare(String obj1, String obj2) {
			System.out.println(obj1.substring(0, 1)+" - "+ map.get(obj1.substring(0, 1)));
			System.out.println(obj2.substring(0, 1)+" - "+ map.get(obj2.substring(0, 1)));
			int value1 = Integer.valueOf(map.get(obj1.substring(0, 1))).compareTo((Integer.valueOf(map.get(obj2.substring(0, 1)))));
			return value1;
		}
	}
}
