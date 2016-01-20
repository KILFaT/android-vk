package kilfat.android_vk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;

import kilfat.android_vk.R;
import kilfat.android_vk.model.Message;

public class RecyclerViewAdapterMessage extends RecyclerView.Adapter<RecyclerViewAdapterMessage.UserViewHolder> {
    RecyclerViewAdapterMessage fwdMessages;
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView message;
        private TextView date;
        private ImageView image;
        private RecyclerView recyclerView; // Recursion
        private

        UserViewHolder(View itemView) {
            super(itemView);
            message = (TextView)itemView.findViewById(R.id.message);
            date = (TextView)itemView.findViewById(R.id.date);
            recyclerView=(RecyclerView) itemView.findViewById(R.id.recyclerView);
            image=(ImageView) itemView.findViewById(R.id.attachPhoto);
        }
    }

    private List<Message> messages;
    private Context context;

    public RecyclerViewAdapterMessage(List<Message> messages, Context context){
        this.messages = messages;
        this.context=context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_message, viewGroup, false);
        UserViewHolder pvh = new UserViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder UserViewHolder, int i) {
        UserViewHolder.message.setText(messages.get(i).getMessage());
        UserViewHolder.date.setText(new java.text.SimpleDateFormat("DD.MM HH:mm").format(
                new java.util.Date(messages.get(i).getDate() * 1000)));
        if(messages.get(i).getPhotoIDs()!=null && messages.get(i).getPhotoIDs().length()!=0)
            Picasso.with(context).load(messages.get(i).getPhotoIDs()).into(UserViewHolder.image);
        if(messages.get(i).isFwd()){
            fwdMessages=new RecyclerViewAdapterMessage(messages.get(i).getFwdMessages(),context);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
