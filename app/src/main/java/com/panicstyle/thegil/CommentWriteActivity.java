package com.panicstyle.thegil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.panicstyle.thegil.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class CommentWriteActivity extends AppCompatActivity implements Runnable {
    private ProgressDialog m_pd;
    private int m_nMode;
    private String m_strBoardId;
    private String m_strBoardNo;
    private String m_strCommentNo;
    private boolean m_bSaveStatus;
    private String m_ErrorMsg;
    private String m_strComment;
    private TheGilApplication m_app;
    private String m_strWmode;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_write);

        m_app = (TheGilApplication)getApplication();

        intenter();

        if (m_nMode == GlobalConst.WRITE) {
            setTitle("댓글쓰기");
            m_strWmode = "c";
            m_strCommentNo = "";
        } else if (m_nMode == GlobalConst.MODIFY) {
            setTitle("댓글수정");
            m_strWmode = "cu";
            m_strComment = Utils.repalceHtmlSymbol(m_strComment);
            EditText tContent = (EditText) findViewById(R.id.editContent);
            tContent.setText(m_strComment);
        } else {
            setTitle("답변댓글쓰기");
            m_strWmode = "c";
        }
    }

    public void intenter() {
//    	Intent intent = getIntent();  // 값을 가져오는 인텐트 객체생성
    	Bundle extras = getIntent().getExtras();
    	// 가져온 값을 set해주는 부분
        m_nMode = extras.getInt("mode");
    	m_strBoardId = extras.getString("boardId");
    	m_strBoardNo = extras.getString("boardNo");
    	m_strCommentNo = extras.getString("commentNo");
        m_strComment = extras.getString("content");

    }
	
    public void SaveData() {
    	
    	EditText textContent = (EditText)findViewById(R.id.editContent);

        m_strComment = textContent.getText().toString();
    	
    	if (m_strComment.length() <= 0) {
    		AlertDialog.Builder ab = null;
			ab = new AlertDialog.Builder( this );
			ab.setMessage( "입력된 내용이 없습니다. 종료하시겠습니까?");
			ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					finish();
				}
			});
			ab.setNegativeButton(android.R.string.cancel, null);
			ab.setTitle( "확인" );
			ab.show();
			return;
    	}

        if (getParent() == null) {
            setResult(Activity.RESULT_OK, new Intent());
        } else {
            getParent().setResult(Activity.RESULT_OK, new Intent());
        }

        m_pd = ProgressDialog.show(this, "", "저장중", true, false);

        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        PostData();
    	handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
            if(m_pd != null){
                if(m_pd.isShowing()){
                    m_pd.dismiss();
                }
            }
    		if (!m_bSaveStatus) {
	    		AlertDialog.Builder ab = null;
				ab = new AlertDialog.Builder( CommentWriteActivity.this );
				String strErrorMsg = "댓글 저장중 오류가 발생했습니다. \n" + m_ErrorMsg;
				ab.setMessage(strErrorMsg);
				ab.setPositiveButton(android.R.string.ok, null);
				ab.setTitle( "확인" );
				ab.show();
				return;
    		}
    		finish();
    	}
    };        
    	
    protected boolean PostData() {
        String m_strSCA = "";
        if (m_strBoardId == "B13") {
            m_strSCA = "문서자료";
        }

		String url = GlobalConst.WWW_SERVER + "/2014/bbs/write_comment_update.php";
        String strParam = "number=" + m_strBoardNo + "&content=" + m_strComment;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("w", m_strWmode));
        nameValuePairs.add(new BasicNameValuePair("bo_table", m_strBoardId));
        nameValuePairs.add(new BasicNameValuePair("wr_id", m_strBoardNo));
        nameValuePairs.add(new BasicNameValuePair("comment_id", m_strCommentNo));
        nameValuePairs.add(new BasicNameValuePair("sca", m_strSCA));
        nameValuePairs.add(new BasicNameValuePair("sfl", ""));
        nameValuePairs.add(new BasicNameValuePair("stx", ""));
        nameValuePairs.add(new BasicNameValuePair("spt", ""));
        nameValuePairs.add(new BasicNameValuePair("page", ""));
        nameValuePairs.add(new BasicNameValuePair("is_good", "0"));
        nameValuePairs.add(new BasicNameValuePair("wr_content", m_strComment));
		String result = m_app.m_httpRequest.requestPost(url, nameValuePairs, url);

        if (result.contains("<title>오류안내 페이지")) {
            m_ErrorMsg = Utils.getMatcherFirstString("(<p class=\\\"cbg\\\">).*?(</p>)", result);
            m_ErrorMsg = Utils.repalceHtmlSymbol(m_ErrorMsg);
            return false;
        }
        
		m_bSaveStatus = true;
    	return true;
    }

    public void CancelData() {
        if (getParent() == null) {
            setResult(Activity.RESULT_CANCELED, new Intent());
        } else {
            getParent().setResult(Activity.RESULT_CANCELED, new Intent());
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_article_write, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cancel:
                CancelData();
                return true;
            case R.id.menu_save:
                SaveData();
                return true;
        }
        return true;
    }
}
