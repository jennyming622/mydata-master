package tw.gov.ndc.emsg.mydata.entity.ext;

import tw.gov.ndc.emsg.mydata.entity.MydataNews;
import tw.gov.ndc.emsg.mydata.entity.MydataNewsCategory;

import java.util.List;

public class MyDataNewsCategoryExt extends MydataNewsCategory {

    private List<MydataNews> newsList;


    public List<MydataNews> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<MydataNews> newsList) {
        this.newsList = newsList;
    }
}
