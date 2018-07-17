package com.example.simplesave;

import android.content.Intent;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AppLibrary {

    public static User deserializeUser(Intent intent) {
        User user = (User) intent.getSerializableExtra("zach");
        user.getBudgetPlan().setStartDate(new Timestamp((Date) intent.getSerializableExtra("startDate")));
        user.getBudgetPlan().setEndDate(new Timestamp((Date) intent.getSerializableExtra("endDate")));
        HashMap<Date, Transaction> m = (HashMap<Date, Transaction>) intent.getSerializableExtra("transactions");
        user.getBudgetPlan().setTransactions(new ArrayList<Transaction>());
        for(Date d : m.keySet()){
            m.get(d).setTimestamp(new Timestamp(d));
            user.getBudgetPlan().addTransaction(m.get(d));
        }
        return user;
    }

    public static String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public static int getDaysDif(Timestamp first, Timestamp second) {
        long firstSeconds = first.getSeconds();
        long secondSeconds = second.getSeconds();
        return (int) TimeUnit.SECONDS.toDays(Math.abs(firstSeconds - secondSeconds));
    }

    public static boolean isDateEqual(Timestamp first, Timestamp second){
        return getDaysDif(first, second) == 0;
    }

    public static Timestamp getTimestampWithoutTime(Timestamp t) {
        Calendar c = Calendar.getInstance();
        c.setTime(t.toDate());
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return new Timestamp(c.getTime());
    }

    public static Timestamp getToday() {
        Timestamp today = getTimestampWithoutTime(new Timestamp(new Date()));
        return today;
    }

    public static void pushUser(User u) {
        String testdoc = u.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(testdoc).set(u, SetOptions.merge());
    }

    public static Intent serializeUser(User u, Intent intent) {
        intent.putExtra("zach", u);
        intent.putExtra("startDate", u.getBudgetPlan().getStartDate().toDate());
        intent.putExtra("endDate", u.getBudgetPlan().getEndDate().toDate());
        intent.putExtra("transactions", u.getBudgetPlan().getTransactionsMap());

        return intent;
    }

}