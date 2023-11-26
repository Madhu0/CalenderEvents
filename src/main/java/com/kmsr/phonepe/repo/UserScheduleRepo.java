package com.kmsr.phonepe.repo;

import com.kmsr.phonepe.entities.UserSchedule;
import java.util.List;

public interface UserScheduleRepo {

  UserSchedule save(UserSchedule userSchedule);

  UserSchedule getById(String id);

  List<UserSchedule> getAllByUserId(String userId);

  UserSchedule deleteById(String id);
}
