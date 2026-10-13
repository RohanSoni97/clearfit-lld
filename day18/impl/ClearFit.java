package mocks.day18.impl;

import mocks.day18.model.Center;
import mocks.day18.model.User;
import mocks.day18.model.WorkoutEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClearFit {

    private static final ClearFit clearFit = new ClearFit();
    private final Map<String, Center> center = new HashMap<>();

    private final Map<String, List<WorkoutEvent>> workout = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();



    private  ClearFit() {}
    public static ClearFit getInstance() {
        return clearFit;
    }

    public synchronized String addCenter(String center) {
        if(this.center.containsKey(center)) {
            throw  new IllegalArgumentException("Center already exists");
        }
        this.center.put(center, new Center(center, new ArrayList<>(),new HashSet<>()));

        return "Center '" + center + "' added";
    }


    private synchronized String addCenterTiming(String centerName, int startTime,int  endTime) {
        if(startTime < 0 || endTime < 0 || startTime > endTime || endTime >= 24) {
            throw new IllegalArgumentException("Invalid start or end time");
        }
        Center existingCenter = center(centerName);
        List<int[]> timings = new ArrayList<>(existingCenter.timings());
        timings.add(new int[]{startTime, endTime});

        Center center = new Center(existingCenter, timings, existingCenter.workoutTypes());
        this.center.put(centerName, center);
        return  "Center '" + centerName + "' timing added '" + startTime + ":" + endTime + "'";
    }
    public synchronized String addCenterTimingSlots(String centerName, List<int[]> timings) {

        for (int []timing: timings) {
            System.out.println(addCenterTiming(centerName, timing[0], timing[1]));
        }
        return  "Center '" + centerName + "' timing added ";
    }


    public synchronized String addCenterActivities(String centerName, List<String> activities) {
        if(activities == null || activities.isEmpty()) {
            throw  new IllegalArgumentException("activities cannot be null or empty");
        }
        Center existingCenter = center(centerName);
        Set<String> workoutTypes = new HashSet<>(existingCenter.workoutTypes());
        workoutTypes.addAll(activities);

        Center center = new Center(existingCenter.centerName(), existingCenter.timings(), workoutTypes);
        this.center.put(centerName, center);

        return   "Center '" + centerName + "' activities added '" + quoted(activities) + "'";
    }

    public synchronized  String addWorkoutEvent(String centerName, String workoutType, int startTime, int endTime, int availableSlots) {
        Center existingCenter = center(centerName);
        if(!existingCenter.workoutTypes().contains(workoutType)) {
            throw   new IllegalArgumentException("Workout type '" + workoutType + "' does not exist");
        }

        List<WorkoutEvent> workoutEvents = (workout.containsKey(centerName)) ? new ArrayList<>(workout.get(centerName)) : new ArrayList<>();
        WorkoutEvent proposedWorkoutEvent = new WorkoutEvent(workoutType, startTime, endTime, new HashSet<>(), availableSlots);
        for (WorkoutEvent workoutEvent : workout.getOrDefault(centerName, new ArrayList<>())) {
            if(workoutEvent.overlaps(proposedWorkoutEvent)) {
                throw   new IllegalArgumentException("Workout event overlaps with other event !");
            }
        }
        if(!existingCenter.canAddEvent(proposedWorkoutEvent)) {
            throw   new IllegalArgumentException("Center does not operate at this time slot!");
        }
        workoutEvents.add(proposedWorkoutEvent);
        this.workout.put(centerName, workoutEvents);

        return "WorkoutType added successfully";
    }

    public synchronized String registerUser(String name) {
        if(users.containsKey(name)) {
            throw   new IllegalArgumentException("User already exists");
        }
        this.users.put(name, new User(name));
        return  "User registered successfully";
    }


    public synchronized List<String> viewWorkoutAvailability(String workoutType) {
        List<String> centerName = centerPerformingWorkoutOfSpecificType(workoutType);
        if(centerName.isEmpty()) {
           throw new IllegalArgumentException("No center with specific workout");
        }

        List<String> messages = new ArrayList<>();
        for (String center: centerName){

            String timing  = filterWorkoutOfSpecificType( workout.get(center), workoutType);
            messages.add(center +" '" + workoutType +"' " + timing);
        }
        return messages;
    }

    public synchronized String viewWorkoutAvailability(String workoutType,String centerName) {
        Center center = center(centerName);
        if(!center.workoutTypes().contains(workoutType)) {
            throw   new IllegalArgumentException("Workout type '" + workoutType + "' does not exist");
        }
        return centerName +" '" + workoutType +"' " + filterWorkoutOfSpecificType(workout.get(centerName), workoutType);

    }


    public synchronized String bookSession(String user, String center, String workoutType, int startTime, int endTime) {
        if(workoutType == null || workoutType.isEmpty()) {
            throw new IllegalArgumentException("workoutType cannot be null or empty");
        }
        if(startTime < 0 || endTime < 0 || startTime > endTime || endTime >= 24) {
            throw new IllegalArgumentException("Invalid start or end time");
        }

        user(user);
        center(center);

        Optional<WorkoutEvent> workoutEvent = workout(center, workoutType, startTime, endTime);
        if(!workoutEvent.isPresent()) {
            throw   new IllegalArgumentException("Workout event does not exist");
        }
        WorkoutEvent workoutEventExisting = workoutEvent.get();
        if(workoutEventExisting.capacity() == 0) {
            throw new IllegalArgumentException("No seat available");
        }
        Set<String> usersBooked = new HashSet<>(workoutEventExisting.usersBooked());
        usersBooked.add(user);
        WorkoutEvent workoutEvent_ = new WorkoutEvent(workoutEventExisting, usersBooked, workoutEventExisting.capacity() - 1);
        this.workout.get(center).remove(workoutEventExisting);
        this.workout.get(center).add(workoutEvent_);

        return "Session booked successfully !";
    }
    public synchronized String cancelSession(String user, String center, String workoutType, int startTime, int endTime) {
        if(workoutType == null || workoutType.isEmpty()) {
            throw new IllegalArgumentException("workoutType cannot be null or empty");
        }
        if(startTime < 0 || endTime < 0 || startTime > endTime || endTime >= 24) {
            throw new IllegalArgumentException("Invalid start or end time");
        }
        user(user);
        center(center);

        Optional<WorkoutEvent> workoutEvent = workoutBookedByUser(center, workoutType, startTime, endTime, user);
        if(!workoutEvent.isPresent()) {
            throw   new IllegalArgumentException("user has n't booked event");
        }
        WorkoutEvent workoutEventExisting = workoutEvent.get();
        Set<String> usersBooked = new HashSet<>(workoutEventExisting.usersBooked());
        usersBooked.remove(user);
        WorkoutEvent workoutEvent_ = new WorkoutEvent(workoutEventExisting, usersBooked, workoutEventExisting.capacity() + 1);
        this.workout.get(center).remove(workoutEventExisting);
        this.workout.get(center).add(workoutEvent_);

        return  "Session cancelled successfully !";

    }

    private Optional<WorkoutEvent> workoutBookedByUser(String center, String workoutType, int startTime, int endTime, String user) {
        return this.workout.get(center).stream().filter(workoutEvent -> workoutEvent.workoutType().equals(workoutType)
                && workoutEvent.startTime() == startTime && workoutEvent.endTime() == endTime && workoutEvent.userBooked(user)).findFirst();
    }
    private Optional<WorkoutEvent> workout(String center, String workoutType, int startTime, int endTime) {
        return this.workout.get(center).stream().filter(workoutEvent -> workoutEvent.workoutType().equals(workoutType)
                && workoutEvent.startTime() == startTime && workoutEvent.endTime() == endTime).findFirst();
    }

    private String filterWorkoutOfSpecificType(  List<WorkoutEvent> workoutEvents , String workoutType) {
        List<Integer> list = workoutEvents.stream()
                .filter(workoutEvent -> workoutEvent.workoutType().equals(workoutType))
                .map(WorkoutEvent::startTime).toList();

        Collections.sort(new ArrayList<>(list));

        return list.stream().map(n -> "'" + n + "'").collect(Collectors.joining(","));
    }

    private List<String> centerPerformingWorkoutOfSpecificType(String workoutType) {

       return this.center.values().stream()
                .filter(center_ -> center_.workoutTypes().contains(workoutType))
                .map(Center::centerName)
                .toList();
    }


    private Center center(String name) {
        Center center_ = center.get(name);
        if(center_ == null) {
            throw new IllegalArgumentException("Center " + name + " does not exist");
        }
        return  center_;
    }



    private  User user(String name) {
        User user = users.get(name);
        if(user == null) {
            throw   new IllegalArgumentException("User " + name + " does not exist");
        }
        return   user;
    }

    private  String quoted(List<String> list) {
        return list.stream().map(n -> "'" + n + "'").collect(Collectors.joining(",", "[", "]"));
    }






}
