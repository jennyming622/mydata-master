/**
 * 
 */
package tw.gov.ndc.emsg.mydata.auth;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.riease.common.sysinit.security.jcaptcha.JcaptchaForm;
import com.riease.common.validator.ValidateFirst;
import com.riease.common.validator.ValidateSecond;
import com.riease.common.validator.ValidateThird;

/**
 * 登入頁表單物件
 * @author wesleyzhuang
 *
 */
@GroupSequence({ValidateFirst.class, ValidateSecond.class, ValidateThird.class, SigninForm.class})
public class SigninForm extends JcaptchaForm {

	@NotEmpty(message="{NotEmpty.account}", groups={ValidateFirst.class})
	@Size(min=1,max=100,message="{Size.account}", groups={ValidateSecond.class})
//	@Pattern(regexp="^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$", 
//			 message="{Pattern.account}", groups={ValidateThird.class})
	private String account;
	

	@NotEmpty(message="{NotEmpty.passwd}", groups={ValidateFirst.class})
	@Size(min=8,max=100,message="{Size.passwd}", groups={ValidateSecond.class})
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
	 		message="{Pattern.passwd}", groups={ValidateThird.class})
	private String passwd;

	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
}
