package dto.user;

import constant.Constant;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.UserServiceImpl;
import util.MsgUtil;

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
			errors.add(MsgUtil.getValidationError(
					Constant.ITEM_USER_NAME,
					Constant.MSG_E006,
					Constant.ITEM_NAME_USER_NAME,
					userName));
		}

		if (!password.equals(passwordConfirm)) {
			errors.add(MsgUtil.getValidationError(
					Constant.ITEM_PASSWORD,
					Constant.MSG_E013,
					Constant.ITEM_NAME_PASSWORD,
					Constant.ITEM_NAME_PASS_CONF));
		}

		return errors.isEmpty() ? null : errors;
	}
}
