package services;

import com.google.inject.ImplementedBy;
import dto.user.CreateUserDto;
import services.implement.UserServiceImpl;

/**
 * Created on 2016/05/08.
 */
@ImplementedBy(UserServiceImpl.class)
public interface UserService {

	void create(CreateUserDto createUserDto);

}
