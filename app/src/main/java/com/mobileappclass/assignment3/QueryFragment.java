package com.mobileappclass.assignment3;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueryFragment extends Fragment {


    public QueryFragment() {
        // Required empty public constructor
    }

    FirebaseDatabase db;

    private ArrayList<String> sortOptions = new ArrayList<>();
    public ArrayList<String> queryListItems = new ArrayList<>();

    public ArrayAdapter<String> adapter;

    Spinner dropdownList;
    EditText netid;
    Button enterButton;
    ListView queryList;

    private boolean isAscending, isDescending;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_query, container, false);


        sortOptions.add("Ascending");
        sortOptions.add("Descending");

        dropdownList = (Spinner)view.findViewById(R.id.spinner);
        netid = (EditText)view.findViewById(R.id.netID);
        enterButton = (Button)view.findViewById(R.id.enterButton);
        queryList = (ListView)view.findViewById(R.id.queryList);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownList.setAdapter(adapter);

        dropdownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        isAscending = true;
                        isDescending = false;
                        break;

                    case 1:
                        isAscending = false;
                        isDescending = true;
                        break;

                    default:
                        isAscending = isDescending = false;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                isAscending = isDescending = false;
            }
        });


        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterButtonClick();
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateList(queryListItems);

    }

    public void enterButtonClick(){
        if((isAscending || isDescending) && !netid.getText().toString().equals("")){

            readFirebaseDB(netid.getText().toString(), isAscending);
        }
        else{
            Toast.makeText(getActivity(), "Please Enter in both fields", Toast.LENGTH_SHORT).show();
        }
    }


    public void readFirebaseDB(String netid, final boolean isAsc){

        queryListItems.clear();
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Students").child(netid);

        final StringBuffer resultString = new StringBuffer();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot netId) {

                for(DataSnapshot dateTime : netId.getChildren()){   // multiple children

                    for(DataSnapshot mainData : dateTime.getChildren()){    // 4 children
                        //System.out.println(mainData.getValue());

                        resultString.append(mainData.getValue() + " ");

                    }
                    if(isAsc){
                        queryListItems.add(resultString.toString());
                    }
                    else {
                        queryListItems.add(0, resultString.toString());
                    }
                    resultString.delete(0, resultString.length());
                }

                adapter.notifyDataSetChanged();
                System.out.println(queryListItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void populateList(ArrayList<String> list){
        queryListItems = list;
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, queryListItems);
        queryList.setAdapter(adapter);
    }
}
