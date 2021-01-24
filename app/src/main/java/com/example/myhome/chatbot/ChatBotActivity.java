package com.example.myhome.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.myhome.R;
import com.example.myhome.authentication.RegistrationActivity;
import com.example.myhome.chatbot.adapters.ChatAdapter;
import com.example.myhome.chatbot.helpers.SendMessageInBg;
import com.example.myhome.chatbot.interfaces.BotReply;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatBotActivity extends AppCompatActivity implements BotReply {

        private RecyclerView chatView;
        private ChatAdapter chatAdapter;
        private List<Message> messageList = new ArrayList<>();
        private EditText editMessage;
        private ImageButton btnSend;
        private SessionsClient sessionsClient;
        private SessionName sessionName;
        private String uuid = UUID.randomUUID().toString();
        private String TAG = "chatbotactivity";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_bot);
            chatView = findViewById(R.id.chatView);
            editMessage = findViewById(R.id.editMessage);
            btnSend = findViewById(R.id.btnSend);

            chatAdapter = new ChatAdapter(messageList, this);
            chatView.setAdapter(chatAdapter);

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    String message = editMessage.getText().toString().trim();
                    if (!message.isEmpty() && message!=null) {
                        messageList.add(new Message(message, false));
                        editMessage.setText("");
                        sendMessageToBot(message);
                        Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                        Objects.requireNonNull(chatView.getLayoutManager())
                                .scrollToPosition(messageList.size() - 1);
                    } else {
                        DynamicToast.makeWarning(ChatBotActivity.this, "Enter the Query!", 10).show();
                    }
                }
            });

            setUpBot();
        }

        private void setUpBot() {
            try {
                InputStream stream = this.getResources().openRawResource(R.raw.credential);
                GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
                String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

                SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
                SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                        FixedCredentialsProvider.create(credentials)).build();
                sessionsClient = SessionsClient.create(sessionsSettings);
                sessionName = SessionName.of(projectId, uuid);

                Log.d(TAG, "projectId : " + projectId);
            } catch (Exception e) {
                Log.d(TAG, "setUpBot: " + e.getMessage());
            }
        }

        private void sendMessageToBot(String message) {
            QueryInput input = QueryInput.newBuilder()
                    .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
            new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
        }

        @Override
        public void callback(DetectIntentResponse returnResponse) {
            if(returnResponse!=null) {
                String botReply = returnResponse.getQueryResult().getFulfillmentText();
                if(!botReply.isEmpty()){
                    messageList.add(new Message(botReply, true));
                    chatAdapter.notifyDataSetChanged();
                    Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
                }else {
                    DynamicToast.makeError(ChatBotActivity.this, "Something Went Wrong!", 10).show();
                }
            } else {
                DynamicToast.makeError(ChatBotActivity.this, "Failed to Connect!", 10).show();
            }
        }
    }