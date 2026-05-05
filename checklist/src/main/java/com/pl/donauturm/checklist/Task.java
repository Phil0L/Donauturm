package com.pl.donauturm.checklist;

import java.util.List;
import java.util.Objects;

public class Task {
    private final String taskName;
    private boolean checked;

    public Task(String taskName) {
        this.taskName = taskName;
        this.checked = false;
    }

    public Task(String taskName, boolean checked) {
        this.taskName = taskName;
        this.checked = checked;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        this.checked = !checked;
    }

    public boolean isInCategory(List<Task> others) {
        if (!getTaskName().contains(" - ")) return false;
        String category = getTaskName().split(" - ")[0];
        for (Task other : others) {
            if (other.getTaskName().equals(getTaskName()))
                continue;
            if (other.getTaskName().startsWith(category + " - "))
                return true;
        }
        return false;
    }

    public boolean couldBeInInCategory() {
        return getTaskName().contains(" - ");
    }

    public String getCategory(){
        if (!couldBeInInCategory()) return null;
        return getTaskName().split(" - ")[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskName, task.taskName);
    }

    @Override
    public int hashCode() {
        return taskName != null ? taskName.hashCode() : 0;
    }
}
