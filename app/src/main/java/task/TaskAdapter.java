package task;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import com.havayi.todo.R;

import db.DBManager;

/**
 * Created by Havayi on 14-Jan-17.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.mViewHolder> {

    private static final int TYPE_TASK = 1, TYPE_DATE = 2;

    private ArrayList<Task> tasks;
    //private Context context;
    private int editablePosition;
    public boolean isOnActionMode = false;
    private DBManager dbManager;
    private OnItemClickListener mItemClickListener;
    private IOnItemLongClickListener mItemLongClickListener;

    public void setItemLongClickListener(IOnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public int getEditablePosition() {
        return editablePosition;
    }

    public TaskAdapter(ArrayList<Task> tasks, DBManager dbManager) {
        this.tasks = tasks;
        this.dbManager = dbManager;
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public Object getItem(int i) {
        return tasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View nView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new mViewHolder(nView);
    }

    @Override
    public void onBindViewHolder(mViewHolder viewHolder, final int position) {

        Task t = (Task) getItem(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

        viewHolder.titleText.setText(t.getTitle());
        viewHolder.descriptionText.setText(t.getDescription());

        viewHolder.dateView.setText(sdf.format(t.getDate()));

        viewHolder.deleteButton.setImageResource(R.drawable.delete_button);
//        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tasks.remove(position);
//                notifyDataSetChanged();
//            }
//        });

        viewHolder.checkBox.setOnCheckedChangeListener(onEditCheckedChangeListener);
        viewHolder.checkBox.setTag(position);
        if(isOnActionMode)
        {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(false);
            viewHolder.deleteButton.setVisibility(View.GONE);
        }
        else {
            viewHolder.deleteButton.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void addOrUpdateTask(Task task) {
        if (task.getID() > -1) {
            dbManager.updateTask(task);
            for (int i = 0; i < tasks.size(); i++) {
                if (task.getID() == tasks.get(i).getID()) {
                    tasks.set(i, task);
                }
            }
        } else {
            long id = dbManager.insertTask(task);
            task.setID(id);
            tasks.add(task);
        }
        sort();
        notifyDataSetChanged();
    }

    public void deleteTaskByID(long ID){

        dbManager.deleteTaskByID(ID);
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getID() == ID) {
                tasks.remove(i);
                notifyDataSetChanged();
                return;
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener onEditCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            ((Task) getItem((Integer) compoundButton.getTag())).setCheckedForDelete(b);
        }
    };

    public void sort()
    {
        Collections.sort(tasks,new TaskComparator());
    }

    class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView dateView;
        CheckBox checkBox;
        TextView titleText;
        TextView descriptionText;
        ImageButton deleteButton;
        CardView cardView;
        mViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.date_text_view_adapter);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.task_check_box_adapter);
            titleText = (TextView) itemView.findViewById(R.id.title_text_view_adapter);
            descriptionText = (TextView) itemView.findViewById(R.id.description_text_view_adapter);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button_adapter);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClick(tasks.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {
            mItemLongClickListener.onItemLongClick();
            return true;
        }
    }

    public void deleteChecked()
    {
        //ArrayList<String> deleteIDs = new ArrayList<>();
        for(int i = 0; i<tasks.size();)
        {
            if(tasks.get(i).isCheckedForDelete()) {
                deleteTaskByID(tasks.get(i).getID());
                //deleteIDs.add(String.valueOf(tasks.get(i).getID())); //TODO dzel multiple row jnjel@
                //tasks.remove(i);
            }
            else i++;
        }
      //  dbManager.deleteMultipleRowsByID(deleteIDs.toArray(new String[0]));
        notifyDataSetChanged();
    }

    public interface IOnItemLongClickListener {
        void onItemLongClick();
    }

    public interface OnItemClickListener{
        void onItemClick(Task task);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
