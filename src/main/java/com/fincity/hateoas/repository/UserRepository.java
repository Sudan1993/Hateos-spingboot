package com.fincity.hateoas.repository;

import com.fincity.hateoas.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
	User findByEmailIdIgnoreCase(String emailId);
}
