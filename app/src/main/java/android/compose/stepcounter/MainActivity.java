package android.compose.stepcounter;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private Boolean running = false;
    float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private SensorManager s;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        s = (SensorManager) getSystemService(SENSOR_SERVICE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            boolean a = checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED;
            if (!a) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        101);
            }
        }

        loadData();
        resetSteps();

    }

    private void loadData() {
        // In this function we will retrieve data
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key1", 0f);

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber");

        previousTotalSteps = savedNumber;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        TextView tv_stepsTaken = findViewById(R.id.tv_stepsTaken);

        if (running) {
            totalSteps = event.values[0];

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            int a = (int) totalSteps;
            int b = (int) previousTotalSteps;
            int currentSteps = a - b;

            // It will show the current steps to the user
            String str=Integer.toString(currentSteps);
            tv_stepsTaken.setText(str);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor stepSensor = s.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show();
        } else {
            s.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }


    public void resetSteps() {
        TextView tv_stepsTaken = findViewById(R.id.tv_stepsTaken);


        tv_stepsTaken.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Long tap to reset steps", Toast.LENGTH_SHORT).show();
            }
        });

        tv_stepsTaken.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                previousTotalSteps = totalSteps;
                String stp=Integer.toString(0);
                tv_stepsTaken.setText(stp);
                saveData();
                return true;
            }
        });


    }

    private void saveData() {

        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key1", previousTotalSteps);
        editor.apply();
    }
}




