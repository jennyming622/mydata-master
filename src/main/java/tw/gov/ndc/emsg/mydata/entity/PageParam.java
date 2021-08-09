package tw.gov.ndc.emsg.mydata.entity;

import java.util.Date;

public class PageParam {
    private Integer pageSize;
    private Integer page;
    private Integer total;
    private Date searchTime;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Date getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Date searchTime) {
        this.searchTime = searchTime;
    }

    public Integer getTotalPage() {
        int size = this.total / this.pageSize;
        int remain = this.total % this.pageSize;
        if(remain > 0) {
            return size + 1;
        }
        return size;
    }
}
