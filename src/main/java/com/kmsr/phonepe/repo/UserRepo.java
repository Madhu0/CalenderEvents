package com.kmsr.phonepe.repo;

import com.kmsr.phonepe.entities.User;
import java.util.Collection;
import java.util.List;

public interface UserRepo {

  User save(User user);

  User getById(String id);

  User deleteById(String id);

  List<User> getAllByIds(Collection<String> ids);
}
