package kale.eventbus.test;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import kale.eventbus.BroadEventBus;
import kale.eventbus.R;
import kale.eventbus.annotation.EventSubscriber;

import static kale.eventbus.test.EventTag.CUSTOM_EVENT;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 一定要先初始化
        BroadEventBus.install(this, true);

        BroadEventBus.register(this);

        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SendEventActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadEventBus.unregister(this);
    }

    @EventSubscriber(tag = CUSTOM_EVENT)
    private void function(int i, String s, float f, double d, boolean b) {
        Log.d(TAG, "function() called with: i = [" + i + "], s = [" + s + "], f = [" + f + "], d = [" + d + "], b = [" + b + "]");
        Toast.makeText(MainActivity.this, "ddd", Toast.LENGTH_SHORT).show();
    }

    @EventSubscriber(tag = CUSTOM_EVENT)
    private void function() {
        showToast("No param");
    }

    @EventSubscriber(tag = CUSTOM_EVENT)
    private void function(CharSequence str) {
        Log.d(TAG, "function: " + Thread.currentThread().getName());
        showToast("CharSequence: " + str);
    }

    @EventSubscriber(tag = CUSTOM_EVENT)
    private void function(String str, float f) {
        showToast("String: " + str + " float: " + f);
    }

    @EventSubscriber(tag = CUSTOM_EVENT)
    private void function(List<String> strings) {
        showToast("List.get(0): " + strings.get(0));
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
