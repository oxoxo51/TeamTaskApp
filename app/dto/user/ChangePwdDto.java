package dto.user;

import constant.Constant;
import models.User;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.UserServiceImpl;
import util.MsgUtil;

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

		List<User> userList = service.findUserByName(userName);
		String password = userList.get(0).password;
		// 現パスワードチェック
		if (!password.equals(passwordAsIs)) {
			errors.add(MsgUtil.getValidationError(Constant.ITEM_PASSWORD_AS_IS, Constant.MSG_E012));
		}
		// 新パスワード-新パスワード確認用一致チェック
		if (!passwordToBe.equals(passwordToBeConfirm)) {
			errors.add(MsgUtil.getValidationError(
					Constant.ITEM_PASSWORD_TO_BE,
					Constant.MSG_E013,
					Constant.ITEM_NAME_PASSWORD,
					Constant.ITEM_NAME_PASS_CONF));
		}

		return errors.isEmpty() ? null : errors;
	}
}
