package task;

import java.util.Comparator;

/**
 * Created by Havayi on 20-Dec-16.
 */

public class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task task, Task t1) {
        return task.getDate().compareTo(t1.getDate());
    }
}
