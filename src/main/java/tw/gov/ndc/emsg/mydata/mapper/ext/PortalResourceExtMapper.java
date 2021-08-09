package tw.gov.ndc.emsg.mydata.mapper.ext;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import tw.gov.ndc.emsg.mydata.entity.PortalBoxExt;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxLogExt;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExt;

public interface PortalResourceExtMapper {
	public List<PortalResource> selectByExampleOrderByOid();
	public List<PortalResource> selectByCateIdOrderByOid(@Param("cateId") Integer cateId);
	public List<PortalBoxLogExt> selectMyBoxLogByAccount(@Param("account")String account);
	public List<PortalBoxExt> selectMyBoxByAccount(@Param("account") String account);
	public List<PortalBoxExt> selectMyBoxForCounterByAccount(@Param("account")String account);
	public List<PortalBoxExt> selectMyBoxForCounter(Map<String, Object> param);
	public List<PortalBoxExt> selectMyBoxForCounterByPsdId(@Param("psdId")Integer psdId);
	public List<PortalBoxExt> selectMyBoxByAccountAndPrId(@Param("account") String account,@Param("prId") Integer prId);
	public PortalBoxExt selectMyBoxById(@Param("id") Integer id);
	List<PortalResourceExt> selectPortalResourceWithCateName();
	List<PortalResourceExt> selectPrByPs(@Param("psId") Integer psId);
	List<PortalResourceExt> selectPrByPsCounter(@Param("psId") Integer psId);
}
