package com.pl.donauturm.checklist;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pl.donauturm.checklist.databinding.PrefabBossBinding;
import com.pl.donauturm.checklist.databinding.PrefabCheckItemAddBinding;
import com.pl.donauturm.checklist.databinding.PrefabCheckItemBinding;
import com.pl.donauturm.checklist.databinding.PrefabCheckItemCategoryBinding;
import com.pl.donauturm.commons.util.datalistener.OnDataCheckedChangedListener;
import com.pl.donauturm.commons.util.datalistener.OnDataClickListener;
import com.pl.donauturm.commons.util.datalistener.OnDataLongClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChecklistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER_VIEW_TYPE = 1;
    public static final int TASK_VIEW_TYPE = 2;
    public static final int ADD_VIEW_TYPE = 3;
    public static final int CATEGORY_TYPE = 4;

    private final boolean showBoss = true;
    private final boolean showAdd = true;
    private final boolean showCategories = true;

    public static final String OTHERS = "Sonstiges";

    private final Fragment context;

    private String taskListName;
    private String date;
    private String boss;
    private final List<Integer> categoryIndexes;
    private final List<Task> tasks;
    private ChecklistAdapterCallback callback;

    public ChecklistAdapter(@NonNull Fragment activity, @NonNull String taskListName, @Nullable String date, @Nullable String boss, @Nullable List<Task> tasks) {
        this.context = activity;
        this.taskListName = taskListName;
        this.date = date != null ? date : "";
        this.boss = boss != null ? boss : "";
        if (tasks == null) tasks = new ArrayList<>();
        this.tasks = new ArrayList<>(tasks);
        this.categoryIndexes = new ArrayList<>();
        this.sortTasks();
        this.refreshCategories();
    }

    @Override
    public int getItemViewType(int position) {
        if (showBoss && position == 0) return HEADER_VIEW_TYPE;
        if (showAdd && position == getItemCount() - 1) return ADD_VIEW_TYPE;
        if (showCategories && categoryIndexes.contains(position)) return CATEGORY_TYPE;
        return TASK_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE)
            return new HeaderViewHolder(PrefabBossBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        if (viewType == ADD_VIEW_TYPE)
            return new TaskAddViewHolder(PrefabCheckItemAddBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        if (viewType == CATEGORY_TYPE)
            return new CategoryViewHolder(PrefabCheckItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        return new TaskViewHolder(PrefabCheckItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskViewHolder)
            onBindViewHolder(((TaskViewHolder) holder), position);
        if (holder instanceof HeaderViewHolder)
            onBindViewHolder(((HeaderViewHolder) holder), position);
        if (holder instanceof TaskAddViewHolder)
            onBindViewHolder(((TaskAddViewHolder) holder), position);
        if (holder instanceof CategoryViewHolder)
            onBindViewHolder(((CategoryViewHolder) holder), position);
    }

    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getTaskFromRecyclerPosition(position);
        holder.binding.taskName.setText(task.getTaskName());
        holder.binding.checkBox.setOnCheckedChangeListener(null);
        holder.binding.checkBox.setChecked(task.isChecked());
        holder.binding.checkBox.setOnCheckedChangeListener(((OnDataCheckedChangedListener<Task>) (lambdaTask, isChecked) -> {
            lambdaTask.setChecked(isChecked);
            if (callback != null)
                callback.onTaskChecked(taskListName, isChecked, lambdaTask, getTasks());
        }).withData(task));
        holder.binding.taskName.setOnClickListener(((OnDataClickListener<Task>) lambdaTask -> {
            lambdaTask.toggleChecked();
            notifyItemChanged(getRecyclerPositionFromTask(lambdaTask));
            if (callback != null)
                callback.onTaskChecked(taskListName, lambdaTask.isChecked(), lambdaTask, getTasks());
        }).withData(task));
        holder.binding.getRoot().setOnClickListener(((OnDataClickListener<Task>) lambdaTask -> {
            lambdaTask.toggleChecked();
            notifyItemChanged(getRecyclerPositionFromTask(lambdaTask));
            if (callback != null)
                callback.onTaskChecked(taskListName, lambdaTask.isChecked(), lambdaTask, getTasks());
        }).withData(task));
        holder.binding.taskName.setOnLongClickListener(((OnDataLongClickListener<Task>) lambdaTask -> {
            new AreYouSureWithData<>(getRecyclerPositionFromTask(lambdaTask)).setOnYesListener(this::removeTask).show(context.getChildFragmentManager(), getClass().getSimpleName());
            return true;
        }).withData(task));
    }

    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        if (date == null || date.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy", Locale.getDefault());
            date = sdf.format(Calendar.getInstance().getTime());
            if (callback != null)
                callback.onDateChanged(taskListName, date);
        }
        if (!holder.binding.editDate.getText().toString().equals(date))
            holder.binding.editDate.setText(date);
        holder.binding.editDate.addTextChangedListener((AbstractTextWatcher.Wrapper) text -> {
            date = text;
            if (callback != null) callback.onDateChanged(taskListName, text);
        });
        if (!holder.binding.editName.getText().toString().equals(boss))
            holder.binding.editName.setText(boss);
        holder.binding.editName.addTextChangedListener((AbstractTextWatcher.Wrapper) text -> {
            boss = text;
            if (callback != null) callback.onBossChanged(taskListName, text);
        });
        if (!holder.binding.listName.getText().toString().equals(taskListName))
            holder.binding.listName.setText(taskListName);
        holder.binding.listName.addTextChangedListener((AbstractTextWatcher.Wrapper) text -> {
            taskListName = text;
            if (callback != null && !text.isEmpty())
                callback.onTaskListNameChanged(taskListName, text, getTasks(), boss, date);
        });
    }

    public void onBindViewHolder(@NonNull TaskAddViewHolder holder, int position) {
        holder.binding.buttonAdd.setOnClickListener(v -> {
            String taskName = holder.binding.editTask.getText().toString();
            if (taskName.trim().isEmpty()) return;
            addTask(new Task(taskName));
            holder.binding.editTask.setText("");
        });
    }

    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = getTaskFromRecyclerPosition(position + 1).getCategory();
        holder.binding.categoryName.setText(category);
        if (category == null || category.isEmpty())
            holder.binding.categoryName.setText(OTHERS);
    }

    @NonNull
    private Task getTaskFromRecyclerPosition(int position) {
        final int requestPosition = position;
        if (showCategories)
            position -= categoryIndexes.stream().filter(i -> i <= requestPosition).count();
        if (showBoss) position--;
        return tasks.get(position);
    }

    private int getRecyclerPositionFromTask(Task task) {
        int position = tasks.indexOf(task);
        if (showBoss) position++;
        if (showCategories) {
            for (Integer categoryIndex : categoryIndexes) {
                if (categoryIndex <= position) position++;
            }
        }
        return position;
    }

    @Override
    public int getItemCount() {
        return tasks.size() + (showBoss ? 1 : 0) + (showAdd ? 1 : 0) + categoryIndexes.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTask(Task task) {
        this.tasks.add(task);
        this.sortTasks();
        sortTasks();
        refreshCategories();
        notifyDataSetChanged();
        if (callback != null)
            callback.onTaskAdded(taskListName, task, getTasks());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeTask(int recyclerPosition){
        notifyItemRemoved(recyclerPosition);
        tasks.remove(getTaskFromRecyclerPosition(recyclerPosition));
        if (tasks.isEmpty())
            addTask(new Task("Eine Liste braucht mindestens eine Aufgabe"));
        sortTasks();
        refreshCategories();
        notifyDataSetChanged();
        if (callback != null)
            callback.onTaskRemoved(taskListName, recyclerPosition + (showBoss ? -1 : 0), getTasks());
    }

    private void sortTasks() {
        List<Task> others = Arrays.asList(tasks.toArray(new Task[0]));
        tasks.sort((o1, o2) -> {
            if (o1.isInCategory(others) && o2.isInCategory(others))
                return o1.getTaskName().compareTo(o2.getTaskName());
            if (o1.isInCategory(others))
                return -1;
            if (o2.isInCategory(others))
                return 1;
            return o1.getTaskName().compareTo(o2.getTaskName());
        });
    }

    private void refreshCategories() {
        categoryIndexes.clear();
        String category = "";
        int index = (showBoss ? 1 : 0);
        int categoryIndex = (showBoss ? 1 : 0);
        for (Task task : tasks) {
            if (!category.isEmpty() && task.couldBeInInCategory() && !task.getCategory().equals(category)) {
                categoryIndexes.add(categoryIndex);
                index++;
                categoryIndex = index;
            }
            if (!task.couldBeInInCategory()) {
                categoryIndexes.add(categoryIndex);
                categoryIndexes.add(index + 1);
                break;
            }
            category = task.getCategory();
            index++;
        }
    }

    public void setCallback(ChecklistAdapterCallback callback) {
        this.callback = callback;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public PrefabCheckItemBinding binding;

        public TaskViewHolder(@NonNull PrefabCheckItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public PrefabBossBinding binding;

        public HeaderViewHolder(@NonNull PrefabBossBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class TaskAddViewHolder extends RecyclerView.ViewHolder {
        public PrefabCheckItemAddBinding binding;

        public TaskAddViewHolder(@NonNull PrefabCheckItemAddBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public PrefabCheckItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull PrefabCheckItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ChecklistAdapterCallback {
        void onTaskListNameChanged(String oldTaskListName, String newTaskListName, List<Task> taskList, String boss, String date);

        void onBossChanged(String taskListName, String newName);

        void onDateChanged(String tasListName, String date);

        void onTaskAdded(String taskListName, Task addedTask, List<Task> taskList);

        void onTaskRemoved(String taskListName, int index, List<Task> taskList);

        void onTaskChecked(String taskListName, boolean isChecked, Task task, List<Task> taskList);
    }

}