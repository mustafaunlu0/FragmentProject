package com.mustafaunlu.artbookfragment.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafaunlu.artbookfragment.R;
import com.mustafaunlu.artbookfragment.databinding.RecyclerRowBinding;
import com.mustafaunlu.artbookfragment.model.Art;
import com.mustafaunlu.artbookfragment.view.FirstFragmentDirections;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtViewHolder> {

    ArrayList<Art> artArrayList;

    public ArtAdapter(ArrayList<Art> artArrayList) {
        this.artArrayList = artArrayList;
    }

    @NonNull
    @Override
    public ArtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ArtViewHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recyclerRowBinding.recyclerViewTextView.setText(artArrayList.get(position).comment);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("anahtarId",artArrayList.get(position).id);
                bundle.putString("info","old");
                //NavDirections action= FirstFragmentDirections.actionFirstFragmentToSecondFragment();
                Navigation.findNavController(v).navigate(R.id.secondFragment,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    public class ArtViewHolder extends RecyclerView.ViewHolder{
        RecyclerRowBinding recyclerRowBinding;
        public ArtViewHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding=recyclerRowBinding;
        }
    }
}
