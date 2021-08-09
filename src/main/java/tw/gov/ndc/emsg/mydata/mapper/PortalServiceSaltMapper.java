package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.PortalServiceSalt;

public interface PortalServiceSaltMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(PortalServiceSalt record);

    int insertSelective(PortalServiceSalt record);

    List<PortalServiceSalt> selectByExample(Map<String,Object> param);

    PortalServiceSalt selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalServiceSalt record);

    int updateByPrimaryKey(PortalServiceSalt record);
}