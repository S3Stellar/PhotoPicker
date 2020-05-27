package com.naorfarag.exercise1naorjonathan.fragments;

import android.os.Bundle;

public interface FragmentUploadListener {
    String UPLOAD_KEY = "upload_key";

    void onUploadPerformed(Bundle bundle);
}
