package com.simon.demo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.simon.likeview.view.ShineView;


public class MainActivity extends AppCompatActivity {
    private Button testBtn;
    private Button test2Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testBtn = (Button) findViewById(R.id.test_btn);
        test2Btn = (Button) findViewById(R.id.test2_btn);
        initTest1();
        initTest2();
    }

    private void initTest2() {
        //--初始化view--
        final ShineView shineView = new ShineView(this);
        //--配置--
        shineView.setAutoAnim(true);
        shineView.setBigShineColor(Color.GREEN);
        shineView.setShineCount(8);
        shineView.attachView(test2Btn); //**此方法必须执行，且最后执行，仅仅需要执行一次，ShineView可以重用，其他的需要此效果的重新执行此方法即可**
        test2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shineView.startAnim();
            }
        });
    }

    private void initTest1() {
        final ShineView shineView = (ShineView) LayoutInflater.from(this).inflate(R.layout.shine_view, null).findViewById(R.id.shineView);
        shineView.attachView(testBtn);//**此方法必须执行，且最后执行，仅仅需要执行一次，ShineView可以重用，其他的需要此效果的重新执行此方法即可**
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shineView.startAnim();
            }
        });
    }
}
