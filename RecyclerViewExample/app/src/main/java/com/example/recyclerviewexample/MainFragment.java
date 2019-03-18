package com.example.recyclerviewexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class MainFragment extends android.support.v4.app.Fragment {

    RecyclerAdapter recyclerAdapter;
    RecyclerView rvRecycler;
    ArrayList<RecyclerItem> list;
    Button button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        rvRecycler = view.findViewById(R.id.rvRecycler);
        button = view.findViewById(R.id.button);
        rvRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)); // 수평
        //rvRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),false)); //수직
        recyclerAdapter = new RecyclerAdapter(list);
        list = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(new RecyclerItem("경문이는 여덟글자"));
                list.add(new RecyclerItem("성현인 일곱글자"));
                list.add(new RecyclerItem("령국 여섯글자"));
                list.add(new RecyclerItem("지혜다섯개"));
                recyclerAdapter.setItems(list);
                rvRecycler.setHasFixedSize(true);

                rvRecycler.setAdapter(recyclerAdapter);
            }
        });

        return view;
    }
}
