package services;

import com.google.inject.ImplementedBy;
import dto.team.EditTeamDto;
import models.Team;
import models.User;
import services.implement.TeamServiceImpl;

import java.util.List;

/**
 * Created on 2016/05/10.
 */
@ImplementedBy(TeamServiceImpl.class)
public interface TeamService {

	List<String> create(EditTeamDto editTeamDto);
	List<String> update(EditTeamDto editTeamDto);
	List<Team> findTeamListByUser(User user);
	List<Team> findTeamByName(String teamName);
}
