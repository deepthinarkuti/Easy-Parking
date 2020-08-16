package siokouros.filippos.phonepark.Model;

import java.sql.Time;

public class Parking {

    private String Location;
    private String startTime;
    private String hours;
    private String minutes;
    private String date;
    private String regNumber;
    private String completed;
    private String amount;
    private String endTimeMilis;
    public Parking() {
    }

    public Parking(String location, String startTime, String hours, String minutes, String date, String regNumber, String completed, String amount) {
        Location = location;
        this.startTime = startTime;
        this.hours = hours;
        this.minutes = minutes;
        this.date = date;
        this.regNumber = regNumber;
        this.completed = completed;
        this.amount = amount;
    }


    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEndTimeMilis() {
        return endTimeMilis;
    }

    public void setEndTimeMilis(String endTimeMilis) {
        this.endTimeMilis = endTimeMilis;
    }
}






