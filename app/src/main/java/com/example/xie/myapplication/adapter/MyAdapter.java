package com.example.xie.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.xie.myapplication.R;
import com.example.xie.myapplication.tool.Msg;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int TYPE_RECEIVED = 0;//表示这是一条收到的消息
    public static final int TYPE_SENT = 1;//表示这是一条发出的消息
    private List<EMMessage> dataLists = new ArrayList<>();

    public MyAdapter(List<EMMessage> data){
        dataLists =data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0) {
            view = View.inflate(parent.getContext(), R.layout.text_recv, null);
            return new RecvViewHolder(view);
        }else{
            view = View.inflate(parent.getContext(), R.layout.text_send, null);
            return new SendViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecvViewHolder) {
            ((RecvViewHolder) holder).mTextViewRecv.setText(getMessage(dataLists.get(position)));
        } else if (holder instanceof SendViewHolder) {
            ((SendViewHolder) holder).mTextViewSend.setText(getMessage(dataLists.get(position)));
            holder.setIsRecyclable(false);
        }
    }
    public void addItem(int position,EMMessage msg){
        if(msg.getBody().equals("")) {
            dataLists.add(position,msg);
            notifyItemInserted(position);
        }
    }

    private String getMessage(EMMessage message) {
        return message.getBody().toString().split("\"")[1];
    }

    public int getItemViewType(int position) {
        if(dataLists.get(position).getFrom().equalsIgnoreCase(EMClient.getInstance().getCurrentUser())){
            return  TYPE_SENT;
        }
        else {
            return TYPE_RECEIVED;
        }
    }

    public void refreshDatas(List mDataGoods) {
        dataLists.clear();
        dataLists.addAll(mDataGoods);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return  dataLists == null ? 0 : dataLists.size();
    }

    class SendViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewSend;
        public SendViewHolder(View itemView) {
            super(itemView);
            mTextViewSend = (TextView)
                    itemView.findViewById(R.id.textView_Send);
        }
    }
    class RecvViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewRecv;
        public RecvViewHolder(View itemView) {
            super(itemView);
            mTextViewRecv = (TextView)
                    itemView.findViewById(R.id.textView_Recv);
        }

    }


}
