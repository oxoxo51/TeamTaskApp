package dto.user;

import models.User;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/06/20.
 */
public class ChangePwdDto {

	/**
	 * ユーザー名.
	 */
	private String userName;

	/**
	 * 現パスワード.
	 */
	@Constraints.Required
	private String passwordAsIs;

	/**
	 * 新パスワード.
	 */
	@Constraints.Required
	private String passwordToBe;

	/**
	 * 新パスワード（確認用）
	 */
	@Constraints.Required
	private String passwordToBeConfirm;


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordAsIs() {
		return passwordAsIs;
	}

	public void setPasswordAsIs(String passwordAsIs) {
		this.passwordAsIs = passwordAsIs;
	}

	public String getPasswordToBe() {
		return passwordToBe;
	}

	public void setPasswordToBe(String passwordToBe) {
		this.passwordToBe = passwordToBe;
	}

	public String getPasswordToBeConfirm() {
		return passwordToBeConfirm;
	}

	public void setPasswordToBeConfirm(String passwordToBeConfirm) {
		this.passwordToBeConfirm = passwordToBeConfirm;
	}


	/**
	 * バリデーション.
	 * @return
	 */
	public List<ValidationError> validate() {
		UserServiceImpl service = new UserServiceImpl();
		List<ValidationError> errors = new ArrayList<>();

		List<User> userList = User.find.where().eq("userName", userName).findList();
		String password = userList.get(0).password;
		// 現パスワードチェック
		if (!password.equals(passwordAsIs)) {
			errors.add(new ValidationError("passwordAsIs", "パスワードが正しくありません。"));
		}
		// 新パスワード-新パスワード確認用一致チェック
		if (!passwordToBe.equals(passwordToBeConfirm)) {
			errors.add(new ValidationError("password", "新パスワードと新パスワード（確認）が一致しません。"));
		}

		return errors.isEmpty() ? null : errors;
	}
}
