package kilfat.android_vk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiMessages;
import com.vk.sdk.api.model.VKApiGetMessagesResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kilfat.android_vk.R;
import kilfat.android_vk.adapter.RecyclerViewAdapter;
import kilfat.android_vk.adapter.RecyclerViewAdapterMessage;
import kilfat.android_vk.model.Message;
import kilfat.android_vk.model.Person;

public class DialogActivity  extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Context context;
    private Toolbar toolbar;
    private Intent intent;
    private int userID;
    private int pastVisiblesItems=0, visibleItemCount=0, totalItemCount=0;
    private LinearLayoutManager layoutManager;
    private List<Message> messagesHistory = new ArrayList<Message>();
    private int count = 20;
    private int offset=0;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        intent = getIntent();
        long time = intent.getLongExtra(getString(R.string.last_seen), 0);
        toolbar.setTitle(intent.getCharSequenceExtra(getString(R.string.first_last_name)));
        if (time == 0) {
            toolbar.setSubtitle("online");
        } else {
            String dateTime = new java.text.SimpleDateFormat("DD.MM HH:mm").format(
                    new java.util.Date(intent.getLongExtra(getString(R.string.last_seen), 0) * 1000));
            toolbar.setSubtitle("был(а) в " + dateTime);
        }
        userID = intent.getIntExtra(getString(R.string.user_id), 0);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        showMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showMessages() {
        VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(
                "user_id", userID, "offset", offset, "count", count), VKApiGetMessagesResponse.class);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                messagesHistory = getMessagesHistory((VKApiGetMessagesResponse) response.parsedModel, null);
                //totalItemCount += visibleItemCount = messagesHistory.size();
                RecyclerViewAdapterMessage adapter = new RecyclerViewAdapterMessage(messagesHistory, context);
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
                loading = false;
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                        if (!loading) {
                            if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                loading = true;
                                offset+=count;
                                showMessages();
                            }
                        }
                    }
                });
            }
        });
    }

    public List<Message> getMessagesHistory(VKApiGetMessagesResponse response, VKList<VKApiMessage> fwd){
        VKList<VKApiMessage> messageVKList= (fwd==null)? response.items: fwd;
        List<Message> temp=new ArrayList<Message>();
        Message tempMessage;

        String photoID=null;
        for (VKApiMessage message : messageVKList) {
            if(message.attachments!=null && message.attachments.size()!=0) {
                if (message.attachments.get(0).getType().equals("photo")) {
                    photoID = ((VKApiPhoto)message.attachments.get(0)).photo_130;
                }
            }
            tempMessage=new Message(message.body, message.date, photoID, message.out);
            if(!message.fwd_messages.isEmpty()){
                tempMessage.setFwdMessages(getMessagesHistory(response, message.fwd_messages));
            }
            temp.add(tempMessage);
        }
        return temp;
    }
}