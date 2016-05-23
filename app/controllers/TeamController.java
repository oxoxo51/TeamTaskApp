package controllers;

import com.google.inject.Inject;
import dto.team.CreateTeamDto;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.mvc.Result;
import services.TeamService;
import views.html.index;
import views.html.team;
import views.html.teamList;

/**
 * Created on 2016/05/10.
 */
public class TeamController extends Apps {
	private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

	@Inject
	TeamService service;

	public Result displayTeamList() {
		logger.info("TeamController#displayTeamList");

		// ログイン状態確認の上ユーザーに紐付くチームリストを表示
		// TODO 今は暫定でユーザーの1件目で取得
		User user = User.find.all().get(0);
		return ok(teamList.render(user.userName));
	}

	/**
	 * チーム新規登録画面表示.
	 * @return
	 */
	public Result displayCreateTeam() {
		logger.info("TeamController#displayCreateTeam");

		Form<CreateTeamDto> createTeamDtoForm = Form.form(CreateTeamDto.class);
		return ok(team.render("create", createTeamDtoForm));
	}

	public Result create() {
		logger.info("TeamController#create");

		Form<CreateTeamDto> createTeamDtoForm = Form.form(CreateTeamDto.class).bindFromRequest();
		if (!createTeamDtoForm.hasErrors()) {
			CreateTeamDto dto = createTeamDtoForm.get();
			service.create(dto);
			flash("success", "登録しました。");
			// TODO 実際にはチームリストに遷移
			return ok(index.render("TODO:要編集"));
		} else {
			// TODO
			flash("error", "登録できません。");
			return badRequest(team.render("CREATE", createTeamDtoForm));
		}
	}


}
