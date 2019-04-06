package com.kominfo.praunper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView messageTExt;
    TextView messageTime;
    TextView messageUser;

    public MessageViewHolder(View v) {
        super(v);
        messageTExt = (TextView) itemView.findViewById(R.id.message_text);
        messageTime = (TextView) itemView.findViewById(R.id.message_time);
        messageUser = (TextView) itemView.findViewById(R.id.message_user);
    }
}
