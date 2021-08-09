package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.TfidoRecord;

public interface TfidoRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TfidoRecord record);

    int insertSelective(TfidoRecord record);

    List<TfidoRecord> selectByExample(Map<String,Object> param);

    TfidoRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TfidoRecord record);

    int updateByPrimaryKey(TfidoRecord record);
}