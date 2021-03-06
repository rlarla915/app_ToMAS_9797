package com.team9797.ToMAS.navigation;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team9797.ToMAS.MainActivity;
import com.team9797.ToMAS.R;
import com.team9797.ToMAS.ui.home.group.groupFragment;
import com.team9797.ToMAS.ui.home.market.MarketFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirstLevelAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> list_second_level_map;
    private HashMap<String, String> list_second_level_path;
    private HashMap<String, List<String>> list_third_level_map;
    private HashMap<String, String> list_third_level_path;
    private List<String> list_first_child;
    private List<String> list_second_child;
    MainActivity mainActivity;

    public FirstLevelAdapter(Context context, List<String> listDataHeader, MainActivity tmp_mainActivity) {
        this.context = context;
        this.listDataHeader = new ArrayList<>();
        this.listDataHeader.addAll(listDataHeader);
        mainActivity = tmp_mainActivity;
        //init second level
        list_first_child = new ArrayList<>();
        list_second_level_map = new HashMap<>();
        list_second_level_path = new HashMap<>();
        list_third_level_path = new HashMap<>();
        list_second_child = new ArrayList<>();
        list_third_level_map = new HashMap<>();
        for (int i = 0; i < 2; i++)
        {
            String second_content = listDataHeader.get(i);
            switch (second_content)
            {
                case "자기개발":
                    FirebaseFirestore.getInstance().collection("mainpage/자기개발/자기개발").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                list_first_child = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    list_first_child.add(document.getId());
                                    String tmp_path = "mainpage/자기개발/자기개발/" + document.getId() + "/" + document.getId();
                                    list_second_level_path.put(document.getId(), tmp_path);
                                }
                                list_second_level_map.put(second_content, list_first_child);
                                for (int i = 0; i < list_first_child.size(); i++)
                                {
                                    String third_content = list_first_child.get(i);
                                    FirebaseFirestore.getInstance().collection(list_second_level_path.get(third_content)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                list_second_child = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    list_second_child.add(document.getId());
                                                    list_third_level_path.put(document.getId(), list_second_level_path.get(third_content));
                                                }
                                                list_third_level_map.put(third_content, list_second_child);
                                            } else {
                                            }
                                        }
                                    });
                                }
                            } else {

                            }
                        }
                    });
                    break;
                case "소통게시판":
                    FirebaseFirestore.getInstance().collection("mainpage/소통게시판/소통게시판").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                list_first_child = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    list_first_child.add(document.getId());
                                    String tmp_path = "mainpage/소통게시판/소통게시판/" + document.getId() + "/" + document.getId();
                                    list_second_level_path.put(document.getId(), tmp_path);
                                }

                                list_second_level_map.put(second_content, list_first_child);
                                for (int i = 0; i < list_first_child.size(); i++)
                                {
                                    String third_content = list_first_child.get(i); // need to fix
                                    FirebaseFirestore.getInstance().collection(list_second_level_path.get(third_content)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                list_second_child = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    list_second_child.add(document.getId());
                                                    list_third_level_path.put(document.getId(), list_second_level_path.get(third_content));
                                                }
                                                list_third_level_map.put(third_content, list_second_child);
                                            } else {
                                                //
                                            }
                                        }
                                    });
                                }
                            } else {
                                //
                            }
                        }
                    });
                    break;
            }
        }

    }

    @Override
    public int getGroupCount()
    {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i)
    {
        /*
        if (this.listDataChild.get(this.listDataHeader.get(i)) == null) {
            return 0;
        }
        else
        {
            return this.listDataChild.get(this.listDataHeader.get(i)).size();
        }
         */
        return 1;
    }

    @Override
    public String getGroup(int i) {
        return this.listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1)
    {
        return i1; //need to fix
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1)
    {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup parent) {
        /*
        final CustomExpListView secondLevelExpListView = new CustomExpListView(this.context);
        String parentNode = (String)getGroup(i);
        secondLevelExpListView.setAdapter(new SecondLevelAdapter(this.context, list_second_level_map.get(parentNode), list_third_level_map));
        secondLevelExpListView.setGroupIndicator(null);
        return secondLevelExpListView;
        */

        String headerTitle = (String)getGroup(i);
        if (convertView == null)
        {
            LayoutInflater expand_Inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = expand_Inflater.inflate(R.layout.drawer_list_group, parent, false);
        }
        TextView txtListHeader = (TextView)convertView.findViewById(R.id.lblListHeader);
        txtListHeader.setTypeface(null, Typeface.BOLD);
        txtListHeader.setText(headerTitle);
        if (i == 2)
        {
            txtListHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.closeDrawer();
                    mainActivity.push_title(headerTitle);
                    mainActivity.set_title();
                    Fragment change_fragment = new MarketFragment();
                    FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, change_fragment).commit();
                }
            });
        }
        else if (i == 3)
        {
            txtListHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.closeDrawer();
                    mainActivity.push_title(headerTitle);
                    mainActivity.set_title();
                    Fragment change_fragment = new groupFragment();
                    FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, change_fragment).commit();
                }
            });
        }
        return convertView;

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup parent) {
        if (i < 2) {
            final CustomExpListView secondLevelExpListView = new CustomExpListView(this.context);
            String parentNode = (String) getGroup(i);
            secondLevelExpListView.setAdapter(new SecondLevelAdapter(this.context, list_second_level_map.get(parentNode), list_third_level_map, list_third_level_path, mainActivity));
            secondLevelExpListView.setGroupIndicator(null);
            return secondLevelExpListView;
        }
        else
        {
            return convertView;
        }
        /*
        final String childText = getChild(i, i1);
        if (view == null){
            LayoutInflater expand_Inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = expand_Inflater.inflate(R.layout.fragment_child, null);
        }
        TextView txtListChild = (TextView)view.findViewById(R.id.expand_list_item);
        txtListChild.setText(childText);
        return view;
        */

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
