package tw.gov.ndc.emsg.mydata.mapper;

import tw.gov.ndc.emsg.mydata.entity.PortalServiceDownloadSub;

public interface PortalServiceDownloadSubMapper {

	PortalServiceDownloadSub selectByPrimaryKey(Integer psdId);

	int insert(PortalServiceDownloadSub record);
}