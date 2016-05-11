package services;

import com.google.inject.ImplementedBy;
import dto.team.CreateTeamDto;
import services.implement.TeamServiceImpl;

/**
 * Created on 2016/05/10.
 */
@ImplementedBy(TeamServiceImpl.class)
public interface TeamService {

	void create(CreateTeamDto createTeamDto);

}
