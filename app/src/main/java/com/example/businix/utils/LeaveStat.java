package com.example.businix.utils;

public class LeaveStat {
    private int totalAllDay;
    private int morningShift;
    private int afternoonShift;

    public LeaveStat(){
        this.afternoonShift = 0;
        this.morningShift = 0;
        this.totalAllDay = 0;
    }

    public int getTotalAllDay() {
        return totalAllDay;
    }

    public void setTotalAllDay(int totalAllDay) {
        this.totalAllDay = totalAllDay;
    }

    public int getMorningShift() {
        return morningShift;
    }

    public void setMorningShift(int morningShift) {
        this.morningShift = morningShift;
    }

    public int getAfternoonShift() {
        return afternoonShift;
    }

    public void setAfternoonShift(int afternoonShift) {
        this.afternoonShift = afternoonShift;
    }
}
