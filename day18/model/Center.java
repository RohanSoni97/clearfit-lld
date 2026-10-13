package mocks.day18.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public record Center(String centerName, List<int[]> timings, Set<String> workoutTypes) {
    public Center {
        if(centerName == null || centerName.isEmpty()) {
            throw  new IllegalArgumentException("centerName cannot be null or empty");
        }
        timings = Collections.unmodifiableList(timings);
        workoutTypes = Collections.unmodifiableSet(workoutTypes);

    }

    private boolean isAllowedWorkoutType(String workoutType) {
        if(!workoutTypes.contains(workoutType)) {
            throw   new IllegalArgumentException("workoutType is not allowed: " + workoutType);
        }
        return  true;
    }

    public Center(Center existingCenter, List<int[]> timings, Set<String> workoutTypes ) {
        this(existingCenter.centerName(), timings, workoutTypes);
    }

    /** Accepts a nonempty workout only when it fits entirely within one opening interval. */
    public boolean canAddEvent(WorkoutEvent event) {
        int startTime = event.startTime();
        int endTime = event.endTime();
        if (startTime >= endTime) return false;

        for (int[] slot : timings) {
            if (slot[0] <= startTime && endTime <= slot[1]) {
                return  true;
            }

        }

        return false;

    }

}
