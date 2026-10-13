package mocks.day18.model;

import java.util.Collections;
import java.util.Set;

public record WorkoutEvent(String workoutType, int startTime, int endTime, Set<String> usersBooked, int capacity) {

    public WorkoutEvent {
        if(workoutType == null || workoutType.isEmpty()) {
            throw  new IllegalArgumentException("event Name or workoutType can't be null or empty !");
        }
        if(startTime < 0 || endTime < 0 || startTime > endTime || endTime > 23){
            throw   new IllegalArgumentException("invalid time range passed");
        }
        if(capacity < 0) {
            throw   new IllegalArgumentException("capacity can't be negative");
        }
       usersBooked = Collections.unmodifiableSet(usersBooked);
    }

    public WorkoutEvent(WorkoutEvent existingEvent, Set<String> usersBooked, int capacity) {
        this(existingEvent.workoutType() ,  existingEvent.startTime(), existingEvent.endTime(), usersBooked, capacity);
    }

    public boolean userBooked(String userName) {
        return  usersBooked.contains(userName);
    }

    public boolean overlaps(WorkoutEvent proposed) {
        return   proposed.endTime() > this.startTime() && proposed.startTime() < this.endTime;
    }
}
