package com.pl.donauturm.checklist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.checklist.databinding.FragmentChecklistBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MainFragmentChecklist extends Fragment implements ChecklistAdapter.ChecklistAdapterCallback {

    public static final String TAG = "com.pl.donauturm.Checklist";

    private FragmentChecklistBinding binding;
    private RecyclerView recyclerView;
    private ChecklistAdapter adapter;

    private final Map<String, TaskList> taskLists = new ArrayMap<>();
    private String currentList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChecklistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requireActivity().addMenuProvider(new ChecklistMenu());
        loadLists();
        TaskList initial = taskLists.entrySet().iterator().next().getValue();
        currentList = initial.getTaskListName();
        recyclerView = binding.tasks;
        adapter = new ChecklistAdapter(this, initial.getTaskListName(), initial.getDate(), initial.getBoss(), initial.getTaskList());
        adapter.setCallback(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void applyTaskList(@NonNull TaskList taskList) {
        adapter = new ChecklistAdapter(this, taskList.getTaskListName(), taskList.getDate(), taskList.getBoss(), taskList.getTaskList());
        adapter.setCallback(this);
        recyclerView.setAdapter(adapter);
        currentList = taskList.getTaskListName();
    }

    @NonNull
    private List<String> getListNames() {
        if (taskLists.isEmpty()) loadLists();
        return new ArrayList<>(taskLists.keySet());
    }

    private void loadLists() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        sharedPreferences.getAll().forEach((s, o) -> {
            TaskList taskList;
            if (o instanceof Set && (taskList = tryLoadTaskList(s)) != null)
                taskLists.put(s, taskList);
        });
        if (taskLists.isEmpty())
            taskLists.put("Neue Liste", new TaskList("Neue Liste"));
    }

    @Nullable
    private TaskList tryLoadTaskList(String name) {
        try {
            return loadTaskList(name);
        } catch (Exception ignored) {
            return null;
        }
    }

    @NonNull
    private TaskList loadTaskList(@NonNull String name) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Set<String> data = sharedPreferences.getStringSet(name, null);
        String boss = sharedPreferences.getString(name + "Boss", "");
        String date = sharedPreferences.getString(name + "Date", "");
        List<Task> tasks = data.stream().map(d -> d.split("->")).map(sd -> {
            String checkedS = sd[sd.length - 1].trim();
            String nameS = Arrays.stream(sd).limit(sd.length - 1).collect(Collectors.joining("->")).trim();
            return new Task(nameS, checkedS.equals("true"));
        }).collect(Collectors.toList());
        return new TaskList(name, boss, date, tasks);
    }

    @Nullable
    private TaskList getCurrentTaskList() {
        if (!taskLists.containsKey(currentList)) return null;
        return taskLists.get(currentList);
    }

    @NonNull
    private TaskList requireCurrentTaskList() {
        return Objects.requireNonNull(taskLists.get(currentList));
    }

    private void reset() {
        TaskList taskList = getCurrentTaskList();
        if (taskList == null) return;
        taskList.setBoss("");
        taskList.setDate("");
        taskList.reset();
        saveTaskList(taskList);
        applyTaskList(taskList);
    }

    private void addList() {
        String listName = getFreeListName("Neue Liste", 0);
        saveTaskList(listName, List.of(new Task("Halte diese Aufgabe um sie zu löschen")));
        taskLists.put(listName, loadTaskList(listName));
        requireActivity().invalidateOptionsMenu();
        applyTaskList(loadTaskList(listName));
    }

    private String getFreeListName(String testName, int iteration) {
        String checkString = iteration <= 1 ? testName : testName + " " + iteration;
        if (getListNames().contains(checkString))
            return getFreeListName(testName, ++iteration);
        return checkString;
    }

    @Override
    public void onTaskListNameChanged(String oldTaskListName, String newTaskListName, List<Task> taskList, String boss, String date) {
        taskLists.put(newTaskListName, new TaskList("tempSave", new ArrayList<>(List.of(new Task("tempTask")))));
        deleteTaskList(oldTaskListName);
        taskLists.remove(oldTaskListName);
        saveTaskList(new TaskList(newTaskListName, boss, date, taskList));
        currentList = newTaskListName;
        taskLists.replace(newTaskListName, loadTaskList(newTaskListName));
        requireActivity().invalidateOptionsMenu();
    }

    @Override
    public void onBossChanged(String taskListName, String newName) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(taskListName + "Boss", newName);
        editor.apply();
        taskLists.put(taskListName, loadTaskList(taskListName));
    }

    @Override
    public void onDateChanged(String taskListName, String date) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(taskListName + "Date", date);
        editor.apply();
        taskLists.put(taskListName, loadTaskList(taskListName));
    }

    @Override
    public void onTaskAdded(String taskListName, Task addedTask, List<Task> taskList) {
        saveTaskList(taskListName, taskList);
        taskLists.put(taskListName, loadTaskList(taskListName));
    }

    @Override
    public void onTaskRemoved(String taskListName, int index, List<Task> taskList) {
        saveTaskList(taskListName, taskList);
        taskLists.put(taskListName, loadTaskList(taskListName));
    }

    @Override
    public void onTaskChecked(String taskListName, boolean isChecked, Task task, List<Task> taskList) {
        saveTaskList(taskListName, taskList);
        taskLists.put(taskListName, loadTaskList(taskListName));
    }

    private void saveTaskList(String taskListName, List<Task> taskList) {
        saveTaskList(new TaskList(taskListName, taskList));
    }

    private void saveTaskList(@NonNull TaskList taskList) {
        StringBuilder debug = new StringBuilder();
        Set<String> stringDataSet = new ArraySet<>();
        for (Task task : taskList.getTaskList()) {
            String taskData = task.getTaskName() + " -> " + (task.isChecked() ? "true" : "false");
            stringDataSet.add(taskData);
            if (task.isChecked())
                debug.append(task.getTaskName()).append(", ");
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(taskList.getTaskListName(), stringDataSet);
        if (taskList.getDate() != null)
            editor.putString(taskList.getTaskListName() + "Date", taskList.getDate());
        if (taskList.getBoss() != null)
            editor.putString(taskList.getTaskListName() + "Boss", taskList.getBoss());
        editor.apply();
        Log.d(getTag(), "Saved " + taskList.getTaskListName() + ": " + debug);
    }

    private void deleteCurrentTaskList() {
        deleteTaskList(currentList);
        taskLists.remove(currentList);
        TaskList newShown = taskLists.entrySet().iterator().next().getValue();
        applyTaskList(newShown);
        requireActivity().invalidateOptionsMenu();
    }

    private void deleteTaskList(String oldTaskListName) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(oldTaskListName);
        editor.remove(oldTaskListName + "Date");
        editor.remove(oldTaskListName + "Boss");
        editor.apply();
    }

    public class ChecklistMenu implements MenuProvider{

        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.checklist_menu, menu);
        }

        @Override
        public void onPrepareMenu(@NonNull Menu menu) {
            List<String> listNames = getListNames();
            SubMenu listsMenu = menu.findItem(R.id.lists).getSubMenu();
            listsMenu.clear();
            for (String listName : listNames) {
                MenuItem menuItem = listsMenu.add(R.id.list_group, Menu.NONE, Menu.NONE, listName);
                menuItem.setCheckable(true);
                if (listName.equals(currentList))
                    menuItem.setChecked(true);
            }
            listsMenu.add("Liste hinzufügen");
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem item) {
            String title = item.getTitle().toString();
            switch (title) {
                case "Reset":
                    new AreYouSure().setOnYesListener(MainFragmentChecklist.this::reset).show(getChildFragmentManager(), getTag());
                    break;
                case "Liste hinzufügen":
                    addList();
                    break;
                case "Print":
                    new PdfPrinter(binding.getRoot(), requireCurrentTaskList().toString(), getActivity()).open();
                    break;
                case "Delete":
                    new AreYouSure().setOnYesListener(MainFragmentChecklist.this::deleteCurrentTaskList).show(getChildFragmentManager(), getTag());
                    break;
                default:
                    if (taskLists.containsKey(title)) {
                        applyTaskList(Objects.requireNonNull(taskLists.get(title)));
                        break;
                    }
                    return false;
            }
            return true;
        }
    }

    public static class TaskList {
        private final String taskListName;
        private final List<Task> taskList;
        private String boss;
        private String date;

        public TaskList(String taskListName) {
            this.taskListName = taskListName;
            this.taskList = new ArrayList<>();
        }

        public TaskList(String taskListName, List<Task> taskList) {
            this.taskListName = taskListName;
            this.taskList = taskList;
        }

        public TaskList(String taskListName, String boss, String date, List<Task> taskList) {
            this.taskListName = taskListName;
            this.boss = boss;
            this.date = date;
            this.taskList = taskList;
        }

        public String getTaskListName() {
            return taskListName;
        }

        public String getBoss() {
            return boss;
        }

        public void setBoss(String boss) {
            this.boss = boss;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<Task> getTaskList() {
            return taskList;
        }

        private void reset() {
            this.taskList.forEach(tl -> tl.setChecked(false));
        }

        @NonNull
        @Override
        public String toString() {
            return (getTaskListName() + getDate() + getBoss()).replaceAll("[ .äüöß]+", "");
        }
    }

}
