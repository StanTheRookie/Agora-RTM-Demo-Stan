package com.example.rtmdemostan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.SendMessageOptions;

public class MainActivity extends AppCompatActivity {

    private EditText et_uid;
    private TextView history_message;
    private EditText et_channel_id;
    private EditText et_message;
    private EditText et_peer_id;

    private String AppID;
    private String Token;
    private String uid;
    private String channelID;
    private String messageToBeSent;
    private String peerID;

    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;
    private RtmMessage mRtmMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            AppID = getBaseContext().getString(R.string.AppID);
            writeMessage("mRtmclient Initializing...\n");
            mRtmClient = RtmClient.createInstance(getBaseContext(), AppID, new RtmClientListener() {
                @Override
                public void onConnectionStateChanged(int i, int i1) {
                    String text = "Connection state changed to " + i + " Reason: " + i1 + "\n";
                    writeMessage(text);
                }

                @Override
                public void onMessageReceived(RtmMessage rtmMessage, String s) {
                    String text = "Message received from " + s + " Message: " + rtmMessage.getText() + "\n";
                    writeMessage(text);
                }

                @Override
                public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {

                }

                @Override
                public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

                }

                @Override
                public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

                }

                @Override
                public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

                }

                @Override
                public void onTokenExpired() {

                }

                @Override
                public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

                }
            });

        }catch(Exception e){
            throw new RuntimeException("RTM Client Initialization Failed");
        }
    }



    private void writeMessage(String text) {
        history_message = findViewById(R.id.tv_history_message);
        history_message.append(text);
    }

    public void onClickLogin(View view) {
        et_uid =findViewById(R.id.EditText_UID);
        uid = et_uid.getText().toString();
        Token = getBaseContext().getString(R.string.Token);
        mRtmClient.login(Token, uid, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                writeMessage("mRtmClient " + uid + " Login Success \n");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "User: " + uid + " failed to log in to the RTM system!" + errorInfo.toString();
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                });
            }
        });

    }

    public void onClickLogout(View view) {
        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                writeMessage("mRtmClient " + uid + " log out success!\n"  );
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                CharSequence text = "User: " + uid + " failed to log out from the RTM system!" + errorInfo.toString();
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                });
            }
        });
    }

    public void onClickJoin(View view) {

        et_channel_id = findViewById(R.id.EditText_ChannelID);
        channelID = et_channel_id.getText().toString();
        writeMessage("Creating Channel...... \n");
        RtmChannelListener mRtmChannelListener = new RtmChannelListener() {
            @Override
            public void onMemberCountUpdated(int i) {

            }

            @Override
            public void onAttributesUpdated(List<RtmChannelAttribute> list) {

            }

            @Override
            public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
                String text = rtmMessage.getText();
                String fromUser = rtmChannelMember.getUserId();

                String message_text = "Message received from " + fromUser + " : " + text + "\n";
                writeMessage(message_text);

            }

            @Override
            public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

            }

            @Override
            public void onMemberJoined(RtmChannelMember rtmChannelMember) {
                writeMessage("RtmChannel Member: " + rtmChannelMember.getChannelId() + " joined!\n");
            }

            @Override
            public void onMemberLeft(RtmChannelMember rtmChannelMember) {
                writeMessage("RtmChannel Member: " + rtmChannelMember.getChannelId() + " left!\n");
            }
        };


        try {
            mRtmChannel = mRtmClient.createChannel(channelID, mRtmChannelListener);

        } catch (RuntimeException e) {
            writeMessage("Join Channel Failed :( \n");
            writeMessage(e.toString() + "\n");
        }

        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                writeMessage(uid + " joined Channel " + channelID + " success! \n");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                writeMessage("Failed to join the channel!\nReason: " + errorInfo.toString() + "\n");
                CharSequence text = "User: " + uid + " failed to join the channel!" + errorInfo.toString();
                int duration = Toast.LENGTH_SHORT;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                });
            }
        });
    }


    public void onClickLeave(View view) {
        mRtmChannel.leave(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                writeMessage(uid + " Leaving Channel......\nLeft Channel......\n");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                writeMessage("Failed to leave the channel!\nReason: "+ errorInfo.toString() +"\n");
            }
        });
    }

    public void onClickSendChannel(View view) {
        et_message = findViewById(R.id.EditText_messageToSend);
        messageToBeSent = et_message.getText().toString();
        mRtmMessage = mRtmClient.createMessage();
        mRtmMessage.setText(messageToBeSent);
        mRtmChannel.sendMessage(mRtmMessage, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                String text = "Message sent to channel " + mRtmChannel.getId() + " : " + mRtmMessage.getText() + "\n";
                writeMessage(text);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send to channel " + mRtmChannel.getId() + " Error: " + errorInfo + "\n";
                writeMessage(text);
            }
        });
    }

    public void onClickSendPeer(View view) {

        et_peer_id=findViewById(R.id.EditText_PeerID);
        peerID=et_peer_id.getText().toString();

        et_message = findViewById(R.id.EditText_messageToSend);
        messageToBeSent = et_message.getText().toString();
        mRtmMessage = mRtmClient.createMessage();
        mRtmMessage.setText(messageToBeSent);
        SendMessageOptions mSendMessageOptions = new SendMessageOptions();
        mSendMessageOptions.enableOfflineMessaging = true;


        mRtmClient.sendMessageToPeer(peerID, mRtmMessage, mSendMessageOptions, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                String text = "Message sent from " + uid + " To " + peerID + " : " + mRtmMessage.getText() + "\n";
                writeMessage(text);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                String text = "Message fails to send from " + uid + " To " + peerID + " Error ： " + errorInfo.toString() + "\n";
                writeMessage(text);
            }
        });
    }
}