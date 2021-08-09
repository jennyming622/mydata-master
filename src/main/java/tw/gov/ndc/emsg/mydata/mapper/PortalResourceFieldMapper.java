package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceField;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceFieldExample;

public interface PortalResourceFieldMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(PortalResourceField record);

    int insertSelective(PortalResourceField record);

    List<PortalResourceField> selectByExample(Map<String,Object> param);

    PortalResourceField selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalResourceField record);

    int updateByPrimaryKey(PortalResourceField record);
}