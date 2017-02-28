package fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.havayi.todo.R;

import java.util.ArrayList;

import db.DBManager;
import receiver.AlarmReceiver;
import task.Task;
import task.TaskAdapter;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment {

    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private IOnNewTaskClickListener mOnActionClickListener;
    private IOnItemClickListener mOnItemClickListener;
    private RecyclerView rv;
    private boolean isOnActionMode = false;
    private ActionMode actionMode;
    private ActionModeCallBack actionModeCallBack = new ActionModeCallBack();
    DBManager dbManager;


    public void setOnItemClickListener(IOnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnActionClickListener(IOnNewTaskClickListener mOnActionClickListener) {
        this.mOnActionClickListener = mOnActionClickListener;
    }

    public TaskListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.new_task_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnActionClickListener != null)
                    mOnActionClickListener.onNewTaskClick(null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  rootView =  inflater.inflate(R.layout.task_list_fragment_layout, container,false);

        rv = (RecyclerView) rootView.findViewById(R.id.task_list_view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        taskAdapter = new TaskAdapter(taskList, dbManager);
        taskAdapter.sort();
        rv.setAdapter(taskAdapter);
        taskAdapter.setItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                mOnItemClickListener.onItemClick(task);
            }
        });

        taskAdapter.setItemLongClickListener(new TaskAdapter.IOnItemLongClickListener() {
            @Override
            public void onItemLongClick() {
                if(actionMode == null)
                    actionMode = getActivity().startActionMode(actionModeCallBack);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DBManager(getContext());
        taskList = dbManager.getTasks();
    }

    public interface IOnNewTaskClickListener {
        void onNewTaskClick(Task task);
    }

    public interface IOnItemClickListener{
        void onItemClick(Task task);
    }

//    private void init()
//    {
//        taskList.add(new Task("asd", "asd", new DatePicker(getActivity()), new TimePicker(getActivity())));
//        taskList.add(new Task("gghrth", "bdfbdfffg", new DatePicker(getActivity()), new TimePicker(getActivity())));
//        taskList.add(new Task("ytiutyi", "bdfbdfbdfb", new DatePicker(getActivity()), new TimePicker(getActivity())));
//        taskList.add(new Task("tyity", "asbdfbdfbdfbd", new DatePicker(getActivity()), new TimePicker(getActivity())));
//        taskList.add(new Task("tyityi", "fdgbfd", new DatePicker(getActivity()), new TimePicker(getActivity())));
//    }

    public void deleteTaskByID(long ID) {
        taskAdapter.deleteTaskByID(ID);
    }

    public void addTaskToList(Task task)
    {
        taskAdapter.addOrUpdateTask(task);
//        boolean isNew = true;
//        for (int i = 0; i <taskList.size(); i++)
//        {
//            if(task.getID() == taskList.get(i).getID()) {
//                taskList.set(i, task);
//                isNew = false;
//            }
//        }
//        if (isNew)
//            taskList.add(task);
//        taskAdapter.sort();
//        taskAdapter.notifyDataSetChanged();
    }

    private class ActionModeCallBack implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            isOnActionMode = true;
            taskAdapter.isOnActionMode = true;
            taskAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId())
            {
                case R.id.delete_list_view_menu:
                    taskAdapter.deleteChecked();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            isOnActionMode = false;
            taskAdapter.isOnActionMode = false;
            taskAdapter.notifyDataSetChanged();
        }
    }
}
