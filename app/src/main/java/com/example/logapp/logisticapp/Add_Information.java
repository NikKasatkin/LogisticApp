package com.example.logapp.logisticapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/*                  COLUMN_DATENOW + " long, " +
                    COLUMN_NAME + " text, " +
                    COLUMN_WEIGHT + " integer, " +
                    COLUMN_TAMESTART + " long, " +
                    COLUMN_TIMETND + " long, " +
                    COLUMN_DISTANCE + " long, " +
                    COLUMN_LATITUBE + " long, " +
                    COLUMN_LONGITUBE + " long" +                */

public class Add_Information extends AppCompatActivity {

    String dist;


    private static final int CM_DELETE_ID = 1;
    ListView lvData;
    DB db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;
    EditText etT2, etT1, etLinLon, etDestance, etAdres, etDtime, etWeight, etFIO;
    String longit, latit;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__information);

        etT2 = (EditText) findViewById(R.id.etT2);
        etT1 = (EditText) findViewById(R.id.etT1);
        etLinLon = (EditText) findViewById(R.id.etLinLon);
        etDestance = (EditText) findViewById(R.id.etDestance);
        etAdres = (EditText) findViewById(R.id.etAdres);
        etDtime = (EditText) findViewById(R.id.etDtime);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etFIO = (EditText) findViewById(R.id.etFIO);

        Toast.makeText(getApplicationContext(), "опеределите Точку на КАРТЕ", Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
        dist = intent.getStringExtra("dest");
        try{
        dist = dist.substring(0,4);}
        catch (Exception e){
            dist = dist;
        }
        //Toast.makeText(getApplicationContext(), "расстояние" + dist, Toast.LENGTH_LONG).show();
        longit = intent.getStringExtra("longit");
        latit = intent.getStringExtra("latit");
        etDestance.setText(dist);
        etLinLon.setText(longit);

        //DIST_DOUBLE = Double.parseDouble(DIST);
        // open DB
        db = new DB(this);
        db.open();

        // take cursor
        cursor = db.getAllData();

        // create ListView
        String[] from = new String[] {DB.COLUMN_DATENOW, DB.COLUMN_NAME, DB.COLUMN_ADRES, DB.COLUMN_WEIGHT, DB.COLUMN_TAMESTART, DB.COLUMN_TIMETND, DB.COLUMN_DISTANCE, DB.COLUMN_LATITUBE, DB.COLUMN_LONGITUBE};
        int[] to = new int[] { R.id.tvOne, R.id.tvTwo,R.id.tvAdres, R.id.tvThree, R.id.tvFour, R.id.tvFive , R.id.tvSix , R.id.tvSeven , R.id.tvEight};

        // create adapter and ListView
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);

        // add menu
        registerForContextMenu(lvData);
    }

    public void onButtonMAP(View view) {
        //goto AddActivity
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /*  cv.put(COLUMN_DATENOW, orderS); //long
        cv.put(COLUMN_NAME, orderS); // text
        cv.put(COLUMN_WEIGHT, orderS); // int
        cv.put(COLUMN_TAMESTART, orderS); //long
        cv.put(COLUMN_TIMETND, orderS); //long
        cv.put(COLUMN_DISTANCE, name); //long
        cv.put(COLUMN_LATITUBE, amount); //long
        cv.put(COLUMN_LONGITUBE, amount); //long    */

    public void onButtonADD(View view) {
        //goto AddActivity
        String timeEnd = etT2.getText().toString();
        String timeStart = etT1.getText().toString();
        String distance = etDestance.getText().toString();
        String adres = etAdres.getText().toString();
        String dateNow = etDtime.getText().toString();
        String fio = etFIO.getText().toString();
        Integer weight = Integer.parseInt(etWeight.getText().toString());

        // add info
        db.addRec(dateNow,fio,adres,weight,timeStart,timeEnd,distance,latit,longit);
        //goto main
        Intent intent = new Intent(this, Add_Information.class);
        startActivity(intent);
    }

    public void onButtonINFO(View view) {
        Intent intent = new Intent(this, Information.class);
        startActivity(intent);
    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "УДАЛИТЬ");
        //menu.add(0, v.getId(), 0, "ИЗМЕНИТЬ");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // teke data from list
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // take id from data and give id to DB
            db.delRec(acmi.id);
            cursor.requery();
            return true;
        }/*else if (item.getTitle()=="ИЗМЕНИТЬ"){
            // teke data from list
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // take id from data and give id to "long _id"
            long _id = acmi.id;
            //long _id update from String id
            String id = String.valueOf(_id);
            //give id in Update.class
            Intent modify_intent = new Intent(getApplicationContext(), UpdateDB.class);
            modify_intent.putExtra("nid", id);
            startActivity(modify_intent);
        }*/
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        // close DB
        db.close();
    }
}


