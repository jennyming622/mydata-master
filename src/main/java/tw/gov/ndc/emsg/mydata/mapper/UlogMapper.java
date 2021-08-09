package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.Ulog;
import tw.gov.ndc.emsg.mydata.entity.UlogExample;

public interface UlogMapper {

	int deleteByPrimaryKey(Integer seq);

	int insert(Ulog record);

	int insertSelective(Ulog record);

	Ulog selectByPrimaryKey(Integer seq);

	int updateByPrimaryKeySelective(Ulog record);

	int updateByPrimaryKey(Ulog record);
}