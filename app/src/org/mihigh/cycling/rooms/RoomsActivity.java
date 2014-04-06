package org.mihigh.cycling.rooms;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.mihigh.cycling.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms);
    }

    public void createNewRoom(View view) {
        System.out.println("in createNewRoom");
        Intent i = new Intent(getApplicationContext(), NewRoomActivity.class);
        startActivity(i);
    }

    public void getOldRooms(View view) {
        System.out.println("in getOldRooms");
    }

    public void getInvitations(View view) {
        System.out.println("in getInvitations");
    }
}