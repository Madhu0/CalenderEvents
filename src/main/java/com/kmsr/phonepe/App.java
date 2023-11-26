package com.kmsr.phonepe;


import static com.kmsr.phonepe.utils.TimeUtil.epochFromString;

import com.kmsr.phonepe.entities.Event;
import com.kmsr.phonepe.entities.TimeSlot;
import com.kmsr.phonepe.entities.UserCalenderSlot;
import com.kmsr.phonepe.entities.UserSchedule;
import com.kmsr.phonepe.repo.EventParticipantRepo;
import com.kmsr.phonepe.repo.EventRepo;
import com.kmsr.phonepe.repo.UserCalenderSlotRepo;
import com.kmsr.phonepe.repo.UserRepo;
import com.kmsr.phonepe.repo.UserScheduleRepo;
import com.kmsr.phonepe.repo.impl.EventParticipantRepoImpl;
import com.kmsr.phonepe.repo.impl.EventRepoImpl;
import com.kmsr.phonepe.repo.impl.UserCalenderSlotRepoImpl;
import com.kmsr.phonepe.repo.impl.UserRepoImpl;
import com.kmsr.phonepe.repo.impl.UserScheduleRepoImpl;
import com.kmsr.phonepe.service.EventParticipantService;
import com.kmsr.phonepe.service.EventService;
import com.kmsr.phonepe.service.UserCalenderSlotService;
import com.kmsr.phonepe.service.UserScheduleService;
import com.kmsr.phonepe.service.UserService;
import com.kmsr.phonepe.service.impl.EventParticipantServiceImpl;
import com.kmsr.phonepe.service.impl.EventServiceImpl;
import com.kmsr.phonepe.service.impl.UserCalenderSlotServiceImpl;
import com.kmsr.phonepe.service.impl.UserScheduleServiceImpl;
import com.kmsr.phonepe.service.impl.UserServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) {
    System.out.println("Hello World!");
    initialiseTheApp();
  }

  private static void initialiseTheApp() {
    UserRepo userRepo = new UserRepoImpl();
    EventRepo eventRepo = new EventRepoImpl();
    EventParticipantRepo eventParticipantRepo = new EventParticipantRepoImpl();
    UserScheduleRepo userScheduleRepo = new UserScheduleRepoImpl();
    UserCalenderSlotRepo userCalenderSlotRepo = new UserCalenderSlotRepoImpl();

    UserService userService = new UserServiceImpl(userRepo);
    UserCalenderSlotService userCalenderSlotService = new UserCalenderSlotServiceImpl(
        userCalenderSlotRepo);
    UserScheduleService userScheduleService = new UserScheduleServiceImpl(userScheduleRepo,
        userCalenderSlotService);
    EventParticipantService eventParticipantService = new EventParticipantServiceImpl(
        eventParticipantRepo);
    EventService eventService = new EventServiceImpl(eventRepo, userService,
        userCalenderSlotService, eventParticipantService, userScheduleService);

    Scanner sc = new Scanner(System.in);
    /* Sample commands
      AddUser
      1
      Thor

      AddUser
      2
      Ironman

      AddUser
      3
      Black Widow

      AddShift
      1
      8
      16

      AddShift
      2
      10
      18

      AddShift
      3
      13
      21

      GetNextAvailableSlot
      1,2,3
      2023-11-26T11:00
      2

      GetNextAvailableSlot
      1,2,3
      2023-11-26T11:00
      5

      CreateEvent
      1
      2,3
      2023-11-26T13:00
      2023-11-26T14:00

      GetNextAvailableSlot
      1,2,3
      2023-11-26T11:00
      2

      CreateEvent
      1
      2,3
      2023-11-26T14:00
      2023-11-26T16:00

      GetNextAvailableSlot
      1,2,3
      2023-11-26T11:00
      2
     */
    boolean loopEnd = false;
    while (true) {
      try {
        long start;
        long end;
        String name, userIds, id, startString, endString;
        System.out.println("Enter Command");
        String command = sc.nextLine();
        switch (command) {
          case "AddUser":
            System.out.println("Enter user id");
            id = sc.nextLine();
            System.out.println("Enter user name");
            name = sc.nextLine();
            System.out.println(userService.create(id, name));
            break;
          case "AddShift":
            System.out.println("Enter user id");
            String userId = sc.nextLine();
            System.out.println("Enter starting hour");
            start = sc.nextLong();
            System.out.println("Enter ending hour");
            end = sc.nextLong();
            UserSchedule schedule = userScheduleService.addShift(userId,
                TimeUnit.HOURS.toMillis(start), TimeUnit.HOURS.toMillis(end));
            System.out.println(schedule);
            break;
          case "GetNextAvailableSlot":
            System.out.println("Enter user ids with comma separated values");
            userIds = sc.nextLine();
            System.out.println("Enter starting time in UTC");
            startString = sc.nextLine();
            System.out.println("Enter duration in hours");
            end = sc.nextLong();
            TimeSlot nextAvailableSlot = eventService.getNextAvailableSlot(
                Arrays.asList(userIds.split(",")), epochFromString(startString),
                TimeUnit.HOURS.toMillis(end));
            System.out.println(nextAvailableSlot);
            break;
          case "CreateEvent":
            System.out.println("Enter organiser id");
            String organiserId = sc.nextLine();
            System.out.println("Enter user ids with comma separated values");
            userIds = sc.nextLine();
            System.out.println("Enter starting time in UTC");
            startString = sc.nextLine();
            System.out.println("Enter ending time in UTC");
            endString = sc.nextLine();
            Event event = eventService.createEvent(organiserId, Arrays.asList(userIds.split(",")),
                epochFromString(startString), epochFromString(endString));
            System.out.println(event);
            break;
          case "DeleteEvent":
            System.out.println("Enter event id");
            String eventId = sc.nextLine();
            Event event1 = eventService.deleteEvent(eventId);
            System.out.println(event1);
            break;
          case "GetConflicts":
            System.out.println("Enter userId");
            userId = sc.nextLine();
            System.out.println("Enter starting time in UTC");
            startString = sc.nextLine();
            System.out.println("Enter ending time in UTC");
            endString = sc.nextLine();
            List<UserCalenderSlot> conflicts = eventService.getConflicts(userId,
                epochFromString(startString), epochFromString(endString));
            System.out.println(conflicts);
            break;
          case "End":
            System.out.println("Bye");
            loopEnd = true;
            break;
        }
        if (loopEnd) {
          break;
        }
      } catch (Exception e) {
        System.out.println(String.format("Error: %s \n\n\n", e.toString()));
      }
    }
  }
}
