package com.kmsr.phonepe.service;

import com.kmsr.phonepe.entities.User;
import java.util.Collection;
import java.util.List;

public interface UserService {

  User create(String name);

  User create(String id, String name);

  User getById(String id);

  User update(String id, String name);

  List<User> getAllByIds(Collection<String> ids);
}
