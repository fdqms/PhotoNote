package com.fdqms.photonote;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
 */

import java.net.URISyntaxException;

public class Masonry extends AppCompatActivity{

    RelativeLayout relativeLayout;
    TextView textView;
    /*
    private Socket socket;
    {
        try {
            socket = IO.socket("http://192.168.1.100:5000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }*/

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masonry);

        textView = findViewById(R.id.textView3);

        /*
        socket.connect();
        socket.on("newUser", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        Integer onlineUye = (Integer) args[0];
                        textView.setText(onlineUye.toString());
                    }
                });
            }
        });
         */

        /*
        socket.on("disUser", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        Integer onlineUye = (Integer) args[0];
                        textView.setText(onlineUye.toString());
                    }
                });
            }
        });*/

        relativeLayout = findViewById(R.id.relative);

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Toast.makeText(getBaseContext(),"x: "+motionEvent.getX(),Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

}