package com.kmsr.phonepe.repo.impl;

import com.kmsr.phonepe.entities.User;
import com.kmsr.phonepe.repo.UserRepo;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRepoImpl implements UserRepo {

  private final Map<String, User> userMap;

  public UserRepoImpl() {
    userMap = new HashMap<String, User>();
  }

  public User save(User user) {
    userMap.put(user.getId(), user);
    return user;
  }

  public User getById(String id) {
    return userMap.get(id);
  }

  public User deleteById(String id) {
    return userMap.remove(id);
  }

  @Override
  public List<User> getAllByIds(Collection<String> ids) {
    return userMap.values().stream().filter(user -> ids.contains(user.getId()))
        .collect(Collectors.toList());
  }
}
