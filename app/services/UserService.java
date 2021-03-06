package services;

import com.google.inject.ImplementedBy;
import dto.user.ChangePwdDto;
import dto.user.CreateUserDto;
import models.User;
import services.implement.UserServiceImpl;

import java.util.List;

/**
 * Created on 2016/05/08.
 */
@ImplementedBy(UserServiceImpl.class)
public interface UserService {

	User create(CreateUserDto createUserDto);

	List<User> findUser(String userName, String password);

	List<User> findUserByName(String userName);

	void changePwd(ChangePwdDto changePwdDto);

	User findUserById(Long id);

	void updateLastLoginDate(User user);
}
