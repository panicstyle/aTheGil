package com.panicstyle.thegil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements Runnable {
    private ListView m_listView;

    private ProgressDialog m_pd;
    private int m_LoginStatus;
    static final int SETUP_CODE = 1234;
    private String m_strErrorMsg = "";
    private List<HashMap<String, Object>> m_arrayItems;
    private TheGilApplication m_app;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private static class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        List<HashMap<String, Object>> arrayItems;

        public EfficientAdapter(Context context, List<HashMap<String, Object>> data) {
            mInflater = LayoutInflater.from(context);
            arrayItems = data;
        }

        public int getCount() {
            return arrayItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            HashMap<String, Object> item;
            item = arrayItems.get(position);
            String title = (String) item.get("title");
            String type = (String) item.get("type");
            int isNew = (Integer) item.get("isNew");

            if (type.equalsIgnoreCase("group")) {
                convertView = mInflater.inflate(R.layout.list_group_boardview, null);
                GroupHolder holder;
                holder = new GroupHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
                holder.title.setText(title);
            } else {
                ViewHolder holder;

                convertView = mInflater.inflate(R.layout.list_item_boardview, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
                holder.title.setText(title);
                if (isNew == 1) {
                holder.icon.setImageResource(R.drawable.icon_new);
                } else {
                    holder.icon.setImageResource(0);
                }
            }

            return convertView;
        }

        static class ViewHolder {
            TextView title;
            ImageView icon;
        }

        static class GroupHolder {
            TextView title;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
//                Log.v(TAG,"Permission is granted");
                return true;
            } else {

//                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_listView = (ListView) findViewById(R.id.listView);
        m_arrayItems = new ArrayList<HashMap<String, Object>>();

        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                String boardName = null;
                String boardId = null;
                item = (HashMap<String, Object>) m_arrayItems.get(position);
                boardName = (String) item.get("title");
                boardId = (String) item.get("boardId");

                Intent intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("boardName", boardName);
                intent.putExtra("boardId", boardId);
                startActivity(intent);
            }
        });

        AdView AdView;
        AdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView.loadAd(adRequest);

        m_app = (TheGilApplication) getApplication();

        SetInfo setInfo = new SetInfo();

        isStoragePermissionGranted();
        if (!setInfo.GetUserInfo(MainActivity.this)) {
            m_app.m_strUserId = "";
            m_app.m_strUserPw = "";
        } else {
            m_app.m_strUserId = setInfo.m_userId;
            m_app.m_strUserPw = setInfo.m_userPw;
        }
        System.out.println("UserID = " + m_app.m_strUserId);

        if (!setInfo.CheckVersionInfo(MainActivity.this)) {
            AlertDialog.Builder notice = null;
            notice = new AlertDialog.Builder(MainActivity.this);
            notice.setTitle("버전 업데이트 알림");
            notice.setMessage("1.새글알림 기능이 추가되었습니다. 로그인설정에서 새글알림을 설정하시겠습니까?");
            notice.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                // 확인 버튼 클릭시 설정
                public void onClick(DialogInterface dialog, int whichButton) {
                    showLoginActivity();
                    dialog.dismiss();
                }
            });
            notice.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                // 확인 버튼 클릭시 설정
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
            notice.show();

        }
        setInfo.SaveVersionInfo(MainActivity.this);

        m_pd = ProgressDialog.show(this, "", "로딩중", true, false);

        Thread thread = new Thread(this);
        thread.start();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void run() {
        LoadData(MainActivity.this);
        handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (m_pd != null) {
                if (m_pd.isShowing()) {
                    m_pd.dismiss();
                }
            }
            displayData();
        }
    };

    public void displayData() {
        if (m_LoginStatus == -1) {
            AlertDialog.Builder ab = null;
            ab = new AlertDialog.Builder(MainActivity.this);
            ab.setMessage("로그인 정보가 설정되지 않았습니다. 설정 메뉴를 통해 로그인 정보를 설정하십시오.");
            ab.setPositiveButton(android.R.string.ok, null);
            ab.setTitle("로그인 오류");
            ab.show();
        } else if (m_LoginStatus == 0) {
            AlertDialog.Builder ab = null;
            ab = new AlertDialog.Builder(MainActivity.this);
            ab.setMessage("로그인을 실패했습니다.\n오류내용 : " + m_strErrorMsg + "\n설정 메뉴를 통해 로그인 정보를 변경하십시오.");
            ab.setPositiveButton(android.R.string.ok, null);
            ab.setTitle("로그인 오류");
            ab.show();
        } else {
            m_listView.setAdapter(new EfficientAdapter(MainActivity.this, m_arrayItems));
        }
    }

    private boolean LoadData(Context context) {

        // Login
        Login login = new Login();
        m_LoginStatus = login.LoginTo(context, m_app.m_httpRequest, m_app.m_strUserId, m_app.m_strUserPw);
        m_strErrorMsg = login.m_strErrorMsg;

        if (m_LoginStatus <= 0) {
            return false;
        }

        if (!getData()) {
            m_LoginStatus = 0;
            return false;
        }
        return true;
    }

    protected boolean getData() {

        String url = GlobalConst.MENU_SERVER + "/board-api-menu.do?comm=4";
        String result = m_app.m_httpRequest.requestPost(url, "", url);

        // 각 항목 찾기
        HashMap<String, Object> item;

        try {
            JSONObject newObject = new JSONObject(result);
            JSONArray arrayItem = newObject.getJSONArray("menu");
            for (int i = 0; i < arrayItem.length(); i++) {
                JSONObject jsonItem = arrayItem.getJSONObject(i);
                item = new HashMap<>();

                item.put("title", jsonItem.getString("title"));
                item.put("type", jsonItem.getString("type"));
                item.put("boardId", jsonItem.getString("boardId"));
                item.put("isNew", 0);

                m_arrayItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("after getData");

        if (m_arrayItems.size() <= 0) {
            return false;
        }

        return true;
    }

    public void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, SETUP_CODE);

        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, SETUP_CODE);
            return true;
        } else if (id == R.id.action_info) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SETUP_CODE:
                if (resultCode == RESULT_OK) {
                    m_arrayItems.clear();
                    m_pd = ProgressDialog.show(this, "", "로딩중입니다. 잠시만 기다리십시오...", true, false);

                    Thread thread = new Thread(this);
                    thread.start();
                }
        }
    }
}
