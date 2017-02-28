package fragments;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.havayi.todo.R;

import java.util.Calendar;
import java.util.Date;

import task.Task;

import static android.content.Context.ALARM_SERVICE;

public class NewTaskFragment extends Fragment {




    private IOnSaveButtonClickListener mOnSaveButtonClickListener;
    private DatePicker datePicker;
    private EditText descriptionText;
    private EditText titleText;
    private TimePicker timePicker;
    private CheckBox alertMeCheckBox;
    private RadioGroup radioGroup;
    private Task task;
    private boolean isOnEditState;
    private Menu mMenu;
    private IOnDeleteButtonClickListener mOnDeleteButtonClickListener;
    private IOnBackButtonPressedListener mOnBackButtonPressedListener;

    public NewTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        isOnEditState = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_task_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Date getSimpleDateFromPicker(DatePicker datePicker, TimePicker timePicker)
    {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        int hour;
        int minutes;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minutes = timePicker.getMinute();
        }
        else
        {
            hour  = timePicker.getCurrentHour();
            minutes = timePicker.getCurrentMinute();
        }
        Calendar c = Calendar.getInstance();
        c.set(year,month,day,hour, minutes );
        return c.getTime();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_task_menu:
                isOnEditState = !isOnEditState;
                setEditableState();
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.ok_task_menu:
                save();
                return true;
            case R.id.back_button_menu:
                if(!checkFilledInfo())
                    mOnBackButtonPressedListener.onBackButtonPressed();
                else
                    //TODO pxoel save-i logikan
                    showSaveDialog();
                return true;
            case R.id.delete_task_menu:
                mOnDeleteButtonClickListener.onDeleteButtonCLicked(task.getID());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSaveDialog()
    {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(getActivity());
        saveDialog.setPositiveButton(R.string.save_menu_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                save();
            }
        })
                .setNegativeButton(R.string.dont_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mOnBackButtonPressedListener.onBackButtonPressed();
                    }
                })
                .setTitle("Do you want to save?")
                .show();
    }

    private void setEditableState(){
        descriptionText.setEnabled(isOnEditState);
        titleText.setEnabled(isOnEditState);
        datePicker.setEnabled(isOnEditState);
        timePicker.setEnabled(isOnEditState);
        alertMeCheckBox.setEnabled(isOnEditState);
        radioGroup.setEnabled(isOnEditState);
        radioGroup.setActivated(isOnEditState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mMenu == null)
            mMenu = menu;
        if(task == null)
        {
            mMenu.findItem(R.id.edit_task_menu).setVisible(false);
            mMenu.findItem(R.id.delete_task_menu).setVisible(false);
        }
        else
        {
            mMenu.findItem(R.id.back_button_menu).setVisible(false);
            if (isOnEditState) {
                mMenu.findItem(R.id.ok_task_menu).setVisible(true);
                mMenu.findItem(R.id.edit_task_menu).setVisible(false);
            }
            else {
                mMenu.findItem(R.id.ok_task_menu).setVisible(false);
                mMenu.findItem(R.id.edit_task_menu).setVisible(true);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        descriptionText = (EditText) view.findViewById(R.id.description_text);
        datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        alertMeCheckBox = (CheckBox) view.findViewById(R.id.alert_me_check_box);
        titleText = (EditText) view.findViewById(R.id.task_title_text);
        radioGroup = (RadioGroup) view.findViewById(R.id.alert_me_radio_group);

        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        timePicker.setIs24HourView(true);
        setListeners();

        if(getArguments() != null)
        {
            task = getArguments().getParcelable("task");
            titleText.setText(task.getTitle());
            descriptionText.setText(task.getDescription());

            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            timePicker.setIs24HourView(true);
            Calendar c = Calendar.getInstance();
            c.setTime(task.getDate());

            datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(c.get(Calendar.MINUTE));
            }
            else {
                timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            }

            if(task.getAlert() != -1) {
                alertMeCheckBox.setChecked(true);
                long a = task.getDate().getTime() - task.getAlert();

                radioGroup.setVisibility(View.VISIBLE);
                switch ((int) a) {
                    case Task.ALERT_1_DAY:
                        ((RadioButton) view.findViewById(R.id.radio_button_1_day)).setChecked(true);
                        break;
                    case Task.ALERT_1_HOUR:
                        ((RadioButton) view.findViewById(R.id.radio_button_1_hour)).setChecked(true);
                        break;
                    case Task.ALERT_15_MINS:
                        ((RadioButton) view.findViewById(R.id.radio_button_15_mins)).setChecked(true);
                        break;
                    default:
                        radioGroup.clearCheck();
                        alertMeCheckBox.setChecked(false);
                }
            }
            setEditableState();
        }
    }

    private void setListeners() {
        alertMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    radioGroup.setVisibility(View.VISIBLE);
                else {
                    radioGroup.setVisibility(View.GONE);
                    radioGroup.clearCheck();
                }
            }
        });
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 15)
                    titleText.setTextColor(Color.RED);
                else
                    titleText.setTextColor(Color.BLACK);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        titleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                    hideKeyboard(view);
            }
        });

        descriptionText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                    hideKeyboard(view);
            }
        });
    }

    private void save() {
        if (checkFilledInfo()) {
            if (task == null) {
                task = new Task(titleText.getText().toString(), descriptionText.getText().toString(), getSimpleDateFromPicker(datePicker, timePicker));
            } else {
                task.setTitle(titleText.getText().toString());
                task.setDescription(descriptionText.getText().toString());
                task.setDate(getSimpleDateFromPicker(datePicker, timePicker));
            }
            if (alertMeCheckBox.isChecked()) {
                long t = getAlertTimeFromRadio();
                task.setAlert(task.getDate().getTime() - t);
            } else {
                task.setAlert(-1);
            }
            mOnSaveButtonClickListener.OnSaveButtonClicked(task);
        } else {
            Toast.makeText(getActivity(), "please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }



    private long getAlertTimeFromRadio()
    {
        long a;
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.radio_button_1_day:
                a = Task.ALERT_1_DAY;
                break;
            case R.id.radio_button_1_hour:
                a = Task.ALERT_1_HOUR;
                break;
            case R.id.radio_button_15_mins:
                a = Task.ALERT_15_MINS;
                break;
            default:
                a = -1;
                break;
        }
        return a;
    }

    private void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface IOnSaveButtonClickListener{
        void OnSaveButtonClicked(Task task);
    }

    public interface IOnDeleteButtonClickListener{
        void onDeleteButtonCLicked(long taskID);
    }
    public interface IOnBackButtonPressedListener{
        void onBackButtonPressed();
    }


    public void setOnBackButtonPressedListener(IOnBackButtonPressedListener mOnBackButtonPressedListener) {
        this.mOnBackButtonPressedListener = mOnBackButtonPressedListener;
    }

    public void setOnDeleteButtonClickListener(IOnDeleteButtonClickListener mOnDeleteButtonClickListener) {
        this.mOnDeleteButtonClickListener = mOnDeleteButtonClickListener;
    }

    public void setOnSaveButtonClickListener(IOnSaveButtonClickListener mOnSaveButtonClickListener) {
        this.mOnSaveButtonClickListener = mOnSaveButtonClickListener;
    }

    private boolean checkFilledInfo()
    {
        if(descriptionText.getText().toString().equals("") || titleText.getText().toString().equals(""))
            return false;
        if(alertMeCheckBox.isChecked())
        {
            if(radioGroup.getCheckedRadioButtonId() == -1)
                return false;
        }
        return true;
    }
}
