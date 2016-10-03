package com.example.yinseng.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Configuration.ApiConfigs;
import Encryption.OpenSSLEncryption;
import Encryption.RSACrypto;
import Models.Body;
import Models.Response.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvKcvBSZ5OsXBrTfTY6wx\n" +
            "olmNjqM2opOXAHOzLDaN2aO3U9izpNOTisGxeqbGw9BbXfektu7Q+PLdrmgwpxSK\n" +
            "cmrAfoEj4iez2ok+NCWMjs7utjixqtBe99A5gTQqf/oHOMZonKlQy/232y0k4bk9\n" +
            "0xAa3kSvxsLZFIOUjf3gHxlWeP4AJXceLNQmGR7i6EIBAlWChV/sCUT8PW8AJhU9\n" +
            "LnARn3rzcijdtzm46/fndIyKbnQcnTwbFuKwjyZoKvOLhDvPY9kl6sSo7gReefPV\n" +
            "wuyvv43GIBp88p+PuPjkPXjW0kmTxCgmLLU40+qqB7ZpCUtIbWKyUjAob5Vkfg4j\n" +
            "jQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private String publicKeyPEM;
    private String DEVICE_UUID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        publicKeyPEM = PUBLIC_KEY.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        DEVICE_UUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                   // attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        CharSequence m= "Thanks for using alertify";
        Toast.makeText(getApplicationContext(),m, Toast.LENGTH_SHORT).show();
        //callLogin("yinseng@yahoo.com","123");


    }


    private void callLogin(String email,String password){
        try {
            // need to delete , i put in here because android not allow to input short password, but my password is too short , so i replace the value here
            password = "123";
            // create body pass json
            Long tsLong = System.currentTimeMillis() + (60 * 60 * 1000);
            String ts = tsLong.toString();

            Log.e("Email ", email);
            Log.e("Password", password);


            JSONObject obj = new JSONObject();
            obj.put("email",email);//"yinseng@yahoo.com"
            obj.put("password", password);//"123"
//            obj.put("remember_me", "");
            obj.put("signature", ts); // timestpan now + 60 * 60
            Log.i("seng", "json " + obj.toString());

            // random key 16 bytes
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            String AES_Key = Base64.encodeToString(salt, Base64.NO_WRAP);//"32_length_String";
            System.out.println("\n AES_Key " + AES_Key);


            // AES encrypt
            OpenSSLEncryption openSSLEncryption = new OpenSSLEncryption(AES_Key);
            String AES_Encrypted = openSSLEncryption.encrypt(obj.toString());


            // RSA encrypt key
            RSACrypto rsa = new RSACrypto();
            byte[] message = AES_Key.getBytes();//(Base64.encodeToString(salt, Base64.NO_PADDING)).getBytes("UTF8");
            byte[] RSA_Cipher = rsa.encrypt(rsa.readPublicKey(publicKeyPEM), message);
            String RSA_Encrypted = Base64.encodeToString(RSA_Cipher, 2);


//            new HttpSignin().execute(RSA_Encrypted, AES_Encrypted,DEVICE_UUID);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute(RSA_Encrypted, AES_Encrypted,DEVICE_UUID);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            showProgress(true);
            callLogin(email,password);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute(RSA_Encrypted_Diggest, AES_Encrypted_Diggest,DEVICE_UUID);
            //            mAuthTask.execute((Void) null);




        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public boolean isNetworkAvailable() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, Response> {

        private final String mEmail;
        private final String mPassword;
        private String response_body;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Response doInBackground(String... params) {
            Log.e("RSA Cipher Text", params[0]);
            Log.e("AES Cipher Text", params[1]);
            Log.e("Device ID", params[2]);

            String RSA_Cipher = params[0];
            String AES_Cipher = params[1];
            String Device_ID = params[2];

            try {

                // Create and populate a simple object to be used in the request
                Body body = new Body();
                body.setData(AES_Cipher);

                // Set the Content-Type header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application", "json"));
                requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                requestHeaders.add("X-AL-Sign-Key", RSA_Cipher);
                requestHeaders.add("X-AL-Connect-ID", Device_ID);
                requestHeaders.add("X-AL-Request-ID", "VPt6ixFFnEYiKfGL/UdYTaCYQv7zSki/8OHyPxvtvXg=");


                HttpEntity<Body> requestEntity = new HttpEntity<Body>(body, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

                // Add the Jackson and String message converters
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                String url = ApiConfigs._Server + ApiConfigs._Login;
                Response response = restTemplate.postForObject(url, requestEntity, Response.class);


                return response;


            }catch (final HttpStatusCodeException e ){
//                Log.e("HttpStatusCodeException", e.getMessage(), e);
//                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getResponseBodyAsString());
                Log.d("body",e.getResponseBodyAsString());
                response_body = e.getResponseBodyAsString() ;
            }catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
                e.printStackTrace();
            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            mAuthTask = null;
            showProgress(false);

            try{
                if (response.getCode().equals("200")) {
                    //finish();
                    Context context = getApplicationContext();
                    CharSequence text = "Login Successfully! Hello " + response.getResult().getProfile().getFull_name();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
//                    Message msg = new Message();
//                    msg.Toast(getApplicationContext(),text);

                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }catch (Exception e){
                try {
                    JSONObject obj = new JSONObject(response_body);
                    Log.d("body",obj.getString("result"));
                    CharSequence text = obj.getString("result");

                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                    mEmailView.setError(obj.getString("result"));
                    mEmailView.requestFocus();
                }catch (Exception ee){

                }

            }


        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

