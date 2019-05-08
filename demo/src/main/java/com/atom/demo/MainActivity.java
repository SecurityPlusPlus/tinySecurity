package com.atom.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.atom.lib.tinysecurity.TinySecurityClient;

/**
 * Test
 *
 * @author iwall
 * @data on 2019/5/8 13:44
 * @describe TODO
 * @email 1299854942@qq.com
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * test 测试
     */
    public void test(View view) {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    System.out.println(TinySecurityClient.dbKey());
                    System.out.println(TinySecurityClient.mmkey());
                }
                System.out.println("run over");
            }
        }.start();

    }
}
