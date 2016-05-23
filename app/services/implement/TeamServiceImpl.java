package services.implement;

import dto.team.CreateTeamDto;
import models.Team;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.TeamService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/05/10.
 */
public class TeamServiceImpl implements TeamService {
	private static final Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

	@Override
	public void create(CreateTeamDto createTeamDto) {
		logger.info("TeamServiceImpl#create");
		Team team = new Team();
		team.teamName = createTeamDto.teamName;

		// チームメンバー
		// TODO N:Mの登録方法はこれでよいのだろうか？
		// DTOのLISTからユーザーを取得
		// TODO ユーザー名から再度ユーザーを取得して登録ってすごく冗長。。。
		team.members = new ArrayList<User>();
		List<String> errorMessages = new ArrayList<String>();
		for (String userName : createTeamDto.memberListStr.split(",")) {
			// TODO ユーザー名からユーザーを取得
			List<User> user = User.find.where().eq("userName", userName).findList();
			// 取得できなかった場合エラー
			if (user.size() == 0) {
				errorMessages.add("ユーザー：" + userName + " は存在しません。");
			} else {
				// ユーザー名は重複ない前提
				team.members.add(user.get(0));
			}
		}
		if (errorMessages.size() > 0) {
			// TODO エラーメッセージの返し方
		} else {
			team.save();
		}
	}
}
