package dto.team;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import services.implement.TeamServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/05/10.
 */
public class CreateTeamDto {

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

		List<ValidationError> errors = new ArrayList<>();

		if(service.findTeamByName(teamName) != null && service.findTeamByName(teamName).size() != 0) {
			errors.add(new ValidationError("teamName", "チーム名:" + teamName + " は既に使用されています。"));
		}

		return errors.isEmpty() ? null : errors;
	}
}
