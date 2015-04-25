package com.androider.legacy.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.service.NetService;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.jialin.chat.Message;
import com.jialin.chat.MessageAdapter;
import com.jialin.chat.MessageInputToolBox;
import com.jialin.chat.OnOperationListener;
import com.jialin.chat.Option;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ChatActivity extends ActionBarActivity {

    public static int seller;

    MessageInputToolBox box;
    MessageAdapter adapter;
    HashMap<Integer, ArrayList<String>> faceData = new HashMap<>();
    ArrayList<Option> functionData = new ArrayList<>();
    ArrayList<String> faceNameList = new ArrayList<>();
    ArrayList<Message> messages = new ArrayList<>();
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        seller = getIntent().getIntExtra("talker", -1);
        box = (MessageInputToolBox)findViewById(R.id.message_box);
        box.setOnOperationListener(new OnOperationListener() {
            @Override
            public void send(String content) {
                Message message = new Message(0,1, "Tom", "avatar", "Jerry", "avatar", content, true, true, new Date());
                adapter.getData().add(message);
                list.setSelection(list.getBottom());
            }

            @Override
            public void selectedFace(String content) {

            }

            @Override
            public void selectedFuncation(int index) {

            }
        });

        for(int i = 0; i < 10; i++){
            faceNameList.add("big"+ i);
        }

        faceData.put(R.drawable.em_cate_magic, faceNameList);
        box.setFaceData(faceData);
        for(int i = 0;  i < 5; i++){
            Option takePhoto = new Option(this, "Take", R.drawable.take_photo);
            Option gallery = new Option(this, "gallery", R.drawable.gallery);
            functionData.add(gallery);
            functionData.add(takePhoto);
        }
        box.setFunctionData(functionData);

        list = (ListView) findViewById(R.id.message_list);
        adapter = new MessageAdapter(this, messages);
        list.setAdapter(adapter);

        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hide();
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
