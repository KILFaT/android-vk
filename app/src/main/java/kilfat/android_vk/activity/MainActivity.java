package kilfat.android_vk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TimeUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

import kilfat.android_vk.R;
import kilfat.android_vk.adapter.RecyclerViewAdapter;
import kilfat.android_vk.model.Person;

public class MainActivity extends AppCompatActivity {

    private VKApiUser user;
    private Person person;
    private Intent intentDialog;
    private RecyclerView recyclerView;
    private Context context;
    private List<Person> persons;
    private String[] scope=new String[]{VKScope.FRIENDS,VKScope.MESSAGES};
    private RecyclerTouchListener recyclerTouchListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        persons = new ArrayList<Person>();

        VKSdk.login(this, scope);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerTouchListener = new RecyclerTouchListener(context, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showMessages(position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                VKRequest request = VKApi.friends().get(
                        VKParameters.from(VKApiConst.FIELDS, "first_name,last_name,photo_50,online,id"));

                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKList usersList = (VKList) response.parsedModel;
                        for (Object obj : usersList) {
                            user = (VKApiUser) obj;
                            persons.add(new Person(user.first_name, user.last_name,
                                    user.photo_50, user.online, user.id));
                        }
                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(persons, context);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        recyclerView.addOnItemTouchListener(recyclerTouchListener);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setHasFixedSize(true);
                    }
                });
            }

            @Override
            public void onError(VKError error) {

            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private ClickListener clickListener;
        public RecyclerTouchListener(Context context,RecyclerView recyclerView,
                                     ClickListener clickListener){
            gestureDetector=new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
            this.clickListener=clickListener;
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View view = rv.findChildViewUnder(e.getX(),e.getY());
            if(view!=null && clickListener!=null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(view,rv.getChildLayoutPosition(view));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener{
        public void onClick(View view,int position);
    }

    public void showMessages(int index) {
        boolean flagReady=false;
        intentDialog = new Intent(MainActivity.this, DialogActivity.class);
        person = persons.get(index);

        intentDialog.putExtra(getString(R.string.user_id), (person.getID()));
        intentDialog.putExtra(getString(R.string.first_last_name), (person.getFirstName() + person.getLastName()));
        if(person.isOnline()){
            intentDialog.putExtra(getString(R.string.last_seen) ,0);
            flagReady=true;
        }
        else {
            VKRequest request = VKApi.users().get(
                    VKParameters.from(VKApiConst.USER_ID,person.getID(),VKApiConst.FIELDS, "last_seen"));

            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);

                    VKList usersList = (VKList)response.parsedModel;
                    VKApiUserFull userOne= (VKApiUserFull) usersList.get(0);
                    intentDialog.putExtra(getString(R.string.last_seen), userOne.last_seen);
                    startActivity(intentDialog);
                }
            });
        }

       if(flagReady) startActivity(intentDialog);
    }
}