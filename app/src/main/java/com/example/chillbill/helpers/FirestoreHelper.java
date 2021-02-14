package com.example.chillbill.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chillbill.model.Bill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FirestoreHelper {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    OnGetDocument onGetDocument;
    OnDocumentsProcessingFinished onDocumentsProcessingFinished;
    OnError onError;
    OnTaskSuccessful onTaskSuccessful;

    public FirestoreHelper(FirebaseAuth firebaseAuth, FirebaseFirestore db, OnGetDocument onGetDocument, OnDocumentsProcessingFinished onDocumentsProcessingFinished, OnError onError, OnTaskSuccessful onTaskSuccessful) {
        this.firebaseAuth = firebaseAuth;
        this.db = db;
        this.onGetDocument = onGetDocument;
        this.onDocumentsProcessingFinished = onDocumentsProcessingFinished;
        this.onError = onError;
        this.onTaskSuccessful = onTaskSuccessful;
    }


    private void getFromQuery(Query query){
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            onTaskSuccessful.OnTaskSuccessful();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                onGetDocument.onGetDocument(document);
                            }

                            onDocumentsProcessingFinished.onDocumentsProcessingFinished();
                        } else {
                            onError.onError(task.getException());
                        }
                    }
                });
    }
    public void loadHistoryItems(int limit) {
        Query query = db.collection("Users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).collection("Bills").orderBy("date", Query.Direction.DESCENDING).limit(limit);
        getFromQuery(query);
    }
    public void loadHistoryItems() {
        Query query = db.collection("Users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).collection("Bills").orderBy("date", Query.Direction.DESCENDING);
        getFromQuery(query);
    }





    public interface OnTaskSuccessful {
        void OnTaskSuccessful();
    }

    public interface OnGetDocument{
        void onGetDocument(QueryDocumentSnapshot document);
    }
    public interface OnDocumentsProcessingFinished{
        void onDocumentsProcessingFinished();
    }
    public interface  OnError{
        void onError(Exception e);
    }

    public static Task<QuerySnapshot> getBillsInRange(Date from, Date to) {
        FirebaseAuth fa = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return db.collection("Users").document(fa.getCurrentUser().getUid()).collection("Bills")
                .whereGreaterThanOrEqualTo("date", from)
                .whereLessThanOrEqualTo("date", to)
                .get();
    }

    public static Task<QuerySnapshot> getBills() {
        FirebaseAuth fa = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Users").document(fa.getCurrentUser().getUid()).collection("Bills");
        return reference.get();
    }

}

/*
  public ArrayList<Bill> loadAllHistoryItemExtended() {
        //TODO:: Add get request
        bills = new ArrayList<>();


        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Bill bill = document.toObject(Bill.class);
                                bills.add(bill);
                            }
                            infiniteScroller.populate(bills);

                        } else {
                            Log.w("History", "Error getting documents.", task.getException());
                        }
                    }
                });

        return bills;
    }
 */

