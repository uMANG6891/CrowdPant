package com.umang.crowdpant;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.umang.crowdpant.ui.receiver.SampleAlarmReceiver;

/**
 * Created by umang on 10/02/16.
 */
public class CrowdPantApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SampleAlarmReceiver alarm = new SampleAlarmReceiver();
        alarm.setAlarm(this);
    }
}
