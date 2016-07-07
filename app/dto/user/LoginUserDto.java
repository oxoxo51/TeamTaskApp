package dto.user;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/06/06.
 */
public class LoginUserDto {

	/**
	 * ユーザー名.
	 */
	@Constraints.Required
	private String userName;

	/**
	 * パスワード.
	 */
	@Constraints.Required
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * バリデーション.
	 * @return
	 */
	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>();

		UserServiceImpl service = new UserServiceImpl();

		if(service.findUser(userName, password) == null || service.findUser(userName, password).size() == 0) {
			// validationErrorを空で設定し、エラーはflashメッセージで設定
			errors.add(new ValidationError("",""));
		}

		return errors.isEmpty() ? null : errors;
	}
}
