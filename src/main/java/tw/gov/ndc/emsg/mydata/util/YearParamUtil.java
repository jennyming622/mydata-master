package tw.gov.ndc.emsg.mydata.util;

import org.springframework.util.CollectionUtils;
import tw.gov.ndc.emsg.mydata.entity.YearParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class YearParamUtil {

    /**
     * 顯示近六年
     * API.HnEOpr9EoM 申請核發地價稅繳納證明
     */
    private final static List<String> YEAR_1 = Arrays.asList("API.HnEOpr9EoM");

    /**
     * 顯示近五年，每年4/28為切點，例如:108/4/28後可選擇103 104 105 106 107，108(不含當年度往前推五年) 4/28前可選擇103 104 105 106 107，最多五年(不含近兩年往後推五年)
     * API.syWqjr4flJ 個人所得資料
     */
    private final static List<String> YEAR_2 = Arrays.asList("API.syWqjr4flJ", "API.mBqP4awHJY");

    /**
     * 起訖年度(民國年):每年1/1 - 6/30(含)顯示當年度及往前4年105 106 107 108 109，7/1 - 12/31 顯示當年度和下年度及往前4年104 105 106 107 108 109 110
     * API.LWUBwspg0n 申請核發房屋稅繳納證明
     */
    private final static List<String> YEAR_3 = Arrays.asList("API.LWUBwspg0n");

    /**
     * 當年度-2年;例如今年109年顯示103、104、105、106、107年
     * API.TeV2Md7SIx 綜合所得稅納稅證明書
     * API.YSclUkv7Lj 綜合所得稅稅籍資料
     */
    private final static List<String> YEAR_4 = Arrays.asList("API.TeV2Md7SIx", "API.YSclUkv7Lj");

    /**
     * Default 當年及往前四年
     * API.wH2r0nBb3O 申請核發使用牌照稅繳納證明
     */

    public static YearParam getYearParam(String resourceId) {
        Date now = new Date();

        if(YEAR_1.contains(resourceId)) { // 含當年及前五年
            return new YearParam(0, 6);
        } else if(YEAR_2.contains(resourceId)) {
            if(CDateUtil.getAssignDateZero(4, 28).before(now)) {
               return new YearParam(-1, 5); // 去年及往前四年
            } else {
               return new YearParam(-2, 5); // 前年及往前四年
            }
        } else if(YEAR_3.contains(resourceId)) {
            if(CDateUtil.getAssignDateZero(7, 1).before(now)) {
               return new YearParam(1, 6); // 下一年度，當年，及前四年
            } else {
                return new YearParam(0, 5); //當年，及前四年
            }
        } else if(YEAR_4.contains(resourceId)) {
            return new YearParam(-2, 5); // 前年及往前四年
        }

        return new YearParam(0, 5); // 當年往前推五年
    }


//	if(pr.getResourceId().equalsIgnoreCase("API.wH2r0nBb3O")) {
//        p.setYearParam(new YearParam(0, 5)); // 當年往前推五年
//    } else if(pr.getResourceId().equalsIgnoreCase("API.HnEOpr9EoM")) {
//        p.setYearParam(new YearParam(0, 6)); // 當年往前推六年
//    } else if(pr.getResourceId().equalsIgnoreCase("API.syWqjr4flJ")) {
//        if(CDateUtil.getAssignDateZero(4, 28).before(now)) {
//            p.setYearParam(new YearParam(-1, 5)); // 去年往前推五年
//        } else {
//            p.setYearParam(new YearParam(-2, 5)); // 前年往前推五年
//        }
//    } else if(pr.getResourceId().equalsIgnoreCase("API.LWUBwspg0n")) {
//        if(CDateUtil.getAssignDateZero(7, 1).before(now)) {
//            p.setYearParam(new YearParam(1, 6)); // 後一年往前推六年
//        } else {
//            p.setYearParam(new YearParam(0, 5)); // 當年往前推五年
//        }
//    } else if(pr.getResourceId().equalsIgnoreCase("API.TeV2Md7SIx")||pr.getResourceId().equalsIgnoreCase("API.YSclUkv7Lj")) {
//        p.setYearParam(new YearParam(-2, 5)); // 前年往前推五年
//    } else {
//        p.setYearParam(new YearParam(0, 5)); // 當年往前推五年
//    }
}
