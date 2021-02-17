package com.example.chillbill.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chillbill.model.Bill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    public FirestoreHelper(OnGetDocument onGetDocument, OnDocumentsProcessingFinished onDocumentsProcessingFinished, OnError onError, OnTaskSuccessful onTaskSuccessful) {
        FirebaseAuth fa = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.onGetDocument = onGetDocument;
        this.onDocumentsProcessingFinished = onDocumentsProcessingFinished;
        this.onError = onError;
        this.onTaskSuccessful = onTaskSuccessful;
    }

    private void get(DocumentReference documentReference) {
        documentReference.get()
                .addOnSuccessListener((DocumentSnapshot documentSnapshot) -> {
                    onTaskSuccessful.OnTaskSuccessful();
                    onGetDocument.onGetDocument(documentSnapshot);
                    onDocumentsProcessingFinished.onDocumentsProcessingFinished();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onError.onError(e);
                    }
                });


    }

    private void get(Query query) {
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

    public void loadBills(int limit) {
        Query query = db.collection("Users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).collection("Bills").orderBy("date", Query.Direction.DESCENDING).limit(limit);
        get(query);
    }

    public void loadBills() {
        Query query = db.collection("Users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).collection("Bills").orderBy("date", Query.Direction.DESCENDING);
        get(query);
    }

    public void loadBill(String billId) {
        DocumentReference documentReference = db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(billId);
        get(documentReference);

    }

    public void loadBills(Date from, Date to){
        Query query = db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills")
                .whereGreaterThanOrEqualTo("date", from)
                .whereLessThanOrEqualTo("date", to);
        get(query);
    }


    public interface OnTaskSuccessful {
        void OnTaskSuccessful();
    }

    public interface OnGetDocument {
        void onGetDocument(DocumentSnapshot document);

    }

    public interface OnDocumentsProcessingFinished {
        void onDocumentsProcessingFinished();
    }

    public interface OnError {
        void onError(Exception e);
    }


    public static Task<QuerySnapshot> getBills() {
        FirebaseAuth fa = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("Users").document(fa.getCurrentUser().getUid()).collection("Bills");
        return reference.get();
    }


}



