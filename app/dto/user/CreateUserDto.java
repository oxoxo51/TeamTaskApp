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
	private String passwordConfirm;

	private String getUserName() {
		return userName;
	}

	private void setUserName(String userName) {
		this.userName = userName;
	}

	private String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	private String getPasswordConfirm() {
		return passwordConfirm;
	}

	private void setPasswordConfirm(String passwordConfirm) {
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
