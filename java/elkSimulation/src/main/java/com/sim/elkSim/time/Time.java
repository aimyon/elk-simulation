package com.sim.elkSim.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// 时间类型转换
public class Time {
    private long time;             // currentTimeMills()
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Time(long time){this.time = time;}

    public long getTotalSeconds(long time) {
        return time / 1000;
    }

    public long getCurrentSeconds(long time) {
        return getTotalSeconds(time) % 60;
    }

    public long getTotalMinutes(long time) {
        return getTotalSeconds(time) / 60;
    }

    public long getCurrentMinutes(long time) {
        return getTotalMinutes(time) % 60;
    }

    public long getTotalHours(long time) {
        return getTotalMinutes(time) / 60;
    }

    public long getCurrentHours(long time) {
        return getTotalHours(time) % 24;
    }

    public long getTotalDays(long time) {
        return getTotalHours(time) / 24;
    }

    public void setTime(String time) throws ParseException {
        Date date = (Date) sdf.parse(time);
        this.time = date.getTime();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTime(Date time){
        this.time = time.getTime();
    }

    public Date getTime_date(){
        return new Date(this.time);
    }

    public String getTime_string() {
        return sdf.format(getTime_date());
    }

    public long getTime_long() {
        return time;
    }

}
