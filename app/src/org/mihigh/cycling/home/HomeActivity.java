package org.mihigh.cycling.home;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import org.mihigh.cycling.R;
import org.mihigh.cycling.rooms.RoomsActivity;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    public void goToRooms(View view) {
        System.out.println("in goToRooms");
        Intent i = new Intent(getApplicationContext(), RoomsActivity.class);
        startActivity(i);
    }

    public void goToActivities(View view) {
        System.out.println("in goToActivities");
    }

    public void goToPersonalInfo(View view) {
        System.out.println("in goToPersonalInfo");
    }

    public void goToSettings(View view) {
        System.out.println("in goToSettings");
    }
}