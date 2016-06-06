package controllers;

import com.google.inject.Inject;
import dto.team.CreateTeamDto;
import models.Team;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
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
	public Result displayTeamList() {
		Logger.info("TeamController#displayTeamList");

		// ログイン状態確認の上ユーザーに紐付くチームリストを表示
		// TODO 今は暫定でユーザーの1件目で取得
		// TODO 取得部分はサービスに寄せてここでは呼出のみにすべきか
		User user = User.find.all().get(0);
		List<Team> teams = service.findTeamListByUserName(user.userName);
		return ok(teamList.render(user.userName, teams));
	}

	/**
	 * チーム新規登録画面表示.
	 * @return
	 */
	public Result displayCreateTeam() {
		Logger.info("TeamController#displayCreateTeam");

		Form<CreateTeamDto> createTeamDtoForm = Form.form(CreateTeamDto.class);
		return ok(team.render("CREATE", createTeamDtoForm));
	}

	/**
	 * チーム新規登録.
	 * @return
	 */
	public Result create() {
		Logger.info("TeamController#create");

		Form<CreateTeamDto> createTeamDtoForm = Form.form(CreateTeamDto.class).bindFromRequest();
		if (!createTeamDtoForm.hasErrors()) {
			CreateTeamDto dto = createTeamDtoForm.get();
			service.create(dto);
			String msg = "登録しました。";
			msg += " teamName: " + dto.teamName;
			flash("success", msg);
			// TODO 実際にはログインユーザからチーム一覧を取得するが、暫定でユーザの1件目から取得
			User user = User.find.all().get(0);
			List<Team> teams = service.findTeamListByUserName(user.userName);
			return ok(teamList.render(user.userName, teams));
		} else {
			// TODO
			flash("error", "登録できません。");
			return badRequest(team.render("CREATE", createTeamDtoForm));
		}
	}



}
