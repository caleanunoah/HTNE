package ai.fritz.poseestimationdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class frontpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
    }
    public void fun(View V){
        Intent i = new Intent(this,InstructionActivity.class);
        startActivity(i);
    }
}
