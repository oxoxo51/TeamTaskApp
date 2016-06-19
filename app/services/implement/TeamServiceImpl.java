package services.implement;

import dto.team.CreateTeamDto;
import models.Team;
import models.User;
import play.Logger;
import services.TeamService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/05/10.
 */
public class TeamServiceImpl implements TeamService {
	@Override
	public void create(CreateTeamDto createTeamDto) {
		Logger.info("TeamServiceImpl#create");
		Team team = new Team();
		team.teamName = createTeamDto.getTeamName();

		// チームメンバー
		// TODO N:Mの登録方法はこれでよいのだろうか？
		// DTOのLISTからユーザーを取得
		// TODO ユーザー名から再度ユーザーを取得して登録ってすごく冗長。。。
		team.members = new ArrayList<User>();
		List<String> errorMessages = new ArrayList<String>();
		for (String userName : createTeamDto.getMemberListStr().split(",")) {
			// ユーザー名からユーザーを取得
			List<User> user = User.find.where().eq("userName", userName).findList();
			// 取得できなかった場合エラー
			if (user.size() == 0) {
				errorMessages.add("ユーザー：" + userName + " は存在しません。");
			} else {
				// ユーザー名は重複ない前提
				team.members.add(user.get(0));
			}
		}

		// 作成ユーザー
		// TODO ログインユーザーから設定

		if (errorMessages.size() > 0) {
			// TODO エラーメッセージの返し方
		} else {
			team.save();
		}
	}

	/**
	 * ユーザーが所属するチームをユーザー名から取得する.
	 * @param userName
	 * @return
	 */
	public List<Team> findTeamListByUserName(String userName) {
		Logger.info("TeamServiceImpl#findTeamListByUserName");
		// ユーザー名から該当ユーザーを取得
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
}
