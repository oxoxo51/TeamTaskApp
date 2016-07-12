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
		Logger.info("TeamServiceImpl#edit");

		UserServiceImpl userService = new UserServiceImpl();

		Team team = new Team();
		team.teamName = editTeamDto.getTeamName();

		// チームメンバー
		// DTOのLISTからユーザーを取得
		List<String> errorMessages = new ArrayList<String>();
		setMembers(editTeamDto.getMemberListStr(), team.members, errorMessages);

		// 作成ユーザー:ログインユーザーから設定
		team.createUser = userService.findUserByName(Apps.getLoginUserName()).get(0);

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
		Team team = Team.find.byId(editTeamDto.getId());
		team.teamName = editTeamDto.getTeamName();
		List<String> errorMessages = new ArrayList<String>();

		// チームメンバー
		// 一度クリアした上でDTOからセット
		team.members.clear();
		setMembers(editTeamDto.getMemberListStr(), team.members, errorMessages);

		if (errorMessages.size() == 0) {
			team.update();
		}
		return errorMessages;
	}

	/**
	 * ユーザーが所属するチームをユーザー名から取得する.
	 * @param userName
	 * @return
	 */
	@Override
	public List<Team> findTeamListByUserName(String userName) {
		Logger.info("TeamServiceImpl#findTeamListByUserName");

		UserServiceImpl userService = new UserServiceImpl();

		// ユーザー名から該当ユーザーを取得
		User user = userService.findUserByName(userName).get(0);

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
		Team team = Team.find.where().eq("teamName", teamName).findList().get(0);
		return team.members;
	}

	public Team findTeamById(Long teamId) {
		return Team.find.byId(teamId);
	}
}
