package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxLog;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxLogExample;

public interface PortalBoxLogMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(PortalBoxLog record);

	int insertSelective(PortalBoxLog record);

	PortalBoxLog selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PortalBoxLog record);

	int updateByPrimaryKey(PortalBoxLog record);
}