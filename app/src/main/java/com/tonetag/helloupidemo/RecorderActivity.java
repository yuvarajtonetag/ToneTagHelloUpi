package com.tonetag.helloupidemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.tonetag.helloupi.ApiListener;
import com.tonetag.helloupi.TTUtils;
import com.tonetag.helloupi.ToneTagManager;
import com.tonetag.helloupi.chatbot.ChatBotActivity;
import com.tonetag.helloupi.chatbot.ChatBotListener;
import com.tonetag.helloupi.chatbot.Environment;
import com.tonetag.helloupi.chatbot.Language;

import java.security.InvalidKeyException;

public class RecorderActivity extends AppCompatActivity implements ChatBotListener, ApiListener {
    private static final String TAG = RecorderActivity.class.getSimpleName();

    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 333;
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 666;
    private static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 999;

    private Button btnReceive;
    private EditText mEtEmailAddress;
    private TextView tvRxData;
    private Spinner environmentSpinner, languageSpinner;
    private ToneTagManager mToneTagManager;
    private int selectedLanguageValue = 0;
    private String selectedEnvironment = "";
    private String SELECTED_SUB_KEY = "";
    private String BIC = "YOUR_BIC";
    private String SUB_KEY_STAGING = "YOUR_STAGING_SUBSCRIPTION_KEY";
    private String SUB_KEY_PRODUCTION = "YOUR_PRODCUTION_SUBSCRIPTION_KEY";

    @Override
    protected void onStart() {
        super.onStart();
        checkReadContactsPermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        initView();
    }

    /**
     * Initialise the views
     */

    private void initView() {
        mEtEmailAddress = findViewById(R.id.et_email_address);
        btnReceive = findViewById(R.id.btn_receive);
        tvRxData = findViewById(R.id.tvRxData);
        environmentSpinner = findViewById(R.id.sp_environment);
        languageSpinner = findViewById(R.id.language_spinner);
        initEnvironmentSpinner();
    }

    private void initEnvironmentSpinner() {
        // Create ArrayAdapter using the Language enum values
        ArrayAdapter<Environment> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, Environment.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter to Spinner
        environmentSpinner.setAdapter(adapter);

        // Set a listener to capture the selected language value
        environmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Retrieve the selected Language object from the Spinner
                Environment environment = (Environment) parentView.getItemAtPosition(position);
                // Retrieve the value of the selected Language
                selectedEnvironment = environment.getValue();
                // Do something with the selected language value
                // For example, you can display it or use it in your application logic
                Log.e(TAG, "Selected Environment Value: " + selectedEnvironment);
                initLanguageSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void initLanguageSpinner() {
        // Create ArrayAdapter using the Language enum values
        ArrayAdapter<Language> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_layout, Language.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter to Spinner
        languageSpinner.setAdapter(adapter);

        // Set a listener to capture the selected language value
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Retrieve the selected Language object from the Spinner
                Language selectedLanguage = (Language) parentView.getItemAtPosition(position);
                // Retrieve the value of the selected Language
                selectedLanguageValue = selectedLanguage.getValue();
                // Do something with the selected language value
                // For example, you can display it or use it in your application logic
                Log.e(TAG, "Selected Language Value: " + selectedLanguageValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    /**
     * Initialise the Hello UPI SDK
     */
    private void initHelloUpiSdk() {
        try {
            SELECTED_SUB_KEY = selectedEnvironment.equalsIgnoreCase("1") ? SUB_KEY_STAGING : SUB_KEY_PRODUCTION;
            //Set ToneTagManager Configuration
            mToneTagManager = ToneTagManager.builder()
                    .setContext(RecorderActivity.this)
                    .setBic(BIC) // Client's BIC
                    .setSubKey(SELECTED_SUB_KEY) // Subscription key either staging or production
//                    .setLangCode(Language.LANGUAGE_ENGLISH.getValue())
                    .setLangCode(selectedLanguageValue) // Set default Language using Language Enum
                    .setWelcomeText("Welcome to Hello UPI! Simply speak to send money or check your balance effortlessly") // Any Custom Message can be sent as welcome message
                    .setPoweredByDrawable(R.drawable.ic_powered_by) // Custom Image drawable to showcase at the top right corner of ChatBot except GIF
                    .setMicONGifDrawable(R.drawable.ic_voice_expand) // Only Custom GIF Image drawable to showcase the Voice recording started animation
                    .setMicOFFDrawable(R.drawable.ic_mic_off) // Only Custom Image drawable to showcase when Voice recording is stopped.
                    .showVolumeIcon(false) // Show or hide the Volume icon
                    .showKeyBoardIcon(false) // Show or hide the KeyBoard icon
//                    .setEnvironment(Environment.ENVIRONMENT_STAGING.getValue())
//                    .setEnvironment(Environment.ENVIRONMENT_PRODUCTION.getValue())
                    .setEnvironment(selectedEnvironment) // set Environment either staging or production on runtime using Environment Enum
                    .build();

            mToneTagManager.setApiListener(this);
            ChatBotActivity.setChatBotListener(this);

        } catch (InvalidKeyException e) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(RecorderActivity.this);
            mBuilder.setTitle("Error Initiating Manager \n" + e.getLocalizedMessage());
            mBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
    }

    /**
     * fetchSubscriptionKeyDetails method to get the subscription key details
     * params BIC - Client bic, SUB_KEY - Subscription Key, Environment - STAGING or PRODUCTION
     **/
    private void fetchSubscriptionKeyDetails() {
        if (null != mToneTagManager) {
            mToneTagManager.fetchSubscriptionKeyDetails(BIC, SELECTED_SUB_KEY, selectedEnvironment);
        }
    }

    /**
     * fetchLatestSubscriptionKey method to get the latest subscription key when SDK is expired
     * params BIC - Client bic, SUB_KEY - Subscription Key, Environment - STAGING or PRODUCTION
     **/
    private void fetchLatestSubscriptionKey() {
        if (null != mToneTagManager) {
            mToneTagManager.fetchLatestSubscriptionKey(BIC, SELECTED_SUB_KEY, selectedEnvironment);
        }
    }

    /**
     * Permission to read Contacts
     **/
    private void checkReadContactsPermission() {
        if (ActivityCompat.checkSelfPermission(RecorderActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RecorderActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Permission to read Phone State
     **/
    private void checkReadPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(RecorderActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RecorderActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
        }
    }

    public void onClick(View view) {
        if ((view.getId() == R.id.btn_receive) || (view.getId() == R.id.fab_hello_upi)) {
            hideKeyboard();
            if (ActivityCompat.checkSelfPermission(RecorderActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RecorderActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_REQUEST_CODE);
            } else {
                checkButtonState();
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtEmailAddress.getWindowToken(), 0);
    }

    private void checkButtonState() {
        tvRxData.setText("");
        String emailAddress = mEtEmailAddress.getText().toString().trim();
        if (!emailAddress.isEmpty()) {
            initHelloUpiSdk();
            startHelloUpiChatBot(emailAddress);
        } else {
            Toast.makeText(RecorderActivity.this, "Email address cannot be null/empty", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * startChatBot method to initiate Hello UPI SDK
     * param - Any unique identifier
     **/
    private void startHelloUpiChatBot(String deviceId) {
        if (mToneTagManager != null) {
            mToneTagManager.startChatBot(deviceId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PHONE_STATE_PERMISSION_REQUEST_CODE:
            case READ_CONTACTS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied
                    Log.e(TAG, "Read phone state permission denied");
                }
                break;

            case RECORD_AUDIO_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    checkButtonState();
                } else {
                    // Permission denied
                    Log.e(TAG, "Record audio permission denied");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvRxData.setText("Record audio permission denied");
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    /**
     * TTOnChatBotResponse method callback invokes when Hello UPI SDK completes it's flow
     * Params  txnType - Type of transactions like Check Balance, Change PIN, Reset PIN, SEND MONEY or MONEY Transfer
     * message - Callback entity to be handled by PSP App.
     * sessionId - Session ID unique identifier which is helpful to analyse the flow
     **/
    @Override
    public void TTOnChatBotResponse(int txnType, String message, String sessionId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!message.isEmpty()) {
                    tvRxData.setText("HelloUpi Sdk response" + "\n\n" +
                            "Session Id : " + sessionId + "\n\n" +
                            "Flow type : " + txnType + "\n\n" +
                            "Intent : " + TTUtils.getIntentValueByKey(txnType) + "\n\n" +
                            "Callback Entity : " + message);
                } else {
                    tvRxData.setText("HelloUpi Sdk response" + "\n\n" +
                            "Session Id : " + sessionId + "\n\n" +
                            "Flow type : " + txnType + "\n\n" +
                            "Intent : " + TTUtils.getIntentValueByKey(txnType));
                }
            }
        });
    }

    /**
     * TTOnApiResponse method callback invokes when PSP app calls to fetch Subscription key details or to fetch latest subscription key
     * Params  apiResponse - Callback which returns the api response.
     **/
    @Override
    public void TTOnApiResponse(String apiResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvRxData.setText("Latest Subscription Key: " + apiResponse);
            }
        });
    }

    /**
     * TTOnApiError method callback invokes when PSP app calls to fetch Subscription key details or to fetch latest subscription key
     * Params  code - Error code
     * Params  message - Error Message
     **/
    @Override
    public void TTOnApiError(int code, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvRxData.setText("Api Error: " + code + ", " + message);
            }
        });
    }

    /**
     * TTOnError method callback invokes when ChatBot returns any error while executing the flow.
     * Params  code - Error code
     * Params  message - Error Message
     **/
    @Override
    public void TTOnError(int code, String message) {
        Log.e(TAG, message);
        //SDK Key Expired, api call to fetch latest key and reinitialise ToneTag SDK
        if (code == 101) {
            mToneTagManager.fetchLatestSubscriptionKey(BIC, SELECTED_SUB_KEY, selectedEnvironment);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvRxData.setText("Error Code: " + code + ", Reason:" + message);
            }
        });
    }

    /**
     * getErrorResponseMessage method to return the SDK level error messages
     * Params  code - Error code
     **/
    private String getErrorMessage(int code) {
        return TTUtils.getErrorResponseMessage(code);
    }

}
