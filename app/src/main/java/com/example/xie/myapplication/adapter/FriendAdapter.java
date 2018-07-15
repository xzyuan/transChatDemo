package com.example.xie.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.xie.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> friendsList = new ArrayList<>();

    public FriendAdapter(List<String> data){
        friendsList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = View.inflate(parent.getContext(), R.layout.friend, null);
        return new FriendViewHolder(view);
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((FriendViewHolder) holder).friendBtn.setText(friendsList.get(position));
    }

    public interface  onItemClickListener{
        //        void onItemClick(View view ,int position);
        void  onItemLongClick(View view, int position);
    }

    public void addItem(int position,String friend){
        if(!"".equals(friend)) {
            friendsList.add(position,friend);
            notifyItemInserted(position);
        }
    }

    public void refresh(List<String> list){
        friendsList.clear();
        friendsList.addAll(list);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return  friendsList == null ? 0 : friendsList.size();
    }


    class FriendViewHolder extends RecyclerView.ViewHolder{
        public Button friendBtn;
        public FriendViewHolder(View itemView){
            super(itemView);
            friendBtn = (Button) itemView.findViewById(R.id.friendBtn);
        }
    }
}
