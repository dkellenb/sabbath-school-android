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

package com.cryart.sabbathschool.util;

import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.cryart.sabbathschool.ui.activity.SSAccountsActivity;
import com.facebook.login.LoginManager;

public class SSSignOutDialogPreference extends DialogPreference {
    private Context prefContext;

    public SSSignOutDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        prefContext = context;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            SSAccounts _SSAccounts = SSAccounts.getInstance(prefContext);

            _SSAccounts._SSCognitoCredentialsProvider.clear();
            try {
                _SSAccounts._SSCognitoCredentialsProvider.clear();
                LoginManager.getInstance().logOut();
            } catch (Exception e){}

            Intent intent = new Intent(prefContext, SSAccountsActivity.class);
            prefContext.startActivity(intent);
        }
    }
}
