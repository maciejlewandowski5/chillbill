package com.example.chillbill.helpers;

import androidx.annotation.NonNull;

import com.example.chillbill.model.Bill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Objects;

public class FirestoreHelper {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    OnGetDocument onGetDocument;
    OnDocumentsProcessingFinished onDocumentsProcessingFinished;
    OnError onError;
    OnTaskSuccessful onTaskSuccessful;
    OnProductListUpdateSuccess onProductListUpdateSuccess;
    OnCategoryPercentageUpdateSuccess onCategoryPercentageUpdateSuccess;

    public FirestoreHelper(OnGetDocument onGetDocument, OnDocumentsProcessingFinished onDocumentsProcessingFinished, OnError onError, OnTaskSuccessful onTaskSuccessful, OnProductListUpdateSuccess onProductListUpdateSuccess, OnCategoryPercentageUpdateSuccess onCategoryPercentageUpdateSuccess) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.onGetDocument = onGetDocument;
        this.onDocumentsProcessingFinished = onDocumentsProcessingFinished;
        this.onError = onError;
        this.onTaskSuccessful = onTaskSuccessful;
        this.onProductListUpdateSuccess = onProductListUpdateSuccess;
        this.onCategoryPercentageUpdateSuccess = onCategoryPercentageUpdateSuccess;
    }

    public FirestoreHelper(FirestoreHelperListener firestoreHelperListener) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.onGetDocument = firestoreHelperListener;
        this.onDocumentsProcessingFinished = firestoreHelperListener;
        this.onError = firestoreHelperListener;
        this.onTaskSuccessful = firestoreHelperListener;
        this.onProductListUpdateSuccess = firestoreHelperListener;
        this.onCategoryPercentageUpdateSuccess = firestoreHelperListener;
    }

    private void get(DocumentReference documentReference) {
        documentReference.get()
                .addOnSuccessListener((DocumentSnapshot documentSnapshot) -> {
                    onTaskSuccessful.onTaskSuccessful();
                    onGetDocument.onGetDocument(documentSnapshot.toObject(Bill.class));
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
                            onTaskSuccessful.onTaskSuccessful();
                            try {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    onGetDocument.onGetDocument(document.toObject(Bill.class));
                                }
                            } catch (NullPointerException e) {
                                onError.onError(task.getException());
                            }
                            onDocumentsProcessingFinished.onDocumentsProcessingFinished();
                        } else {
                            onError.onError(task.getException());
                        }
                    }
                });
    }

    public void updateBill(Bill bill) {
        getBillReference(bill.getId()).update("productList", bill.getProductList()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onProductListUpdateSuccess.onSuccessProductListUpdate();

            }
        });
        getBillReference(bill.getId()).update("categoryPercentage", bill.getCategoryPercentage()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onCategoryPercentageUpdateSuccess.onSuccessCategoryPercentageUpdate();
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

    public DocumentReference getBillReference(String billId) {
        return db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills").document(billId);
    }

    public void loadBill(String billId) {
        DocumentReference documentReference = getBillReference(billId);
        get(documentReference);

    }

    public void loadBills(Date from, Date to) {
        Query query = db.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("Bills")
                .whereGreaterThanOrEqualTo("date", from)
                .whereLessThanOrEqualTo("date", to);
        get(query);
    }


    public interface OnTaskSuccessful {
        void onTaskSuccessful();
    }

    public interface OnGetDocument {
        void onGetDocument(Bill bill);
    }

    public interface OnDocumentsProcessingFinished {
        void onDocumentsProcessingFinished();
    }

    public interface OnError {
        void onError(Exception e);
    }

    public interface OnProductListUpdateSuccess {
        void onSuccessProductListUpdate();
    }

    public interface OnCategoryPercentageUpdateSuccess {
        void onSuccessCategoryPercentageUpdate();
    }

    public interface FirestoreHelperListener extends OnTaskSuccessful, OnGetDocument, OnDocumentsProcessingFinished, OnError, OnProductListUpdateSuccess, OnCategoryPercentageUpdateSuccess {
        @Override
        default void onSuccessCategoryPercentageUpdate() {
        }

        @Override
        default void onSuccessProductListUpdate() {
        }

        @Override
        default void onError(Exception e) {
        }

        @Override
        default void onDocumentsProcessingFinished() {
        }

        @Override
        default void onGetDocument(Bill bill) {
        }

        @Override
        default void onTaskSuccessful() {
        }
    }


}



