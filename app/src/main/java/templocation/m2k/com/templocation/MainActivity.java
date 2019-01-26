package templocation.m2k.com.templocation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 201;

    private FusedLocationProviderClient
            mFusedLocationProviderClient;

    String
            mLocation = null;

    Button
            mGetLocation_Button,
            mCheckPermission_Button,
            mRequestPermission_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(MainActivity.this, "Location: " + mLocation, Toast.LENGTH_LONG).show();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if (checkPermission())
            getLocation();

        else
            showMessageOKCancel("Please Grant the Permissions",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                requestPermission();
                        }
                    });

        mGetLocation_Button = findViewById(R.id.mainActivity_getlocation_button);
        mCheckPermission_Button = findViewById(R.id.mainActivity_checkPermission_button);
        mRequestPermission_Button = findViewById(R.id.mainActivity_requestPermission_button);

        mGetLocation_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocation.equals("location not set"))
                    Toast.makeText(MainActivity.this, "Location: " + mLocation, Toast.LENGTH_LONG).show();
                else
                    getLocation();
            }
        });

    }

    private void getLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLocation = location != null ? location.toString() : "location not set";

                    }
                });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted) {
                    Toast.makeText(MainActivity.this, "Permission Granted!!!", Toast.LENGTH_LONG).show();
                    getLocation();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showMessageOKCancel("Permissions are mandate!!!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                requestPermission();
                                        }
                                    });

                            return;
                        }
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(String Dialogmassage, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(Dialogmassage)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
