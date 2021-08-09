package tw.gov.ndc.emsg.mydata.web;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.MydataNews;
import tw.gov.ndc.emsg.mydata.entity.MydataNewsCategory;
import tw.gov.ndc.emsg.mydata.entity.MydataNewsCategoryExample;
import tw.gov.ndc.emsg.mydata.entity.MydataNewsExample;
import tw.gov.ndc.emsg.mydata.entity.ext.MyDataNewsCategoryExt;
import tw.gov.ndc.emsg.mydata.mapper.MydataNewsCategoryMapper;
import tw.gov.ndc.emsg.mydata.mapper.MydataNewsMapper;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/news")
public class NewsController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MydataNewsMapper mydataNewsMapper;
    @Autowired
    MydataNewsCategoryMapper categoryMapper;



    @GetMapping("/list")
    public String listNews(HttpServletRequest request, HttpServletResponse response, ModelMap model){


        List<MyDataNewsCategoryExt> extList = categoryMapper.selectCategoryWithNews();

        for(MyDataNewsCategoryExt ext : extList){
            for(MydataNews mydataNews : ext.getNewsList()){
                if(mydataNews.getPublishDate() != null){
                    Date dt = mydataNews.getPublishDate();
                    mydataNews.setPublishDate(DateUtils.addYears(dt,-1911));
                }
            }
        }

        model.put("extList",extList);

        // 最新消息類別
        List<MyDataNewsCategoryExt> categories =  categoryMapper.selectCategoryWithNews();
        model.put("categories",categories);

        return "news-list";
    }


    @GetMapping("/detail/{newsUid}")
    public String newsDetail(
            @PathVariable("newsUid") String newsUid,
            HttpServletRequest request,
            HttpServletResponse response,
            ModelMap model){

        logger.debug("news detail... {}" , newsUid);

        MydataNews news = mydataNewsMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(newsUid));
        if(news.getPublishDate()!=null){
            Date dt = news.getPublishDate();
            news.setPublishDate(DateUtils.addYears(dt,-1911));
        }
        model.put("news" , news);
        return "news-detail";
    }



}
