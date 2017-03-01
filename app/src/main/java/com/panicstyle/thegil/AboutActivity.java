package com.panicstyle.thegil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.panicstyle.thegil.R;

public class AboutActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("공동육아앱에 대해서...");

        Context context = AboutActivity.this;
        String version;
        try {
        	PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        	version = i.versionName;
        } catch (NameNotFoundException e) { 
        	version = "Unknown";
        }
        
        String versionInfo = "Version : " + version;

        TextView t = (TextView)findViewById(R.id.textVersion); 
        t.setText(versionInfo);        
                
    }
}
