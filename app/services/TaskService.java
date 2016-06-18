package services;

import com.google.inject.ImplementedBy;
import dto.task.CreateTaskMstDto;
import models.TaskMst;
import models.TaskTrn;
import services.implement.TaskServiceImpl;

import java.text.ParseException;
import java.util.List;

/**
 * Created on 2016/05/24.
 */
@ImplementedBy(TaskServiceImpl.class)
public interface TaskService {

	void createTaskMst(CreateTaskMstDto createTaskMstDto);

	List<TaskTrn> findTaskList(long teamId, String dateStr);

	List<TaskTrn> createTaskTrnByTeamId(long teamId, String dateStr);

	TaskTrn createTaskTrn(TaskMst taskMst, String dateStr) throws ParseException;

	String getTaskMstName(long mstId);

	int updateTaskTrnStatus(long taskTrnId);
}
