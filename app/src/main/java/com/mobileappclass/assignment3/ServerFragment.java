package com.mobileappclass.assignment3;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ServerFragment extends Fragment {


    public ServerFragment() {
        // Required empty public constructor
    }

    ListView serverList;
    TextView serverStatus, networkType;
    Button syncButton;

    String networkDetails = "";

    ArrayList<String> serverDBList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    FirebaseDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_server, container, false);

        serverList = (ListView) view.findViewById(R.id.serverList);
        serverStatus = (TextView)view.findViewById(R.id.serverStatus);
        networkType = (TextView)view.findViewById(R.id.networkType);
        syncButton = (Button)view.findViewById(R.id.syncButton);

        Bundle args = getArguments();

        if(!networkDetails.equals("")){
            networkType.setText(networkDetails);
        }

        if(args != null){
            networkType.setText(args.getString("networkInfo"));
            serverStatus.setText(args.getString("serverStatus"));
        }


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateList(serverDBList);
        readFirebaseDB();

    }

    ////////////////////////////// To Read Firebase Database ///////////////////////////////////////

    public void readFirebaseDB(){

        serverDBList.clear();
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Students");
        Query q = ref.limitToFirst(2);

        final StringBuffer resultString = new StringBuffer();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot students) {

                for(DataSnapshot netid : students.getChildren()){   // one child

                    for(DataSnapshot dateTime : netid.getChildren()){   // multiple children

                        for(DataSnapshot mainData : dateTime.getChildren()){    // 4 children
                            //System.out.println(mainData.getValue());
                            resultString.append(mainData.getValue() + " ");


                        }
                        serverDBList.add(resultString.toString());
                        resultString.delete(0, resultString.length());
                    }
                }
                adapter.notifyDataSetChanged();
                System.out.println(serverDBList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void populateList(ArrayList<String> list){

        serverDBList = list;
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        serverList.setAdapter(adapter);
    }

    public void setNetworkStatus(String networkInfo){
        networkType.setText(networkInfo);
        networkDetails = networkInfo;
    }

    public void setConnectionStatus(String conn){
        serverStatus.setText(conn);
    }

}

