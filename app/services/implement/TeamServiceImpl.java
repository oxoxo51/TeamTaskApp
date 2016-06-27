package services.implement;

import constant.Constant;
import controllers.Apps;
import dto.team.EditTeamDto;
import models.Team;
import models.User;
import play.Logger;
import services.TeamService;
import util.ConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/05/10.
 */
public class TeamServiceImpl implements TeamService {
	@Override
	public void create(EditTeamDto editTeamDto) {
		Logger.info("TeamServiceImpl#edit");

		Team team = new Team();
		team.teamName = editTeamDto.getTeamName();

		// チームメンバー
		// DTOのLISTからユーザーを取得
		// TODO ユーザー名から再度ユーザーを取得して登録ってすごく冗長。。。
		List<String> errorMessages = new ArrayList<String>();
		setMembers(editTeamDto.getMemberListStr(), team.members, errorMessages);

		// 作成ユーザー:ログインユーザーから設定
		// TODO ユーザーサービス経由で取得するようにする
		team.createUser = User.find.where().eq(
				"userName", Apps.getLoginUserName())
				.findList().get(0);

		if (errorMessages.size() > 0) {
			// TODO エラーメッセージの返し方
		} else {
			team.save();
		}
	}

	@Override
	public void update(EditTeamDto editTeamDto) {
		Logger.info("TeamServiceImpl#update");
		Team team = Team.find.byId(editTeamDto.getId());
		team.teamName = editTeamDto.getTeamName();

		// チームメンバー
		// 一度クリアした上でDTOからセット
		// TODO ユーザー名から再度ユーザーを取得して登録ってすごく冗長。。
		List<String> errorMessages = new ArrayList<String>();
		team.members.clear();
		setMembers(editTeamDto.getMemberListStr(), team.members, errorMessages);

		if (errorMessages.size() > 0) {
			// TODO エラーメッセージの返し方
		} else {
			team.update();
		}
	}

	/**
	 * ユーザーが所属するチームをユーザー名から取得する.
	 * @param userName
	 * @return
	 */
	@Override
	public List<Team> findTeamListByUserName(String userName) {
		Logger.info("TeamServiceImpl#findTeamListByUserName");
		// ユーザー名から該当ユーザーを取得
		// TODO ユーザーサービス経由で取得
		List<User> userList = User.find.where().eq("userName", userName).findList();
		if (userList.size() == 0) {
			return null;
		} else {
			// ユーザーの所属チームを取得
			return Team.find.where().eq("members", userList.get(0)).setOrderBy("teamName").findList();
		}
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
		for (String userName : memberListStr.split(",")) {
			// ユーザー名からユーザーを取得
			// TODO ユーザーサービス経由で取得
			List<User> user = User.find.where().eq("userName", userName).findList();
			// 取得できなかった場合エラー
			if (user.size() == 0) {
				errorMessages.add(userName + ConfigUtil.get(Constant.MSG_E005));
			} else {
				// ユーザー名は重複ない前提
				members.add(user.get(0));
			}
		}
	}
}
