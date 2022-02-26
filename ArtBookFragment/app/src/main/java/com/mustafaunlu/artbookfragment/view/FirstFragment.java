package com.mustafaunlu.artbookfragment.view;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mustafaunlu.artbookfragment.R;
import com.mustafaunlu.artbookfragment.adapter.ArtAdapter;
import com.mustafaunlu.artbookfragment.databinding.RecyclerRowBinding;
import com.mustafaunlu.artbookfragment.model.Art;

import java.util.ArrayList;


public class FirstFragment extends Fragment {

    ArrayList<Art> artArrayList;
    ArtAdapter artAdapter;
    public FirstFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    private void getData() {

        try{
            SQLiteDatabase sqLiteDatabase=this.getActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null);

            Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM arts",null);
            int commentIx=cursor.getColumnIndex("comment");
            int idIx=cursor.getColumnIndex("id");

            while(cursor.moveToNext()){
                String comment=cursor.getString(commentIx);
                int id=cursor.getInt(idIx);
                Art art=new Art(id,comment);
                artArrayList.add(art);
            }
            artAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView=getView().findViewById(R.id.recyclerView);

        artArrayList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artAdapter=new ArtAdapter(artArrayList);
        recyclerView.setAdapter(artAdapter);

        getData();



    }
}