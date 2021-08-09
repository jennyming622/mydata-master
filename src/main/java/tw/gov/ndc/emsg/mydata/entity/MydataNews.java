package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class MydataNews implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mydata_news.news_uid
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    private String newsUid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mydata_news.title
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    private String title;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mydata_news.content
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    private String content;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mydata_news.publish_date
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    private Date publishDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mydata_news.news_cate
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    private Integer newsCate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table mydata_news
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mydata_news.news_uid
     *
     * @return the value of mydata_news.news_uid
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public String getNewsUid() {
        return newsUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mydata_news.news_uid
     *
     * @param newsUid the value for mydata_news.news_uid
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public void setNewsUid(String newsUid) {
        this.newsUid = newsUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mydata_news.title
     *
     * @return the value of mydata_news.title
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mydata_news.title
     *
     * @param title the value for mydata_news.title
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mydata_news.content
     *
     * @return the value of mydata_news.content
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public String getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mydata_news.content
     *
     * @param content the value for mydata_news.content
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mydata_news.publish_date
     *
     * @return the value of mydata_news.publish_date
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mydata_news.publish_date
     *
     * @param publishDate the value for mydata_news.publish_date
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mydata_news.news_cate
     *
     * @return the value of mydata_news.news_cate
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public Integer getNewsCate() {
        return newsCate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mydata_news.news_cate
     *
     * @param newsCate the value for mydata_news.news_cate
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    public void setNewsCate(Integer newsCate) {
        this.newsCate = newsCate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mydata_news
     *
     * @mbg.generated Mon Jul 13 14:40:38 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", newsUid=").append(newsUid);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", publishDate=").append(publishDate);
        sb.append(", newsCate=").append(newsCate);
        sb.append("]");
        return sb.toString();
    }
}