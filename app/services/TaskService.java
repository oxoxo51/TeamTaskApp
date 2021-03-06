package services;

import com.google.inject.ImplementedBy;
import dto.task.EditTaskMstDto;
import models.TaskMst;
import models.TaskTrn;
import models.Team;
import services.implement.TaskServiceImpl;

import java.text.ParseException;
import java.util.List;

/**
 * Created on 2016/05/24.
 */
@ImplementedBy(TaskServiceImpl.class)
public interface TaskService {

	void createTaskMst(EditTaskMstDto editTaskMstDto);

	void updateTaskMst(EditTaskMstDto editTaskMstDto);

	TaskMst findTaskMstByTeamAndTaskName(String teamName, String taskName);

	List<TaskMst> findTaskMstByTeamName(String teamName) throws Exception;

	List<TaskTrn> findTaskList(Team team, String dateStr);

	TaskTrn createTaskTrn(TaskMst taskMst, String dateStr) throws ParseException;

	int updateTaskTrnStatus(long taskTrnId);

	TaskMst getTaskMstByTaskTrnId(Long taskTrnId);
}
