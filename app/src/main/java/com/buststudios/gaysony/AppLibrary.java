package com.buststudios.gaysony;

import android.content.Intent;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AppLibrary {

    public static User deserializeUser(Intent intent) {
        User user = (User) intent.getSerializableExtra("zach");
        user.getBudgetPlan().setStartDate(new Timestamp((Date) intent.getSerializableExtra("startDate")));
        user.getBudgetPlan().setEndDate(new Timestamp((Date) intent.getSerializableExtra("endDate")));
        //set large transactions
        HashMap<Date, Transaction> largeTransactions = (HashMap<Date, Transaction>) intent.getSerializableExtra("largeTransactions");
        user.getBudgetPlan().setLargeTransactions(new ArrayList<Transaction>());
        for(Date d : largeTransactions.keySet()){
            largeTransactions.get(d).setTimestamp(new Timestamp(d));
            user.getBudgetPlan().addLargeTransaction(largeTransactions.get(d));
        }
        //set transactions
        HashMap<Date, Transaction> transactions = (HashMap<Date, Transaction>) intent.getSerializableExtra("transactions");
        user.getBudgetPlan().setTransactions(new ArrayList<Transaction>());
        for(Date d : transactions.keySet()){
            transactions.get(d).setTimestamp(new Timestamp(d));
            user.getBudgetPlan().addTransaction(transactions.get(d));
        }
        return user;
    }

    public static String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public static int getDaysDif(Timestamp first, Timestamp second) {
        final int SECONDS_IN_DAY = 86400;
        return (int) ((first.getSeconds() - second.getSeconds()) / SECONDS_IN_DAY + 1);
    }

    public static String getDollarFormat(float f){
        DecimalFormat precision = new DecimalFormat( "0.00" );
        return precision.format(f);
    }

    public static boolean isDateEqual(Timestamp first, Timestamp second){
        Calendar cFirst = Calendar.getInstance();
        cFirst.setTime(first.toDate());
        Calendar cSecond = Calendar.getInstance();
        cSecond.setTime(second.toDate());
        return cFirst.get(Calendar.YEAR) == cSecond.get(Calendar.YEAR)
                && cFirst.get(Calendar.MONTH) == cSecond.get(Calendar.MONTH)
                && cFirst.get(Calendar.DAY_OF_MONTH) == cSecond.get(Calendar.DAY_OF_MONTH);
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
        HashMap<Date, Transaction> largeTransactions = new HashMap<>();
        for(Transaction t : u.getBudgetPlan().getLargeTransactions()){
            largeTransactions.put(t.getTimestamp().toDate(), t);
        }
        intent.putExtra("largeTransactions", largeTransactions);
        HashMap<Date, Transaction> transactions = new HashMap<>();
        for(Transaction t : u.getBudgetPlan().getTransactions()){
            transactions.put(t.getTimestamp().toDate(), t);
        }
        intent.putExtra("transactions", transactions);

        return intent;
    }

    public static String timestampToDateString(Timestamp t){
        DateFormat df = new SimpleDateFormat("MMM dd");
        return df.format(t.toDate());
    }

}