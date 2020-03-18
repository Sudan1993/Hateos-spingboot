package com.fincity.hateoas.repository;

import com.fincity.hateoas.model.ConfirmationToken;
import com.fincity.hateoas.model.User;
import org.springframework.data.repository.CrudRepository;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
	ConfirmationToken findByConfirmationToken(String confirmationToken);
	ConfirmationToken findByUser(User user);
}
