/**
 * 
 */
package tw.gov.ndc.emsg.dbencrypt;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author wesleyzhuang
 *
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(String.class)
public class EncryptTypeHandler extends BaseTypeHandler<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(EncryptTypeHandler.class);

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		if(jdbcType == null) jdbcType = JdbcType.VARCHAR;
		//encrypt
		String encrypted = null;
		try {
			encrypted = DbEncryptUtilsV2.encryptAES(parameter);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		ps.setObject(i, encrypted, jdbcType.TYPE_CODE);
	}

	@Override
	public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return decrypt((String)rs.getObject(columnName));
	}

	@Override
	public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return decrypt((String)rs.getObject(columnIndex));
	}

	@Override
	public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return decrypt((String)cs.getObject(columnIndex));
	}


	private String decrypt(String encrypted) {
		String result = null;
		try {
			result = DbEncryptUtilsV2.decryptAES(encrypted);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

}
