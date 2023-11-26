package com.kmsr.phonepe.repo;

import com.kmsr.phonepe.entities.Event;

public interface EventRepo {

  Event save(Event event);

  Event getById(String id);

  Event deleteById(String id);
}
