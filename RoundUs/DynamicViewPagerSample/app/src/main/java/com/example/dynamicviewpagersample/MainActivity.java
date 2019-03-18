package com.example.dynamicviewpagersample;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnAddPage, btnRemovePage;
    private ViewPager vpListPager;
    private ViewPagerManager viewPagerManager;
    private ArrayList<MyView> myViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initClass();
        initEvent();
    }


    private void initView() {
        vpListPager = findViewById(R.id.vpListPager);
        btnAddPage = findViewById(R.id.btnAddPage);
        btnRemovePage = findViewById(R.id.btnRemovePage);
    }

    private void initEvent() {
        btnAddPage.setOnClickListener(btnAddPageClickEvent);
        btnRemovePage.setOnClickListener(btnRemovePageClickEvent);
    }

    private void initClass() {
        viewPagerManager = new ViewPagerManager();
        myViewList = new ArrayList<>();
    }

    private View.OnClickListener btnAddPageClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = (int)(Math.random() * 3);
            if (i == 1)
                myViewList.add(new MyView(getApplicationContext(),"추가했다111"));
            else if (i == 2)
                myViewList.add(new MyView(getApplicationContext(),"추가했다222"));
            else
                myViewList.add(new MyView(getApplicationContext(),"추가했다333"));
            viewPagerManager.addView(vpListPager, myViewList.get(myViewList.size()-1).getView());
        }
    };
    private View.OnClickListener btnRemovePageClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View currentView = viewPagerManager.getCurrentPage(vpListPager);

            if(currentView != null)
                viewPagerManager.removeView(vpListPager, currentView);
            else
                Toast.makeText(getApplicationContext(), "page does not exist!!", Toast.LENGTH_SHORT).show();
        }
    };
}
