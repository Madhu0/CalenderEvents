Users with multiple working shifts and busy slots
Fetch events of the current user and others
Users can create events for time period and other users
Only an organizer can delete a event
Fetch most favourable upcoming slot to create an event
Fetch conflicts for self

Assumptions

1. Assuming the entered shift details for 7 days week(Ignoring weekly offs)
2. No shift details means user will be available for entire day except for the busy slots
3. Not allowing overlapping shifts or Busy slots
4. Assuming the shifts and busy slots are in UTC and day start and end are in UTC

```
Commands
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

CreateEvent
1
2,3
2023-11-26T14:00
2023-11-26T16:00

GetConflicts
1
2023-11-25T00:00
2023-11-28T00:00

GetConflicts
2
2023-11-25T00:00
2023-11-28T00:00

GetConflicts
3
2023-11-25T00:00
2023-11-28T00:00
```