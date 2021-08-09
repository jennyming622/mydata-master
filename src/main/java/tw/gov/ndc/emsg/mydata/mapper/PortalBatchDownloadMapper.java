package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalBatchDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalBatchDownloadExample;

public interface PortalBatchDownloadMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(PortalBatchDownload record);

	int insertSelective(PortalBatchDownload record);

	PortalBatchDownload selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PortalBatchDownload record);
	
	int updateByPrimaryKey(PortalBatchDownload record);
}