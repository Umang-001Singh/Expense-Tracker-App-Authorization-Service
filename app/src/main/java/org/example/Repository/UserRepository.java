package org.example.Repository;

import org.example.Entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, String> {

    public UserInfo findByUserName(String username);
}
