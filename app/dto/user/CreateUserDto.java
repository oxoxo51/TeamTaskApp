package dto.user;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/05/08.
 */
public class CreateUserDto {

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

	/**
	 * パスワード（確認用）
	 */
	@Constraints.Required
	private String passwordConfirm;

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

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	/**
	 * バリデーション.
	 * @return
	 */
	public List<ValidationError> validate() {
		UserServiceImpl service = new UserServiceImpl();

		List<ValidationError> errors = new ArrayList<>();

		if(service.findUserByName(userName) != null && service.findUserByName(userName).size() != 0) {
			errors.add(new ValidationError("userName", "ユーザー名:" + userName + " は既に使用されています。"));
		}

		if (!password.equals(passwordConfirm)) {
			errors.add(new ValidationError("password", "パスワードとパスワード（確認）が一致しません。"));
		}

		return errors.isEmpty() ? null : errors;
	}
}
