package services;

import com.google.inject.ImplementedBy;
import dto.task.CreateTaskMstDto;
import models.TaskTrn;
import services.implement.TaskServiceImpl;

import java.util.List;

/**
 * Created on 2016/05/24.
 */
@ImplementedBy(TaskServiceImpl.class)
public interface TaskService {

	void createTaskMst(CreateTaskMstDto createTaskMstDto);

	List<TaskTrn> findTaskList(long teamId, String dateStr);

	List<TaskTrn> createTaskTrn(long teamId, String dateStr);

	String getTaskMstName(long mstId);

	int updateTaskTrnStatus(long taskTrnId);
}
