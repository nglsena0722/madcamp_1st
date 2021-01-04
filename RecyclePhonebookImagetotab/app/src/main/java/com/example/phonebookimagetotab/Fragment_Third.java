package com.example.phonebookimagetotab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.phonebookimagetotab.R;

public class Fragment_Third extends Fragment {
    public ViewPager viewPager;
    static final String[] LIST_MENU = {"Snake game", "Minesweeper", "Breakout", "LIST4"} ;

    public Fragment_Third(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third,container,false);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU) ;

        ListView listview = (ListView) view.findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 코드 계속 ...

            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position) ;

                // TODO : use strText
                Log.d("2","touch");
                Toast.makeText(getContext(), LIST_MENU[position] + " will start.", Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        Intent intent1 = new Intent(getActivity(), SnakeActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(getActivity(), MineActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(getActivity(), BreakoutActivity.class);
                        startActivity(intent3);
                        break;
                    case 3:
                        break;
                }
            }
        }) ;

        return view;
    }
}
