package controllers;

import com.google.inject.Inject;
import constant.Constant;
import dto.team.EditTeamDto;
import models.Team;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import services.TeamService;
import services.implement.TeamServiceImpl;
import views.html.team;
import views.html.teamList;

import java.util.ArrayList;
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
		setSessionUrl(routes.TeamController.displayTeamList().url());

		// セッションのチームをクリアする
		session().remove("teamName");

		// ログイン状態確認の上ユーザーに紐付くチームリストを表示
		return ok(teamList.render(getLoginUserName(), getTeamList()));
	}

	/**
	 * チーム新規登録画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayCreateTeam() {
		Logger.info("TeamController#displayCreateTeam");
		setSessionUrl(routes.TeamController.displayCreateTeam().url());

		Form<EditTeamDto> editTeamDtoForm = Form.form(EditTeamDto.class);
		return ok(team.render(Constant.MODE_CREATE, editTeamDtoForm));
	}

	/**
	 * チーム更新画面表示.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result displayUpdateTeam(String teamName) {
		Logger.info("TeamController#displayTeam");
		setSessionTeamName(routes.TeamController.displayUpdateTeam(teamName).url());

		EditTeamDto dto = new EditTeamDto();

		String memberListStr = "";
		Team team = service.findTeamByName(teamName).get(0);
		dto.setId(team.id);
		for (User user : team.members) {
			memberListStr += (user.userName + ",");
		}
		// 最後のカンマを除く
		memberListStr = memberListStr.substring(0, memberListStr.length()-1);
		dto.setMemberListStr(memberListStr);
		dto.setTeamName(team.teamName);

		Form<EditTeamDto> editTeamDtoForm = Form.form(EditTeamDto.class).fill(dto);
		return ok(views.html.team.render(Constant.MODE_UPDATE, editTeamDtoForm));
	}

	/**
	 * チーム登録/更新.
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result edit(String mode) {
		Logger.info("TeamController#edit MODE:" + mode);

		List<String> errorMessages = new ArrayList<String>();

		Form<EditTeamDto> editTeamDtoForm = Form.form(EditTeamDto.class).bindFromRequest();
		if (!editTeamDtoForm.hasErrors()) {
			String msg ="";
			EditTeamDto dto = editTeamDtoForm.get();
			switch (mode) {
				case Constant.MODE_CREATE :
					service.create(dto);
					flashSuccess(Constant.MSG_I003);
					break;
				case Constant.MODE_UPDATE :
					errorMessages = service.update(dto);
					// エラー返却時の処理
					if (errorMessages != null && errorMessages.size() > 0) {
						for (String error : errorMessages) {
							flash("error", error);
						}
						return badRequest(team.render(mode, editTeamDtoForm));
					}
					flashSuccess(Constant.MSG_I004);
					break;
			}
			// チームリストを表示
			return redirect(routes.TeamController.displayTeamList());
		} else {
			flashError(Constant.MSG_E003);
			return badRequest(team.render(mode, editTeamDtoForm));
		}
	}

	/**
	 * チームの選択状態を変更する.
	 * @param teamName
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public Result changeTeam(String teamName) {
		Logger.info("TeamController#changeTeam");

		if (Constant.USER_TEAM_BLANK.equals(teamName)) {
			clearSessionTeamName();
		} else {
			setSessionTeamName(teamName);
		}
		return redirect(session(Constant.ITEM_URL));

	}

	/**
	 * セッションのログインユーザーに紐付くチームリストを返却する.
	 * @return
	 */
	public static List<Team> getTeamList() {
		TeamServiceImpl teamService = new TeamServiceImpl();
		return teamService.findTeamListByUserName(getLoginUserName());
	}

}
