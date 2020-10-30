package com.team9797.ToMAS.ui.calendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.team9797.ToMAS.MainActivity;
import com.team9797.ToMAS.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment implements OnRangeSelectedListener{


    MaterialCalendarView calendarView;
    MainActivity mainActivity;
    FloatingActionButton btn_calendar;
    TextView type_textView;
    TextView title_textView;
    TextView content_textView;
    RangeDayDecorator decorator;
    Button event_update;
    LinearLayout calendar_container;
    Button event_delete;
    List<CalendarDay> selected_day_list = new ArrayList<>();
    ArrayList<List<CalendarDay>> events = new ArrayList<>();
    HashMap<CalendarDay, event> day_to_event_map = new HashMap<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        mainActivity = (MainActivity)getActivity();
        calendarView = root.findViewById(R.id.calendarView);
        btn_calendar = root.findViewById(R.id.btn_calendar);
        event_update = root.findViewById(R.id.event_update);
        event_delete = root.findViewById(R.id.event_delete);
        title_textView = root.findViewById(R.id.event_title);
        type_textView = root.findViewById(R.id.event_type);
        content_textView = root.findViewById(R.id.event_content);
        calendar_container = root.findViewById(R.id.calendar_container);
        decorator = new RangeDayDecorator(mainActivity);
        calendarView.setOnRangeSelectedListener(this);
        calendarView.addDecorator(decorator);
        //calendarview 크기 줄이기
        calendarView.setTileHeightDp(40);

        mainActivity.db.collection("user").document(mainActivity.getUid()).collection("events")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // calendarDay를 firebase내에 저장 할 수 있는지 확인 후 적용
                        event tmp_event = new event(document.get("type").toString(), document.get("title").toString(), document.get("content").toString(), document.get("color", Integer.class), document.getId());
                        ArrayList<String> tmp_string_list= (ArrayList<String>)document.get("days");
                        ArrayList<CalendarDay> tmp_list = new ArrayList<>();
                        for (int i = 0; i< tmp_string_list.size(); i++)
                        {
                            String [] split_date = tmp_string_list.get(i).split("-");
                            CalendarDay tmp_calendarDay = CalendarDay.from(Integer.parseInt(split_date[0]), Integer.parseInt(split_date[1]), Integer.parseInt(split_date[2]));
                            tmp_list.add(tmp_calendarDay);
                            day_to_event_map.put(tmp_calendarDay, tmp_event);
                        }
                        events.add(tmp_list);
                        show_events();
                    }
                } else {
                    //Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                event selected_event = day_to_event_map.get(date);
                if (selected_event == null) {
                    calendar_container.setVisibility(View.GONE);
                } else {
                    calendar_container.setVisibility(View.VISIBLE);
                    type_textView.setText("종류 : " + selected_event.type);
                    title_textView.setText("제목 : " + selected_event.title);
                    content_textView.setText("내용 : " + selected_event.content);

                    // 수정 기능 수정해야 함.
                    event_update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            add_event_dialog dialog = new add_event_dialog(mainActivity, selected_event.type, selected_event.title, selected_event.content);
                            dialog.show(getFragmentManager(), "일정등록");
                            dialog.setDialogResult(new add_event_dialog.add_event_dialog_result() {
                                @Override
                                public void get_result(String type, String title, String content, int color) {
                                    // 여기서 서버로 보내기
                                    Map<String, Object> post = new HashMap<>();
                                    post.put("type", type);
                                    post.put("title", title);
                                    post.put("content", content);
                                    post.put("color", color);
                                    ArrayList<String> string_selected_days = new ArrayList<>();
                                    for (int i = 0; i< selected_day_list.size(); i++)
                                    {
                                        String tmp = Integer.toString(selected_day_list.get(i).getYear()) + "-" + Integer.toString(selected_day_list.get(i).getMonth()) + "-"+ Integer.toString(selected_day_list.get(i).getDay());
                                        string_selected_days.add(tmp);
                                    }
                                    post.put("days", string_selected_days);

                                    mainActivity.db.collection("user").document(mainActivity.getUid()).collection("events").document()
                                            .set(post)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("AAA", "DocumentSnapshot successfully written!");
                                                    refresh();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("AAA", "Error writing document", e);
                                                }
                                            });
                                }
                            });
                        }
                    });
                    event_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("QQQ", selected_event.id);
                            mainActivity.db.collection("user").document(mainActivity.getUid()).collection("events").document(selected_event.id).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("AA", "success");
                                            refresh();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.w("AAA", "Error writing document", e);
                                        }
                                    });
                        }
                    });
                }
            }
        });

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_event_dialog dialog = new add_event_dialog(mainActivity);
                dialog.show(mainActivity.getSupportFragmentManager(), "일정등록");
                dialog.setDialogResult(new add_event_dialog.add_event_dialog_result() {
                    @Override
                    public void get_result(String type, String title, String content, int color) {
                        // 여기서 서버로 보내기
                        Map<String, Object> post = new HashMap<>();
                        post.put("type", type);
                        post.put("title", title);
                        post.put("content", content);
                        post.put("color", color);
                        ArrayList<String> string_selected_days = new ArrayList<>();
                        for (int i = 0; i< selected_day_list.size(); i++)
                        {
                            String tmp = Integer.toString(selected_day_list.get(i).getYear()) + "-" + Integer.toString(selected_day_list.get(i).getMonth()) + "-"+ Integer.toString(selected_day_list.get(i).getDay());
                            string_selected_days.add(tmp);
                        }
                        post.put("days", string_selected_days);

                        mainActivity.db.collection("user").document(mainActivity.getUid()).collection("events").document()
                                .set(post)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("AAA", "DocumentSnapshot successfully written!");
                                        refresh();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("AAA", "Error writing document", e);
                                    }
                                });
                    }
                });
            }
        });

        return root;
    }

    public void show_events()
    {
        for (int i = 0; i < events.size(); i++)
        {
            // 여기서 글자 추가하면 될 듯
            calendarView.addDecorator(new EventDecorator(day_to_event_map.get(events.get(i).get(0)).color, events.get(i)));
        }
        calendarView.invalidateDecorators();
    }

    @Override
    public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
        selected_day_list.clear();
        for (int i = 0; i < dates.size(); i++)
            selected_day_list.add(dates.get(i));
    }


    public void refresh()
    {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}

class event{
    String type;
    String title;
    String content;
    int color;
    String id;

    event()
    {

    }
    event(String tmp_type, String tmp_title, String tmp_content, int tmp_color, String tmp_id)
    {
        type = tmp_type;
        title = tmp_title;
        content = tmp_content;
        color = tmp_color;
        id = tmp_id;
    }
}