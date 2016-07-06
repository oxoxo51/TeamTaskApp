package dto.team;

import models.User;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.TeamServiceImpl;
import services.implement.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/05/10.
 */
public class EditTeamDto {

	/**
	 * id.
	 */
	private Long id;

	/**
	 * チーム名.
	 */
	@Constraints.Required
	private String teamName;

	/**
	 * チームメンバーリスト.
	 * チームメンバー名をカンマ区切りで渡す.
	 */
	@Constraints.Required
	private String memberListStr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getMemberListStr() {
		return memberListStr;
	}

	public void setMemberListStr(String memberListStr) {
		this.memberListStr = memberListStr;
	}


	/**
	 * バリデーション.
	 * @return
	 */
	public List<ValidationError> validate() {
		TeamServiceImpl service = new TeamServiceImpl();
		UserServiceImpl userService = new UserServiceImpl();

		List<ValidationError> errors = new ArrayList<>();

		// 入力のタスク名称と登録済み別タスクの名称が重複したらエラー
		if((service.findTeamByName(teamName).size() != 0)
				&& (id == null || service.findTeamByName(teamName).get(0).id != id)) {
			errors.add(new ValidationError("teamName", "チーム名:" + teamName + " は既に使用されています。"));
		}

		for (String userName : memberListStr.split(",")) {
			// ユーザー名からユーザーを取得
			List<User> user = userService.findUserByName(userName);
			// 取得できなかった場合エラー
			if (user.size() == 0) {
				errors.add(new ValidationError("memberListStr", "ユーザー：" + userName + " は存在しません。"));
			}
		}
		return errors.isEmpty() ? null : errors;
	}
}
