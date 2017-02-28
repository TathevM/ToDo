package task;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Havayi on 17-Dec-16.
 */

public class Task implements Parcelable {

    public static final int ALERT_1_DAY = 86400000;
    public static final int ALERT_1_HOUR = 3600000;
    public static final int ALERT_15_MINS = 900000;

    private String description;
    private Date date;
    private String title;
    private long alert;
    private long ID;
    private boolean checkedForDelete;

    public Task(String title , String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
        //getSimpleDateFromPicker(datePicker, timePicker);
        this.alert = -1;
        //ID = Calendar.getInstance().getTime().getTime();
        ID = -1;
        checkedForDelete = false;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getID() {
        return ID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public void setDate(DatePicker datePicker, TimePicker timePicker) {
//        getSimpleDateFromPicker(datePicker,timePicker);
//    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCheckedForDelete() {
        return checkedForDelete;
    }

    public void setCheckedForDelete(boolean checkedForEdit) {
        this.checkedForDelete = checkedForEdit;
    }

    public void setAlert(long alert) {
        this.alert = alert;
    }

    public long getAlert() {
        return alert;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
    public String getTitle(){
        return title;
    }



    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(ID);
        out.writeString(description);
        out.writeString(title);
        out.writeLong(date != null ? date.getTime() : -1);
        out.writeLong(alert);
    }

    public static final Parcelable.Creator<Task> CREATOR
            = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    private Task(Parcel in) {
        ID = in.readLong();
        description = in.readString();
        title = in.readString();
        long tempDate = in.readLong();
        date = tempDate == -1 ? null : new Date(tempDate);
        alert = in.readLong();
    }
}
