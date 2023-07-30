package com.example.businix.utils;

import com.example.businix.models.LeaveRequestDetail;

import java.util.Date;

public class StatData {
    public static class AttendanceData {
        private double workHours;
        private double overTimeHours;
        private double lateHours;
        private Date date;

        public double getOverTimeHours() {
            return overTimeHours;
        }

        public void setOverTimeHours(double overTimeHours) {
            this.overTimeHours = overTimeHours;
        }

        public double getWorkHours() {
            return workHours;
        }

        public void setWorkHours(double workHours) {
            this.workHours = workHours;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
        public double getLateHours() {
            return lateHours;
        }

        public void setLateHours(double lateHours) {
            this.lateHours = lateHours;
        }
    }
}
