/**
 * 
 */
package tw.gov.ndc.emsg.mydata.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.riease.common.enums.EnableStatus;

/**
 * 配合SpringSecurity機制，用以查詢shop_user的登入驗證及授權。
 * @author wesleyzhuang
 *
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
	
	/**
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserDetails user = null;
		boolean enabled = true;
		boolean accountNotExpired = true;
		boolean credentialsNotExpired = true;
		boolean accountNotLocked = true;
		List<GrantedAuthority> gauth = new ArrayList<>();
				
		user = new User("none","none",
				enabled,accountNotExpired,credentialsNotExpired,accountNotLocked,
				gauth);
		return user;
	}

}
