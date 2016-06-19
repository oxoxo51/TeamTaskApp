package services;

import com.google.inject.ImplementedBy;
import dto.team.CreateTeamDto;
import models.Team;
import services.implement.TeamServiceImpl;

import java.util.List;

/**
 * Created on 2016/05/10.
 */
@ImplementedBy(TeamServiceImpl.class)
public interface TeamService {

	void create(CreateTeamDto createTeamDto);
	List<Team> findTeamListByUserName(String userName);
	List<Team> findTeamByName(String teamName);
}
