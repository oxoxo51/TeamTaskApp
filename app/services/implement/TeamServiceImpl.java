package services.implement;

import constant.Constant;
import controllers.Apps;
import dto.team.EditTeamDto;
import models.Team;
import models.User;
import play.Logger;
import services.TeamService;
import services.UserService;
import util.MsgUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/05/10.
 */
public class TeamServiceImpl implements TeamService {

	/**
	 * チーム新規作成.
	 * @param editTeamDto
	 * @return
	 */
	@Override
	public List<String> create(EditTeamDto editTeamDto) {
		Logger.info("TeamServiceImpl#create");
		List<String> errorMessages = new ArrayList<String>();
		UserServiceImpl userService = new UserServiceImpl();

		Team team = new Team();
		// 作成ユーザー:ログインユーザーから設定
		team.createUser = userService.findUserByName(Apps.getLoginUserName()).get(0);
		// 登録・更新共通処理
		team = editTeam(team, editTeamDto, errorMessages);
		// チームメンバー

		if (errorMessages.size() == 0) {
			team.save();
		}
		return errorMessages;
	}

	/**
	 * チーム更新.
	 * @param editTeamDto
	 * @return
	 */
	@Override
	public List<String> update(EditTeamDto editTeamDto) {
		Logger.info("TeamServiceImpl#update");
		List<String> errorMessages = new ArrayList<String>();

		Team team = Team.find.byId(editTeamDto.getId());
		// チームメンバー：一度クリアした上でDTOからセット
		team.members.clear();

		// 登録・更新共通処理
		team = editTeam(team, editTeamDto, errorMessages);

		if (errorMessages.size() == 0) {
			team.update();
		}
		return errorMessages;
	}

	private Team editTeam(Team team, EditTeamDto editTeamDto, List<String> errorMessages) {
		team.teamName = editTeamDto.getTeamName();
		// DTOのLISTからユーザーを取得
		setMembers(editTeamDto.getMemberListStr(), team.members, errorMessages);

		return team;
	}

	/**
	 * ユーザーが所属するチームをユーザーから取得する.
	 * @param user
	 * @return
	 */
	@Override
	public List<Team> findTeamListByUser(User user) {
		Logger.info("TeamServiceImpl#findTeamListByUser");

		// ユーザーの所属チームを取得
		return Team.find.where().eq("members", user).setOrderBy("teamName").findList();

	}

	/**
	 * チーム名でチームを取得する.
	 * @param teamName
	 * @return
	 */
	@Override
	public List<Team> findTeamByName(String teamName) {
		Logger.info("TeamServiceImpl#findTeamByName");
		return Team.find.where().eq("teamName", teamName).findList();
	}

	/**
	 * チームメンバーをセットする.
	 * @param memberListStr
	 * @param members
	 * @param errorMessages
	 */
	private void setMembers(String memberListStr, List<User> members, List<String> errorMessages) {

		UserService userService = new UserServiceImpl();

		for (String userName : memberListStr.split(",")) {
			// ユーザー名からユーザーを取得
			List<User> user = userService.findUserByName(userName);
			// 取得できなかった場合エラー
			if (user.size() == 0) {
				errorMessages.add(MsgUtil.makeMsgStr(Constant.MSG_E005, userName));
			} else {
				// ユーザー名は重複ない前提
				members.add(user.get(0));
			}
		}
	}

	public List<User> findUserByTeamName(String teamName) {
		Logger.info("TeamServiceImpl#findUserByTeamName");

		Team team = Team.find.where().eq("teamName", teamName).findList().get(0);
		return team.members;
	}

}
