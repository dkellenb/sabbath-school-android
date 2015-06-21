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

import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.regions.Regions;

import java.util.List;

public class SSData {
    private static SSData _instance;
    public CognitoSyncManager _SSCognitoClient;

    private SSData(){}

    public synchronized static SSData getInstance(Context context) {
        if (_instance == null){
            _instance = new SSData();
            _instance._SSCognitoClient = new CognitoSyncManager(
                context,
                Regions.US_EAST_1,
                SSAccounts.getInstance(context)._SSCognitoCredentialsProvider
            );
        }
        return _instance;
    }

    private void saveData(String _SSDatasetName, String _SSKey, String _SSValue){
        Dataset _SSDataset = _SSCognitoClient.openOrCreateDataset(_SSDatasetName);
        _SSDataset.put(_SSKey, _SSValue);
        _SSDataset.synchronizeOnConnectivity(new DefaultSyncCallback(){
            @Override
            public void onSuccess(final Dataset dataset, List<Record> updatedRecords) {}
        });
    }

    public void saveComments(String _SSDayDate, String comments) {
        this.saveData(_SSDayDate + "_" + SSCore.LANGUAGE, SSConstants.SS_DATASET_COMMENTS_KEY, comments);
    }

    public void saveHighlights(String _SSDayDate, String highlights) {
        this.saveData(_SSDayDate + "_" + SSCore.LANGUAGE, SSConstants.SS_DATASET_HIGHLIGHTS_KEY, highlights);
    }
}
