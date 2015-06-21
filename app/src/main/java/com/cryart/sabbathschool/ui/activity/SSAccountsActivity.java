/*
 * Copyright (c) 2015 Vitaliy Lim <lim.vitaliy@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cryart.sabbathschool.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.cryart.sabbathschool.R;
import com.cryart.sabbathschool.util.SSAccounts;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.HashMap;
import java.util.Map;

public class SSAccountsActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private boolean SSGoogleSignInClicked;
    private boolean SSGoogleSignInProgress;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient SSGoogleApiClient;
    private SSAccounts _SSAccounts;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _SSAccounts = SSAccounts.getInstance(getApplicationContext());

        setContentView(R.layout.ss_accounts_activity);

        if (_SSAccounts._SSCognitoCredentialsProvider.getCachedIdentityId() != null){
            this.navigateToLoadingActivity();
        }

        SignInButton googleSignIn = (SignInButton) this.findViewById(R.id.ss_accounts_google_sign_in);
        googleSignIn.setOnClickListener(this);

        SSGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ss_accounts_google_sign_in && !SSGoogleApiClient.isConnecting()) {
            SSGoogleSignInClicked = true;
            SSGoogleApiClient.connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                SSGoogleSignInClicked = false;
            }

            SSGoogleSignInProgress = false;

            if (!SSGoogleApiClient.isConnecting()) {
                SSGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!SSGoogleSignInProgress) {
            if (SSGoogleSignInClicked && result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    SSGoogleSignInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    SSGoogleSignInProgress = false;
                    SSGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        SSGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        SSGoogleSignInClicked = false;

        new SSFetchGoogleTokenTask(this).execute();
    }

    public class SSFetchGoogleTokenTask extends AsyncTask<Void, Void, String> {
        public SSAccountsActivity _SSAccountsActivity;

        public SSFetchGoogleTokenTask(SSAccountsActivity _SSAccountsActivity) {
            this._SSAccountsActivity = _SSAccountsActivity;
        }

        @Override
        protected String doInBackground(Void... params) {
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(),
                        Plus.AccountApi.getAccountName(SSGoogleApiClient),
                        "audience:server:client_id:725806867451-4e6ghict1b5qo7540poa802qpvqif0qg.apps.googleusercontent.com");
            } catch (Exception e) {}
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                Map<String, String> logins = new HashMap<String, String>();
                logins.put("accounts.google.com", token);
                _SSAccounts._SSCognitoCredentialsProvider.setLogins(logins);
//                SSCognitoProvider.withLogins(logins);
                _SSAccountsActivity.navigateToLoadingActivity();
            }
        }

    }

    public void navigateToLoadingActivity(){
        Intent i = new Intent(getApplicationContext(), SSLoadingActivity.class);
        startActivity(i);
    }
}
