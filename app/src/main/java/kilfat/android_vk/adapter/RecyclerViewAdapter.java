package kilfat.android_vk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import kilfat.android_vk.R;
import kilfat.android_vk.model.Person;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.UserViewHolder> {

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView personFirstName;
        private TextView personLastName;
        private TextView personStatus;
        private ImageView personPhoto;

        UserViewHolder(View itemView) {
            super(itemView);
            personFirstName = (TextView)itemView.findViewById(R.id.person_first_name);
            personLastName = (TextView)itemView.findViewById(R.id.person_last_name);
            personStatus = (TextView)itemView.findViewById(R.id.person_status);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    private List<Person> persons;
    private Context context;

    public RecyclerViewAdapter(List<Person> persons, Context context){
        this.persons = persons;
        this.context=context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        UserViewHolder pvh = new UserViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder UserViewHolder, int i) {
        UserViewHolder.personFirstName.setText(persons.get(i).getFirstName());
        UserViewHolder.personLastName.setText(persons.get(i).getLastName());
        UserViewHolder.personStatus.setText(persons.get(i).isOnline() ? "Online" : "");
        Picasso.with(context).load(persons.get(i).getPhotoID()).into(UserViewHolder.personPhoto);
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}
