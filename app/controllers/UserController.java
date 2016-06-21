package controllers;

import com.google.inject.Inject;
import dto.user.ChangePwdDto;
import dto.user.CreateUserDto;
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
		return ok(user.render("CREATE", createUserDtoForm));
	}

	/**
	 * ユーザー新規登録.
	 * @return
	 */
	public Result create() {
		Form<CreateUserDto> createUserDtoForm = Form.form(CreateUserDto.class).bindFromRequest();
		if (!createUserDtoForm.hasErrors()) {
			CreateUserDto dto = createUserDtoForm.get();
			service.create(dto);
			String msg = "登録しました。 userName: " + dto.getUserName();
			flash("success", msg);
			// ログイン済みの状態にしてチーム一覧に遷移
			session().clear();
			session("userName", dto.getUserName());
			return redirect(routes.TeamController.displayTeamList());
		} else {
			flash("error", "登録できません。エラーの内容を確認してください。");
			return badRequest(user.render("CREATE", createUserDtoForm));
		}
	}

	/**
	 * パスワード変更画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayChangePwd() {
		ChangePwdDto dto = new ChangePwdDto();
		dto.setUserName(session("userName"));
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
			flash("success", "パスワードを変更しました。");
			// セッションを維持したままルートにリダイレクトする。
			return redirect(routes.Apps.index());
		} else {
			flash("error", "登録できません。エラーの内容を確認してください。");
			return badRequest(changePwd.render(changePwdDtoForm));
		}
	}
}
