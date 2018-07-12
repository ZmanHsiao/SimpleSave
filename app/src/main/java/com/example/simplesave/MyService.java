package com.example.simplesave;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseManager.pushUser(Main2Activity.user);
        System.out.println("GAYYYYYYYYYYYY123123" + Main2Activity.user.getBudgetPlan().getRemBudget());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("GAYYYYYYYYYYYY" + + Main2Activity.user.getBudgetPlan().getRemBudget());
        FirebaseManager.pushUser(Main2Activity.user);
        stopSelf();
    }
}
