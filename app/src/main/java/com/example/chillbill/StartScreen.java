package com.example.chillbill;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.chillbill.helpers.Utils;

import java.io.FileNotFoundException;

public class StartScreen extends AppCompatActivity {

    StartScreenHelper startScreenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        startScreenHelper = new StartScreenHelper(this);
        startScreenHelper.initialize();
        startScreenHelper.setProgressBarInvisible();

        Utils.checkCameraPermission(this, StartScreenHelper.CAMERA_REQUEST_CODE);

        startScreenHelper.getJasonReq().getRecipesInfo("naleśniki", this);

        // Perform transactions
        PieChartFragment pieChartFragment = PieChartFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.bounce_interpolator,
                android.R.anim.bounce_interpolator);
        transaction.add(R.id.pieChartFragmentStart, pieChartFragment);
        transaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startScreenHelper.getFirestoreHelper().loadBills(StartScreenHelper.NUMBER_OF_LATEST_HIST_ITEMS);
    }


    public void dispatchTakePictureIntent(View view) {
        startScreenHelper.getMenu().setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().equals(getResources().getString(R.string.add_manually))) {
                // TODO: Create module for manual bill adding;
                startScreenHelper.setProgressBarVisible();
                Utils.toastMessage(getString(R.string.future_feature), this);
                startScreenHelper.setProgressBarInvisible();
            } else if (item.getTitle().toString().equals(getResources().getString(R.string.take_from_galery))) {
                startScreenHelper.getImageHelper().startGallery();
            } else {
                startScreenHelper.getImageHelper().startCamera();
            }
            return false;
        });
        startScreenHelper.getMenu().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == StartScreenHelper.CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.toastMessage(getString(R.string.camera_permission_granted), this);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, StartScreenHelper.CAMERA_REQUEST);
            } else {
                Utils.toastMessage(getString(R.string.camera_permissons_denied), this);
                startScreenHelper.getAddBill().setClickable(true);
                startScreenHelper.setProgressBarInvisible();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StartScreen that = this;
        if (resultCode == RESULT_OK) {
            Uri file = Uri.EMPTY;
            if (requestCode == 0) { // taking picture with camera
                try {
                    file = Uri.fromFile(startScreenHelper.getImageHelper().getCurrentPhoto());
                } catch (FileNotFoundException e) {
                    Utils.toastError(that);
                    startScreenHelper.setProgressBarInvisible();
                    return;
                }
            } else if (requestCode == 1) { // selecting from gallery
                file = data.getData();
            }
            startScreenHelper.getImageHelper().sendToStorage(file, startScreenHelper.getStringReq(),startScreenHelper);
        }
    }


    public void startHistory(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    public void startChartsPage(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }


    public void logOut(View view) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenu().add(R.string.log_out);
        menu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().equals(getResources().getString(R.string.log_out))) {
                startScreenHelper.getAccountHelper().signOut(StartScreenHelper.TAG);
            }
            return false;
        });
        menu.show();
    }


}