package controllers;

import com.google.inject.Inject;
import constant.Constant;
import dto.user.ChangePwdDto;
import dto.user.CreateUserDto;
import models.User;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import services.UserService;
import views.html.changePwd;
import views.html.user;

/**
 * Created on 2016/05/08.
 */
public class UserController extends Apps {

	@Inject
	UserService service;

	/**
	 * ユーザー新規登録画面表示.
	 * @return
	 */
	public Result displayCreateUser() {
		Form<CreateUserDto> createUserDtoForm = Form.form(CreateUserDto.class);
		return ok(user.render(Constant.MODE_CREATE, createUserDtoForm));
	}

	/**
	 * ユーザー新規登録.
	 * @return
	 */
	public Result create() {
		Form<CreateUserDto> createUserDtoForm = Form.form(CreateUserDto.class).bindFromRequest();
		if (!createUserDtoForm.hasErrors()) {
			CreateUserDto dto = createUserDtoForm.get();
			User user = service.create(dto);
			flashSuccess(Constant.MSG_I003);
			// ログイン済みの状態にしてチーム一覧に遷移
			super.login(user);
			return redirect(routes.TeamController.displayTeamList());
		} else {
			flashError(Constant.MSG_E003);
			return badRequest(user.render(Constant.MODE_CREATE, createUserDtoForm));
		}
	}

	/**
	 * パスワード変更画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayChangePwd() {
		setSessionUrl(routes.UserController.displayChangePwd().url());
		ChangePwdDto dto = new ChangePwdDto();
		dto.setUserName(getLoginUserName());
		Form<ChangePwdDto> changePwdDtoForm = Form.form(ChangePwdDto.class).fill(dto);
		return ok(changePwd.render(changePwdDtoForm));
	}

	/**
	 * パスワード変更.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result updatePwd() {
		Form<ChangePwdDto> changePwdDtoForm = Form.form(ChangePwdDto.class).bindFromRequest();
		if (!changePwdDtoForm.hasErrors()) {
			ChangePwdDto dto = changePwdDtoForm.get();
			service.changePwd(dto);
			// TODO redirectを2重にしているためflashメッセージが表示されない
			flashSuccess(Constant.MSG_I007);
			// セッションを維持したままルートにリダイレクトする。
			return redirect(routes.Apps.index());
		} else {
			flashError(Constant.MSG_E003);
			return badRequest(changePwd.render(changePwdDtoForm));
		}
	}
}
