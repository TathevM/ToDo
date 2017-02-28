package com.havayi.todo;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import fragments.NewTaskFragment;
import fragments.TaskListFragment;
import task.Task;

public class MainActivity extends FragmentActivity {

    FragmentManager fragmentManager;
    TaskListFragment taskListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        taskListFragment = (TaskListFragment) fragmentManager.findFragmentById(R.id.task_list_fragment);
        taskListFragment.setOnActionClickListener(new TaskListFragment.IOnNewTaskClickListener() {
            @Override
            public void onNewTaskClick(Task task) {
                openNewTaskFragment();
            }
        });
        taskListFragment.setOnItemClickListener(new TaskListFragment.IOnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                openNewTaskFragment(task);
            }
        });
    }

    private void openNewTaskFragment(Task task)
    {
        //setArguments (task) and open
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NewTaskFragment newTaskFragment = new NewTaskFragment();
        newTaskFragment.setOnSaveButtonClickListener(new NewTaskFragment.IOnSaveButtonClickListener() {
            @Override
            public void OnSaveButtonClicked(Task task) {
                passTaskToList(task);
            }
        });
        newTaskFragment.setOnDeleteButtonClickListener(new NewTaskFragment.IOnDeleteButtonClickListener() {
            @Override
            public void onDeleteButtonCLicked(long taskID) {
                deleteTask(taskID);
            }
        });
        fragmentTransaction.setCustomAnimations(R.anim.new_task_in, R.anim.new_task_out);
        Bundle args = new Bundle();
        args.putParcelable("task", task);
        newTaskFragment.setArguments(args);
        fragmentTransaction.add(R.id.new_task_frame_layout ,newTaskFragment, "newTaskFragment");
        fragmentTransaction.commit();
    }

    private void openNewTaskFragment()
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NewTaskFragment newTaskFragment = new NewTaskFragment();
        newTaskFragment.setOnSaveButtonClickListener(new NewTaskFragment.IOnSaveButtonClickListener() {
            @Override
            public void OnSaveButtonClicked(Task task) {
                passTaskToList(task);
            }
        });
        newTaskFragment.setOnBackButtonPressedListener(new NewTaskFragment.IOnBackButtonPressedListener() {
            @Override
            public void onBackButtonPressed() {
                passTaskToList(null);
            }
        });
        fragmentTransaction.setCustomAnimations(R.anim.new_task_in, R.anim.new_task_out);
        fragmentTransaction.add(R.id.new_task_frame_layout ,newTaskFragment, "newTaskFragment");
        fragmentTransaction.commit();
    }

    private void deleteTask(long ID)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.new_task_out, R.anim.new_task_out);
        fragmentTransaction.remove(fragmentManager.findFragmentByTag("newTaskFragment"));
        fragmentTransaction.commit();
        taskListFragment.deleteTaskByID(ID);
    }

    private void passTaskToList(Task task)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.new_task_out, R.anim.new_task_out);
        fragmentTransaction.remove(fragmentManager.findFragmentByTag("newTaskFragment"));
        fragmentTransaction.commit();
        if(task == null)return;
        taskListFragment.addTaskToList(task);
    }
}
