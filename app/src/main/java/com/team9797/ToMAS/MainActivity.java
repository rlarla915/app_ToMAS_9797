package com.team9797.ToMAS;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team9797.ToMAS.navigation.first_level_adapter;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    private AppBarConfiguration mAppBarConfiguration;
    ExpandableListView expandableListView;
    List<String> headerList = new ArrayList<>();
    String title;

    //firebase
    public FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //firebase
        db = FirebaseFirestore.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // 맨위 tool bar 세팅
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_calendar, R.id.navigation_social, R.id.navigation_mypage)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        // 오른쪽 drawable total_navi 세팅
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // 임시 firebase에서 받아와서 넣어줘야함.
        /*
        String[] ItemHeaders = new String[]{"자기개발", "소통게시판", "플리마켓", "인원모집"};
        Collections.addAll(headerList, ItemHeaders);
         */

        expandableListView = findViewById(R.id.total_right_menu);
        if (expandableListView != null)
        {
            first_level_adapter first_adapter = new first_level_adapter(this, headerList);
            expandableListView.setAdapter(first_adapter);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //NEED TO FIX
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                return false;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.total_navi_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    public void set_title(){
        toolbar.setTitle(title);
    }

    public void push_title(String title_fragment)
    {
        title = title_fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }



}