package com.osu.suzy.mycamera;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends Activity {

    private String[] item={"A","B","C","D"};
    private int[] image = { R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d};
    GridView gv;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv=(GridView)findViewById(R.id.gridView);

        ArrayList<HashMap<String,Object>> photoItems=new ArrayList<HashMap<String,Object>>();
        //add photo && name info
        int len=item.length;
        for(int i=0; i<len;i++){
            HashMap<String,Object> map=new HashMap<String,Object>();
            map.put("image",image[i]);
            map.put("item",item[i]);
            photoItems.add(map);
        }
        String[] from={"image","item"};
        int[] to={R.id.iv_item,R.id.tv_item};

        adapter =new SimpleAdapter(this,photoItems,R.layout.grid_item,from,to);

        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //show big picture
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
