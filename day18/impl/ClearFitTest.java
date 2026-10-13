package mocks.day18.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClearFitTest {
    public static void main(String[] args) {
        ClearFit instance = ClearFit.getInstance();
        System.out.println(instance.addCenter("Koramangala"));
        System.out.println(instance.addCenterTimingSlots("Koramangala", List.of(new int[]{6, 9}, new int[]{18, 21})));

        System.out.println(instance.addCenterActivities("Koramangala", List.of("Weights", "Cardio", "Yoga", "Swimming")));

        System.out.println(instance.addCenter("Bellandur"));
        System.out.println(instance.addCenterTimingSlots("Bellandur", List.of(new int[]{7, 10}, new int[]{19, 22})));
        System.out.println(instance.addCenterActivities("Bellandur", List.of("Weights", "Cardio", "Yoga")));


        System.out.println(instance.addWorkoutEvent("Koramangala", "Weights",  6, 7, 100));
        System.out.println(instance.addWorkoutEvent("Koramangala", "Cardio",  7, 8, 150));
        System.out.println(instance.addWorkoutEvent("Koramangala", "Yoga",  8, 9, 200));


        check(() -> instance.addWorkoutEvent("Bellandur", "Weights",  18, 19, 100));

        // addWorkout(“Bellandur”, “Swimming”, 19, 20, 100) // not allowed because of workout type
        check(() -> instance.addWorkoutEvent("Bellandur", "Swimming",  19, 20, 100));

        System.out.println(instance.addWorkoutEvent("Bellandur", "Cardio", 19, 20, 20));
        System.out.println(instance.addWorkoutEvent("Bellandur", "Weights", 20, 21, 100));
        System.out.println(instance.addWorkoutEvent("Bellandur", "Weights", 21, 22, 100));
        check(() -> instance.addWorkoutEvent("Bellandur", "Cardio", 21, 22, 100));

        System.out.println(instance.registerUser("Vaibhav"));

        System.out.println(instance.viewWorkoutAvailability("Weights"));

        System.out.println(instance.bookSession("Vaibhav", "Koramangala", "Weights", 6, 7));

        System.out.println(instance.cancelSession("Vaibhav", "Koramangala", "Weights", 6, 7));

        System.out.println(instance.viewWorkoutAvailability("Weights", "Koramangala"));


/*
addWorkout(“Bellandur”, “Weights”, 20, 21, 100)
addWorkout(“Bellandur”, “Weights”, 21, 22, 100)

 */
    }

    public static void check(Runnable runnable) {
        try {
            runnable.run();
            System.out.println("Succeeded !!");
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "Exception in runnable", e.getMessage());
        }

    }
}
