package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.PortalServiceStageIp;

public interface PortalServiceStageIpMapper {

    int deleteByExample(Map<String,Object> param);

    int deleteByPrimaryKey(Integer id);

    int insert(PortalServiceStageIp record);

    int insertSelective(PortalServiceStageIp record);

    List<PortalServiceStageIp> selectByExample(Map<String,Object> param);

    PortalServiceStageIp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalServiceStageIp record);

    int updateByPrimaryKey(PortalServiceStageIp record);
}