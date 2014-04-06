package org.mihigh.cycling.rooms;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.mihigh.cycling.R;
import org.mihigh.cycling.commons.Constants;
import org.mihigh.cycling.location.MapActivity;

public class NewRoomActivity extends Activity {
    private EditText newRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_room);

        newRoomName = (EditText)findViewById(R.id.room_name);
    }

    public void createRoom(View view) {
        System.out.println("in createNewRoom");

        Constants.ROOM_ID = newRoomName.getText().toString();

        Intent i = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(i);
    }
}