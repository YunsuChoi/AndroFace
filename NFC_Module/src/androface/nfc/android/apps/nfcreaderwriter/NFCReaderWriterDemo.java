/**
 * @version $Id: NFCReaderWriterDemo.java 168 2012-08-09 09:14:39Z mroland $
 *
 * @author Michael Roland <mi.roland@gmail.com>
 *
 * Copyright (c) 2012 Michael Roland
 *
 * ALL RIGHTS RESERVED.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name Michael Roland nor the names of any contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MICHAEL ROLAND BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package androface.nfc.android.apps.nfcreaderwriter;

import androface.nfc.ndef.UriRecordHelper;
import androface.nfc.utils.StringUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

/**
 * Utilities for URI record encoding.
 */
public class NFCReaderWriterDemo extends Activity {
	private static final String ACTION_USB_PERMISSION = 
			"androface.nfc.android.apps.nfcreaderwriter.USB_PERMISSION";
	// USB가 감지되었을 때의 이벤트를 받음.
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				    // 사용자에게 Android Accessory Protocol을 구현한 장비가 연결되면
				    // 수락할 것인지 문의한 다이얼로그에 대한 사용자의 선택 결과를 받는다.
					synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						// 수락했을 경우
						showMessage("receiver : USB Host 연결됨.");
					} else {
						Log.d(NFCReaderWriterDemo.class.getName(), 
								"permission denied for accessory "
								+ accessory);
					}
					
					openAccessory(accessory);
					// 연결 수락 결과를 받았음을 표시
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				// Android Accessory Protocol을 구현한 장비의 연결이 해제되었을 때
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				// 앱이 사용하고 있는 장비와 같은 것인지 확인
				if (accessory != null && accessory.equals(mAccessory)) {
					showMessage("USB Host 연결 해제됨.");
					closeAccessory();
				}
			}
		}
	};
	
	
// TODO: Part 1/Step 4: Creating our Activity
    private static final int DIALOG_WRITE_URL = 1;
    private EditText mMyUrl;
    private Button mMyWriteUrlButton;
    private boolean mWriteUrl = false;
    
// TODO: Part 1/Step 6: Foreground Dispatch (Detect Tags)
    private static final int PENDING_INTENT_TECH_DISCOVERED = 1;
    private NfcAdapter mNfcAdapter;

// TODO: Part 2/Step 1: A Simple Dialog Box: Detected a Tag
    private static final int DIALOG_NEW_TAG = 3;
    private static final String ARG_MESSAGE = "message";

    //ADKs
	private TextView txtMsg;
	private UsbManager mUsbManager;
	private UsbAccessory mAccessory;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;
	private ToggleButton btnLed;
    private AdkHandler handler;
    private boolean isChecked = false;
    private static final int AdkCommandStr = 4;
    
    // get specific string from string.xml
    //private final String keyCode = getResources().getString(R.string.keyCode);
    private String keyCode = "NFC";
    private TextView keyCodeView;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtMsg = (TextView)this.findViewById(R.id.txtMsg);
        btnLed = (ToggleButton)this.findViewById(R.id.btnLed); // 버튼
        keyCodeView =(TextView)this.findViewById(R.id.keyCodeView);
        
        //Android Accessory Protocol을 구현한 장비의 연결에 대한 브로드캐스트 리시버 등록
      	IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
      	filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
      	registerReceiver(mUsbReceiver, filter);
      	
      	mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		
        btnLed.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        	@Override
            public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
				if(handler != null && handler.isConnected()){  
                      handler.write((byte)0x1, (byte)0x0, isChecked ? 1 : 0);
                      showMessage("AndroFace " + (isChecked ? "On" : "Off"));
                 }
            }});
        
// TODO: Part 1/Step 4: Creating our Activity
        mMyUrl = (EditText) findViewById(R.id.myUrl);
        mMyWriteUrlButton = (Button) findViewById(R.id.myWriteUrlButton);

        // Set action for "Write URL to tag..." button:
        mMyWriteUrlButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mWriteUrl = true;
                NFCReaderWriterDemo.this.showDialog(DIALOG_WRITE_URL);
            }
        });

// TODO: Part 3/Step 2: Handle NDEF_DISCOVERED Intent
        // Resolve the intent that started us:
        resolveIntent(this.getIntent(), false);
    }

    /**
     * Called when the activity gets focus.
     */
    @Override
    public void onResume() {
        super.onResume();
     // 앱이 화면에 보일 때 안드로이드 장비에 Android Accessory Protocol을 
     		// 구현한 USB Host가 연결되어 있는지 확인
     		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
     		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
     		if (accessory != null) { // Android Accessory Protocol를 구현한 장비를 찾았을 경우
     			if (mUsbManager.hasPermission(accessory)) {
     				showMessage("onresume : USB Host 연결됨.");
     				openAccessory(accessory);
     			} else {
     				synchronized (mUsbReceiver) {
     					if (!mPermissionRequestPending) {
     						mUsbManager.requestPermission(accessory,
     								mPermissionIntent); // USB 연결을 통해 장비에 연결해도 되는지 사용자에게 문의
     						mPermissionRequestPending = true; // 연결권한을 물어보드 코드를 실행했음을 표시
     					}
     				}
     			}
     		} else {
     			Log.d(NFCReaderWriterDemo.class.getName(), "mAccessory is null");
     		}
     		
// TODO: Part 1/Step 6: Foreground Dispatch (Detect Tags)
        // Retrieve an instance of the NfcAdapter:
        NfcManager nfcManager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = nfcManager.getDefaultAdapter();

        // Create a PendingIntent to handle discovery of Ndef and NdefFormatable tags:
        PendingIntent pi = createPendingResult(PENDING_INTENT_TECH_DISCOVERED, new Intent(), 0);

        // Enable foreground dispatch for Ndef and NdefFormatable tags:
        mNfcAdapter.enableForegroundDispatch(
                this,
                pi,
                new IntentFilter[]{ new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) },
                new String[][]{
                    new String[]{"android.nfc.tech.NdefFormatable"},
                    new String[]{"android.nfc.tech.Ndef"}
                });
    }

    /**
     * Called when the activity loses focus.
     */
    @Override
    public void onPause() {
        super.onPause();
        closeAccessory();

// TODO: Part 1/Step 7: Cleanup Foreground Dispatch
        // Disable foreground dispatch:
        mNfcAdapter.disableForegroundDispatch(this);
    }

// TODO: Part 3/Step 2: Handle NDEF_DISCOVERED Intent
    /**
     * Called when activity receives a new intent.
     */
    @Override
    public void onNewIntent(Intent data) {
        resolveIntent(data, false);
    }

// TODO: Part 1/Step 8: Receive Foreground Dispatch Intent
    /**
     * Called when a pending intent returns (e.g. our foreground dispatch).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PENDING_INTENT_TECH_DISCOVERED:
                resolveIntent(data, true);
                break;
        }
    }

// TODO: Part 1/Step 8: Receive Foreground Dispatch Intent
    /**
     * Method to handle any intents that triggered our activity.
     * @param data                The intent we received and want to process.
     * @param foregroundDispatch  True if this intent was received through foreground dispatch.
     */
    private void resolveIntent(Intent data, boolean foregroundDispatch) {
        String action = data.getAction();

// TODO: Part 3/Step 2: Handle NDEF_DISCOVERED Intent
        // We were started from the recent applications history: just show our main activity
        if ((data.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) { return; }
        
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
    
// TODO: Part 1/Step 8: Receive Foreground Dispatch Intent
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = data.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (foregroundDispatch && mWriteUrl) {
                // This is a foreground dispatch and we want to write our URL
                mWriteUrl = false;

// TODO: Part 1/Step 9: Prepare our URI NDEF Message
                // Get URL from text box:
                String urlStr = mMyUrl.getText().toString();

                // Convert to URL to byte array (UTF-8 encoded):
                byte[] urlBytes = urlStr.getBytes(Charset.forName("UTF-8"));

                //// Use UriRecordHelper to determine an efficient encoding:
                //byte[] urlPayload = new byte[urlBytes.length + 1];
                //urlPayload[0] = 0;  // no prefix reduction
                //System.arraycopy(urlBytes, 0, urlPayload, 1, urlBytes.length);

                // Better: Use UriRecordHelper to determine an efficient encoding:
                byte[] urlPayload = UriRecordHelper.encodeUriRecordPayload(urlStr);

                // Create a NDEF URI record (NFC Forum well-known type "urn:nfc:wkt:U")
                NdefRecord urlRecord = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN, /* TNF: NFC Forum well-known type */
                        NdefRecord.RTD_URI,        /* Type: urn:nfc:wkt:U */
                        new byte[0],               /* no ID */
                        urlPayload);

                // Create NDEF message from URI record:
                NdefMessage msg = new NdefMessage(new NdefRecord[]{urlRecord});

// TODO: Part 1/Step 10: Write NDEF to Preformatted Tag
                // Write NDEF message to tag:

                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag != null) {
                    // Our tag is already formatted, we just need to write our message

                    try {
                        // Connect to tag:
                        ndefTag.connect();

                        // Write NDEF message:
                        ndefTag.writeNdefMessage(msg);
                    } catch (Exception e) {
                    } finally {
                        // Close connection:
                        try { ndefTag.close(); } catch (Exception e) { }
                    }
                } else {

// TODO: Part 1/Step 11: Write NDEF to Unformatted Tag
                    NdefFormatable ndefFormatableTag = NdefFormatable.get(tag);
                    if (ndefFormatableTag != null) {
                        // Our tag is not yet formatted, we need to format it with our message

                        try {
                            // Connect to tag:
                            ndefFormatableTag.connect();

                            // Format with NDEF message:
                            ndefFormatableTag.format(msg);
                        } catch (Exception e) {
                        } finally {
                            // Close connection:
                            try { ndefFormatableTag.close(); } catch (Exception e) { }
                        }
                    }

// TODO: Part 1/Step 10: Write NDEF to Preformatted Tag
                }

// TODO: Part 1/Step 8: Receive Foreground Dispatch Intent
                dismissDialog(DIALOG_WRITE_URL);

// TODO: Part 2/Step 2: Detect the Tag
            } else {
                // Let's read the tag whenever we are not in write mode
              
                // Retrieve information from tag and display it
                StringBuilder tagInfo = new StringBuilder();
            	String tagUriData;
            	
                // Get tag's UID:
                byte[] uid = tag.getId();
                //*tagInfo.append("UID: ").append(StringUtils.convertByteArrayToHexString(uid)).append("\n\n");

// TODO: Part 2/Step 3: Read NDEF Message from Tag
                // Get tag's NDEF messages:
                Parcelable[] ndefRaw = data.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] ndefMsgs = null;
                if (ndefRaw != null) {
                    ndefMsgs = new NdefMessage[ndefRaw.length];
                    for (int i = 0; i < ndefMsgs.length; ++i) {
                        ndefMsgs[i] = (NdefMessage) ndefRaw[i];
                    }
                }

// TODO: Part 2/Step 4: Find URI Record in NDEF Messages
                // Find URI records:

                if (ndefMsgs != null) {
                    // Iterate through all NDEF messages on the tag:
                    for (int i = 0; i < ndefMsgs.length; ++i) {
                        // Get NDEF message's records:
                        NdefRecord[] records = ndefMsgs[i].getRecords();

                        if (records != null) {
                            // Iterate through all NDEF records:
                            for (int j = 0; j < records.length; ++j) {
                                // Test if this record is a URI record:
                                if ((records[j].getTnf() == NdefRecord.TNF_WELL_KNOWN)
                                        && Arrays.equals(records[j].getType(), NdefRecord.RTD_URI)) {
                                    byte[] payload = records[j].getPayload();
                                    
                                    //// Drop prefix identifier byte and convert remaining URL to string (UTF-8):
                                    //String uri = new String(Arrays.copyOfRange(payload, 1, payload.length), Charset.forName("UTF-8"));

                                    // Better: Use UriRecordHelper to decode URI record payload:
                                    String uri = UriRecordHelper.decodeUriRecordPayload(payload);
                                    
                                    //*tagInfo.append("URI: ").append(uri).append("\n");
                                    tagInfo.append(uri);
                                }
                            }
                            
                        }
                    }
                }

// TODO: Part 2/Step 5: Display Tag Data in a Dialog Box
                Bundle args = new Bundle();
                args.putString(ARG_MESSAGE, tagInfo.toString());
                dataMatched(AdkCommandStr, args);
                //showDialog(DIALOG_NEW_TAG, args);
               
// TODO: Part 1/Step 8: Receive Foreground Dispatch Intent
            }
        }
    }
}
	public void dataMatched(int id, Bundle args){
		switch (id){
			case AdkCommandStr:
				if (args != null) {
                    String message = args.getString(ARG_MESSAGE);
                    if (message.equals(keyCode)) {
                    	showMessage("keyCode Matched!!!");
                        keyMessage(message);
                    	if(handler != null && handler.isConnected()){
                			handler.write((byte)0x1, (byte)0x0, (int) 1);
                		}
                    }
                    else{
                    	showMessage("Wrong KeyCode...");
                    	keyMessage(message);
                    	if(handler !=null && handler.isConnected()){
                    		handler.write((byte)0x1, (byte)0x0, (int) 0);
                    		
                    	}
                    }
				}
				break;
		}
	}

	/**
     * Called when a dialog is created.
     */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
// TODO: Part 1/Step 5: A Dialog Box: Ready to Write to Tag
            case DIALOG_WRITE_URL:
                return new AlertDialog.Builder(this)
                        .setTitle("Write URL to tag...")
                        .setMessage("Touch tag to start writing.")
                        .setCancelable(true)
                        .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int arg) {
                                d.cancel();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface d) {
                                mWriteUrl = false;
                            }
                        }).create();
// TODO: Part 2/Step 1: A Simple Dialog Box: Detected a Tag
            case DIALOG_NEW_TAG:
                return new AlertDialog.Builder(this)
                        .setTitle("Tag detected")
                        .setMessage("")
                        .setCancelable(true)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int arg) {
                                d.dismiss();
                            }
                        })
                        .create();
        }
        return null;
    }

// TODO: Part 2/Step 1: A Simple Dialog Box: Detected a Tag
    /**
     * Called before a dialog is shown to the user.
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
            case DIALOG_NEW_TAG:
                if (args != null) {
                    String message = args.getString(ARG_MESSAGE);
                    if (message != null) { ((AlertDialog) dialog).setMessage(message);}
                }
                break;
        }
    }
  	@Override
  	protected void onDestroy() { // 액티비티가 소멸될 때 호출
  		// 브로드캐스트 리시버를 제거
  		unregisterReceiver(mUsbReceiver);
  		super.onDestroy();
  	}
  	
    private void openAccessory(UsbAccessory accessory){
		mAccessory = accessory;
        if(handler == null)
            handler = new AdkHandler();

       handler.open(mUsbManager, mAccessory);
	}
	
	private void closeAccessory(){
        if(handler != null && handler.isConnected())
            handler.close();
        mAccessory = null;
    }
	
	private void showMessage(String msg){
		txtMsg.setText(msg);
	}
	
	private void keyMessage(String msg){
		keyCodeView.setText(msg);
	}
	
	

	}

