package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import tw.gov.ndc.emsg.mydata.entity.ChineseCharacters;

public interface ChineseCharactersMapper {
	List<ChineseCharacters> selectAll();
    List<ChineseCharacters> selectByWord(@Param("word") String word);
}