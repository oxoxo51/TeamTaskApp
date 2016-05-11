package dto.team;

import java.util.List;
import java.util.Map;

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
	 * チームメンバーのid,ユーザー名
	 */
	public List<Map<Long, String>> teamMemberList;
}
