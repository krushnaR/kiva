package com.sapience.kiva.kivafinal;

import java.util.ArrayList;
import java.util.Locale;

import com.sapience.kiva.kivafinal.DeviceListFragment.DeviceActionListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClientActivity extends Activity implements ChannelListener, DeviceActionListener, ConnectionInfoListener {
	
	
	private static final int REQ_CODE_SPEECH_INPUT = 1;
	private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = true;
    private boolean retryChannel = false;
    
    //Fragment
    TalkHistoryFragment history;
    
    //UI views
    Button setTarget;
    EditText targetInput;
 

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
	private WifiP2pInfo info;
	private String peerAddress;
	private FileServerAsyncTask asyncTask;
	FragmentManager fragmentManager;
	
	private CommandFeed cmd = new CommandFeed();
	private Response responder = new Response();
	private TextToSpeech textToSpeech;
    
    
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		
		//Fragment history init
		history = new TalkHistoryFragment();
		
		//UI initialization for manual target setting
		targetInput = (EditText) findViewById(R.id.targetEdit);
		setTarget = (Button) findViewById(R.id.setTarget);
		
		setTarget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String target = targetInput.getEditableText().toString();
				if(!target.equals("")){
					if(target.equals("401") ||
							target.equals("402") ||
							target.equals("403") ||
							target.equals("404"))
						send(target);
					else
						Toast.makeText(ClientActivity.this, "Invalid Target", Toast.LENGTH_SHORT).show();
				}
				
				else
					Toast.makeText(ClientActivity.this, "Blank Input", Toast.LENGTH_SHORT).show();
				
			}
		});
		
		//for wifi
        
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        
        //TextToSpeech Initialization
        
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR)
				{
					textToSpeech.setLanguage(Locale.US);
				}
				
			}
		});
	}
	
	@Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.on) {
			startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			return true;
		}
		if (id == R.id.find){
			if (!isWifiP2pEnabled) {
                Toast.makeText(ClientActivity.this, "Wifi P2P is off",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.onInitiateDiscovery();
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(ClientActivity.this, "Discovery Initiated",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(ClientActivity.this, "Discovery Failed : " + reasonCode,
                            Toast.LENGTH_SHORT).show();
                }
            });
			return true;
		}
		
		if(id == R.id.send){
			
			send("403");
		}
		
		if(id == R.id.talk){
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
					"How can I help you?");
			try {
				startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
			} catch (ActivityNotFoundException a) {
				Toast.makeText(getApplicationContext(),
						"Device not supported",
						Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
    }
	
	public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            	
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(ClientActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(ClientActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(ClientActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }




	@Override
	public void showDetails(WifiP2pDevice device) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}


	
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			switch (requestCode) {
			case REQ_CODE_SPEECH_INPUT: {
				if (resultCode == RESULT_OK && null != data) {

					ArrayList<String> result = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					String in = result.get(0);
				
					history.updateChat(in, false);
					
					//Responder
					String response = null;
					int number = -1;
			        
			        if(in == null){
			        	Log.d(SwichActivity.TAG, "Null Input");
			        }
			        else{
			        	  	number = cmd.getGreetingNumber(in);
			        	  	if(number != -1)
			        	  		response = responder.getResponseForGreeting(number);
			        	  	else{
			        		  	number = cmd.getQuestionNumber(in);
			        		  	if(number != -1)
			        	  		response = responder.getResponseForQuestion(number);
			        		  	else{
			        		  		number = cmd.getTaskNumber(in);
			        		  		if(number != -1)
			        		  		response = responder.getResponseForTask(number);
			        		  		else
			        		  			response = Response.notFound;
			        		  	}
			        	  	}
			        		
			        }
			              
			        if(response != null){
			        	Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
			        	
			        	history.updateChat(response, true);
			        	
			        	//TextToSpeech..
			        	textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null);
			        }
			        else
			        	Log.d(SwichActivity.TAG, "Getting a null Response");
			        
			    }

					
				}
				break;
			}	
			}


	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		this.info = info;
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		//collecting info here...
		if(info.groupFormed && !info.isGroupOwner){
			this.peerAddress = info.groupOwnerAddress.getHostAddress();
		}
		else{
			asyncTask = new FileServerAsyncTask(this);
			asyncTask.execute();
		}
		
		//swapFragment();
		}
		
	
	private void swapFragment() {
		
		fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		
		transaction.replace(R.id.frag_list, history);
		transaction.addToBackStack(null);
		transaction.commit();
	}


	public void onDataReceived(String data){
		this.peerAddress = data;
	}
	
	public void send(String data){
		if(info != null)
		{
			if(peerAddress != null){
				Log.d("Wifi", "Intent----------- ");
	            Intent serviceIntent = new Intent(this, FileTransferService.class);
	            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
	            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
	                    peerAddress);
	            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
	            serviceIntent.putExtra(FileTransferService.EXTRAS_DATA, data);
	            startService(serviceIntent);
			}
		}
		else
			Toast.makeText(this, "No info found", Toast.LENGTH_SHORT).show();
	}
	}


