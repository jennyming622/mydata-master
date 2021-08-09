package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.Contactus;
import tw.gov.ndc.emsg.mydata.entity.ContactusExample;

public interface ContactusMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Contactus record);

    int insertSelective(Contactus record);

    Contactus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Contactus record);

    int updateByPrimaryKey(Contactus record);
}