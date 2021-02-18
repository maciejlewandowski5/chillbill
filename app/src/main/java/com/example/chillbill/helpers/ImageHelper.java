package com.example.chillbill.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.android.volley.toolbox.StringRequest;
import com.example.chillbill.StartScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.Empty;
import com.google.protobuf.NullValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ImageHelper {

    private File currentPhoto;
    private CameraStared cameraStared;
    private GalleryStarted galleryStarted;
    private Activity activity;

    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;

    public ImageHelper(CameraStared cameraStared, GalleryStarted galleryStarted, StartScreen activity) {
        this.activity = activity;
        this.cameraStared = cameraStared;
        this.galleryStarted = galleryStarted;
        this.currentPhoto = null;

        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void startCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

                Uri photoURI = FileProvider.getUriForFile(activity.getApplicationContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, 0);
                cameraStared.cameraStared();//setProgressBarVisible();
            } catch (IOException ex) {
                Utils.toastError(activity.getApplicationContext());
            }
        }
    }

    public void startGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto, 1);
        galleryStarted.galleryStarted();//setProgressBarVisible();
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp =  // Date format used for storage, no need to localize it.
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(timeStamp, ".jpg", storageDir);

        currentPhoto = image;
        return image;
    }

    public File getCurrentPhoto() throws FileNotFoundException {
        if (currentPhoto == null)
            throw new FileNotFoundException("Photo file was not initialized, first run startCamera()");
        return currentPhoto;
    }

    public void sendToStorage(Uri file,VolleyStringHelper stringReq) {
        StorageReference riversRef = mStorageRef.child("/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/" + file.hashCode());
        Uri finalFile = file;
        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                stringReq.requestBillParsing(String.valueOf(finalFile.hashCode()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((StartScreen)activity).setProgressBarInvisible();
                Utils.toastError(activity.getApplicationContext());
            }
        });

    }

    public interface CameraStared {
        void cameraStared();
    }

    public interface GalleryStarted {
        void galleryStarted();
    }
}
