package com.ExpenseTracker.Auth.Repository;

import com.ExpenseTracker.Auth.Entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, String> {

    public UserInfo findByUserName(String username);
}
