package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownloadExample;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownload;

public interface PortalResourceDownloadMapper {

	long countByExample(Map<String,Object> param);

	int deleteByPrimaryKey(String downloadSn);

	int insert(PortalResourceDownload record);

	int insertSelective(PortalResourceDownload record);

	List<PortalResourceDownload> selectByExample(Map<String,Object> param);

	PortalResourceDownload selectByPrimaryKey(String downloadSn);

	int updateStatByExample(Map<String,Object> param);

	int updateByPrimaryKeySelective(PortalResourceDownload record);

	int updateByPrimaryKey(PortalResourceDownload record);
}