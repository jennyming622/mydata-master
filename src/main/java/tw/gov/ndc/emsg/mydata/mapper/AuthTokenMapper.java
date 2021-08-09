package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.AuthToken;

public interface AuthTokenMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(AuthToken record);

    int insertSelective(AuthToken record);

    List<AuthToken> selectByExample(Map<String,Object> param);

    AuthToken selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AuthToken record);

    int updateByPrimaryKey(AuthToken record);
}