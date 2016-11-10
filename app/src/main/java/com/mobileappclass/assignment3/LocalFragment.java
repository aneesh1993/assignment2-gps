package com.mobileappclass.assignment3;


import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocalFragment extends Fragment {

    ArrayList<String> localDBList = new ArrayList<>();
    ListView listView;

    public LocalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        listView = (ListView) view.findViewById(R.id.localList);

        return view;
    }


    public void populateList(ArrayList<String> list){
        localDBList = list;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }



}
