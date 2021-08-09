package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.MydataNewsCategory;
import tw.gov.ndc.emsg.mydata.entity.MydataNewsCategoryExample;
import tw.gov.ndc.emsg.mydata.entity.ext.MyDataNewsCategoryExt;

public interface MydataNewsCategoryMapper {
    List<MyDataNewsCategoryExt> selectCategoryWithNews();
}
