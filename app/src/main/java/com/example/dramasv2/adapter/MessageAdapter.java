package com.example.dramasv2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dramasv2.R;
import com.example.dramasv2.modelo.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.senderId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Two types: sent and received messages
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (getItemViewType(position) == 0) { // Sent message
                convertView = inflater.inflate(R.layout.vista_mensaje_enviado, parent, false);
            } else { // Received message
                convertView = inflater.inflate(R.layout.vista_mensaje, parent, false);
                holder.senderNameTextView = convertView.findViewById(R.id.sender_name);
            }

            holder.messageTextView = convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = messages.get(position);
        holder.messageTextView.setText(message.text);

        if (getItemViewType(position) == 1) { // Received message
            holder.senderNameTextView.setText(message.senderName);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView senderNameTextView;
        TextView messageTextView;
    }
}
