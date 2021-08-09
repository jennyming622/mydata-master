package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.MydataNews;
import tw.gov.ndc.emsg.mydata.entity.MydataNewsExample;

public interface MydataNewsMapper {

    MydataNews selectByPrimaryKey(String newsUid);

    List<MydataNews> selectByCateId(Integer id);
}
