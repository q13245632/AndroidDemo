package com.example.smstest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.restrictions.RestrictionsReceiver;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {


	private TextView sender;
	private TextView content;
	private EditText to;
	private EditText msginput;
	private Button send;
	private IntentFilter receiverFilter;
	private IntentFilter sendFilter;
	private SendStatusReceiver sendStatusReceiver;
	private MessageReceiver messageReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sender = (TextView) findViewById(R.id.sender);
		content = (TextView) findViewById(R.id.content);
		to = (EditText) findViewById(R.id.to);
		msginput = (EditText) findViewById(R.id.msg_input);
		send = (Button) findViewById(R.id.send);
		sendFilter = new IntentFilter();
		sendFilter.addAction("SENT_SMS_ACTION");
		sendStatusReceiver = new SendStatusReceiver();
		registerReceiver(sendStatusReceiver, sendFilter);
		
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SmsManager smsManager = SmsManager .getDefault();
				Intent sendIntent = new Intent("SENT_SMS_ACTION");
				PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, sendIntent, 0);
				smsManager.sendTextMessage(to.getText().toString(), null, msginput.getText().toString(), pi, null);
			}
		});
		receiverFilter = new IntentFilter();
		receiverFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		//receiverFilter.setPriority(100);提高MessageReceiver的优先级，使其能够先于系统短信程序收到短信广播
		messageReceiver = new MessageReceiver();
		registerReceiver(messageReceiver, receiverFilter);
		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(messageReceiver);
		unregisterReceiver(sendStatusReceiver);
	}


	class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for(int i = 0;i<messages.length;i++){
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			}
			String address = messages[0].getOriginatingAddress();
			String fullMessage = "";
			for(SmsMessage message : messages){
				fullMessage += message.getMessageBody();
			}
			sender.setText(address);
			content.setText(fullMessage);
			//abortBroadcast();中止广播的继续传播，拦截短信
		}
		
	}
	
	class SendStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(getResultCode() == RESULT_OK){
				Toast.makeText(context, "Send succeeded", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "Send failed", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
