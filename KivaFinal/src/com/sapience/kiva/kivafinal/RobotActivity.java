package com.sapience.kiva.kivafinal;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.felhr.usbserial.UsbSerialInterface.UsbReadCallback;
import com.sapience.kiva.kivafinal.DeviceListFragment.DeviceActionListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class RobotActivity extends Activity implements ChannelListener, DeviceActionListener, ConnectionInfoListener {
	
	private final String ACTION_USB = "com.sapience.kiva.kivafinal.USB_PERMISSION";
	static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
	public String target;
	private FileServerAsyncTask asyncTask;


	//Database
	DbHelper helper;
	PathFinder pathFinder;
	private String state = "forward";
	
	//USB
	
	UsbDevice usbDevice;
	UsbDeviceConnection connection;
	UsbSerialDevice serialPort;
	UsbManager usbManager;
	public final String ACTION_USB_PERMISSION = "com.sapience.kiva.kivafinal.USB_PERMISSION";
	
	//Wifi initializations
	
	private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = true;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    
    
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
	
	private boolean isUSBConnected = false;
	private boolean isClientConnected = false;
	private boolean isPositionSet = false;
	private boolean isTargetSet = false;
	
	TextView usbStatus;
	TextView clientStatus;
	TextView positionStatus;
	TextView targetStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robot);
		
		usbStatus = (TextView) findViewById(R.id.usbConnectionStatus);
		clientStatus = (TextView) findViewById(R.id.wifiDirectConnectionStatus);
		positionStatus = (TextView) findViewById(R.id.postionStatus);
		targetStatus = (TextView) findViewById(R.id.tergetStatus);
		
		//for USB
		
		usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
		IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        
        //for wifi
        
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        
        //for Database
        helper =  new DbHelper(this);
        pathFinder = new PathFinder(this);
        
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
  
	
	UsbReadCallback mCallback = new UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                
                if(data.equals("go"))
                	launchScanner();
                else if(data.equals("continue"))
                	if(serialPort != null)
    					serialPort.write(RobotActivity.this.state.getBytes());
                	
            
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    };
	
	
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(intent.getAction().equals(ACTION_USB_PERMISSION)){
				boolean granted  = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
				
				if(granted){
					connection = usbManager.openDevice(usbDevice);
					serialPort = UsbSerialDevice.createUsbSerialDevice(usbDevice, connection);
					
					if(serialPort != null){
						if(serialPort.open()){
							serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            
                            isUSBConnected = true;
                            usbStatus.setText("USB Attached");

						}
						else
							usbStatus.setText("Serial Port not open");
					}
					else
						usbStatus.setText("Serial Port null");
				}
				else
					usbStatus.setText("USB permission not Granted.");
			}
			
			else if(intent.getAction().equals(usbManager.ACTION_USB_DEVICE_ATTACHED)){
				
				HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
                if (!usbDevices.isEmpty()) {
                    boolean keep = true;
                    for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                        usbDevice = entry.getValue();
                        
                        int deviceVID = usbDevice.getVendorId();
                        if (deviceVID == 9025)//Arduino Vendor ID
                        {
                            PendingIntent pi = PendingIntent.getBroadcast(RobotActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                            usbManager.requestPermission(usbDevice, pi);
                            
                            keep = false;
                        } else {
                            connection = null;
                            usbDevice = null;
                        }

                        if (!keep)
                            break;
                    }
                }
			}
			else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
				isUSBConnected = false;
				usbStatus.setText("USB detached");
			}
			
		}
	};
	private WifiP2pInfo info;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.robot, menu);
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
                Toast.makeText(RobotActivity.this, "Wifi P2P is off",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.onInitiateDiscovery();
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(RobotActivity.this, "Discovery Initiated",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(RobotActivity.this, "Discovery Failed : " + reasonCode,
                            Toast.LENGTH_SHORT).show();
                }
            });
			return true;
		}
		
		if(id == R.id.db){
			onDataReceived("403");
			return true;
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
                Toast.makeText(RobotActivity.this, "Connect failed. Retry.",
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
                        Toast.makeText(RobotActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(RobotActivity.this,
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


	private void launchScanner() {
		Toast.makeText(this, "Launching Scanner..", Toast.LENGTH_SHORT).show();
		try {
			Intent intent = new Intent(ACTION_SCAN);
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException anfe) {
			showDialog(RobotActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No");
		}
	}
	
	private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
		downloadDialog.setTitle(title);
		downloadDialog.setMessage(message);
		downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				try {
					act.startActivity(intent);
				} catch (ActivityNotFoundException anfe) {

				}
			}
		});
		downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
			}
		});
		return downloadDialog.show();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				
				String path = pathFinder.path(contents, this.state, this.target);
				
				if(!path.equals("left") && !path.equals("right") && !path.equals("stop"))
					this.state = path;
				
				//send path via USB
				if(serialPort != null)
					serialPort.write(path.getBytes());
				else{
					Toast.makeText(this,"Contents: " + contents + ", Path: " + path, Toast.LENGTH_LONG).show();
					launchScanner();
				}
			}
		}
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		this.info = info;
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		this.isClientConnected = true;
		this.clientStatus.setText("Coonected to client");
		
		if(info.groupFormed && !info.isGroupOwner){
			Intent serviceIntent = new Intent(this, FileTransferService.class);
            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                    info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
            serviceIntent.putExtra(FileTransferService.EXTRAS_DATA, FileServerAsyncTask.SEND_ADDRESS);
            startService(serviceIntent);
		}
		
		asyncTask = new FileServerAsyncTask(this);
		asyncTask.execute();
	}
	
	public void onDataReceived(String data){
		this.target = data;
		this.isTargetSet = true;
		this.targetStatus.setText("Target Set: "+ data);
		launchScanner();
	}
	

}
