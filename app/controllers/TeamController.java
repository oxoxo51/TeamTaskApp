package controllers;

import com.google.inject.Inject;
import dto.team.CreateTeamDto;
import models.Team;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import services.TeamService;
import views.html.team;
import views.html.teamList;

import java.util.List;

/**
 * Created on 2016/05/10.
 */
public class TeamController extends Apps {
	@Inject
	TeamService service;

	/**
	 * チームリスト表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayTeamList() {
		Logger.info("TeamController#displayTeamList");

		// セッションのチームをクリアする
		session().remove("teamName");

		// ログイン状態確認の上ユーザーに紐付くチームリストを表示
		return getTeamList();
	}

	/**
	 * チーム新規登録画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayCreateTeam() {
		Logger.info("TeamController#displayCreateTeam");

		Form<CreateTeamDto> createTeamDtoForm = Form.form(CreateTeamDto.class);
		return ok(team.render("CREATE", createTeamDtoForm));
	}

	/**
	 * チーム新規登録.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result create() {
		Logger.info("TeamController#create");

		Form<CreateTeamDto> createTeamDtoForm = Form.form(CreateTeamDto.class).bindFromRequest();
		if (!createTeamDtoForm.hasErrors()) {
			CreateTeamDto dto = createTeamDtoForm.get();
			service.create(dto);
			String msg = "登録しました。";
			msg += " teamName: " + dto.getTeamName();
			flash("success", msg);
			// チームリストを表示
			return redirect(routes.TeamController.displayTeamList());
		} else {
			// TODO
			flash("error", "登録できません。");
			return badRequest(team.render("CREATE", createTeamDtoForm));
		}
	}

	/**
	 * セッションのログインユーザーに紐付くチームリストを返却する.
	 * @return
	 */
	private Result getTeamList() {
		String userName = session().get("userName");
		List<Team> teams = service.findTeamListByUserName(userName);
		return ok(teamList.render(userName, teams));
	}

}
