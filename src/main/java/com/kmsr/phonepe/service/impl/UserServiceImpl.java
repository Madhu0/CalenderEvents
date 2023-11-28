package com.kmsr.phonepe.service.impl;

import static java.util.Objects.isNull;

import com.kmsr.phonepe.Exceptions.AssetNotFoundException;
import com.kmsr.phonepe.entities.User;
import com.kmsr.phonepe.repo.UserRepo;
import com.kmsr.phonepe.service.UserService;
import com.kmsr.phonepe.utils.PreConditions;
import java.util.Collection;
import java.util.List;

public class UserServiceImpl implements UserService {

  private final UserRepo userRepo;

  public UserServiceImpl(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  @Override
  public User create(String name) {
    PreConditions.notEmpty(name);
    User user = new User(name);
    return userRepo.save(user);
  }

  @Override
  public User create(String id, String name) {
    PreConditions.notEmpty(id);
    PreConditions.notEmpty(name);
    User user = new User(name);
    user.setId(id);
    return userRepo.save(user);
  }

  @Override
  public User getById(String id) {
    PreConditions.notEmpty(id);
    User user = userRepo.getById(id);
    if (isNull(user)) {
      throw new AssetNotFoundException();
    }
    return user;
  }

  @Override
  public User update(String id, String name) {
    PreConditions.notEmpty(id);
    PreConditions.notEmpty(name);
    User user = getById(id);
    user.setName(name);
    return userRepo.save(user);
  }

  @Override
  public List<User> getAllByIds(Collection<String> ids) {
    PreConditions.notEmpty(ids);
    return userRepo.getAllByIds(ids);
  }
}
