package ai.fritz.poseestimationdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ai.fritz.camera.MainActivity;

public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

    }

    public void fun2(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }

}
