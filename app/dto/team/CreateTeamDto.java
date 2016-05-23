package dto.team;

/**
 * Created on 2016/05/10.
 */
public class CreateTeamDto {

	/**
	 * チーム名.
	 */
	public String teamName;

	/**
	 * チームメンバーリスト.
	 * チームメンバー名をカンマ区切りで渡す.
	 */
	public String memberListStr;
}
