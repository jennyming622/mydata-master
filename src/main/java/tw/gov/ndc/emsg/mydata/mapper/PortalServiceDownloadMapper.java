package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceDownloadExample;

public interface PortalServiceDownloadMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(PortalServiceDownload record);

	int insertSelective(PortalServiceDownload record);

	List<PortalServiceDownload> selectByExample(Map<String,Object> param);

	PortalServiceDownload selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PortalServiceDownload record);

	int updateByPrimaryKey(PortalServiceDownload record);
}