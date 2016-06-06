package dto.user;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

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
	public String userName;

	/**
	 * パスワード.
	 */
	@Constraints.Required
	public String password;

	/**
	 * パスワード（確認用）
	 */
	@Constraints.Required
	public String passwordConfirm;

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
		List<ValidationError> errors = new ArrayList<>();

		if (!password.equals(passwordConfirm)) {
			errors.add(new ValidationError("password", "パスワードとパスワード（確認）が一致しません。"));
		}

		return errors.isEmpty() ? null : errors;
	}
}
