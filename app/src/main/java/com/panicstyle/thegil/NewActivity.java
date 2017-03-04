package com.panicstyle.thegil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

public class NewActivity extends AppCompatActivity implements Runnable {
	private ListView m_listView;
	private AdView m_adView;
    private ProgressDialog m_pd;
	private String m_strErrorMsg;
	protected String m_strBoardTitle;
	protected String m_strCommId;
	protected String m_strBoardId;
	protected String m_strBoardNo;
	protected String m_strBoardName;
    private List<HashMap<String, Object>> m_arrayItems;
    private int m_nPage;
    static final int REQUEST_WRITE = 1;
    static final int REQUEST_VIEW = 2;
    protected int m_LoginStatus;
	public static int m_nMode;
	private EfficientAdapter m_adapter;
	private TheGilApplication m_app;

	private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

    private static class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<HashMap<String, Object>> arrayItems;

        public EfficientAdapter(Context context, List<HashMap<String, Object>> data) {
            mInflater = LayoutInflater.from(context);
            arrayItems = data;
        }

        public int getCount() {
            return arrayItems.size() + 1;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == arrayItems.size()) {
            	MoreHolder holder;
                convertView = mInflater.inflate(R.layout.list_item_moreitem, null);

                holder = new MoreHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
	            holder.title.setText("더 보 기");

				return convertView;
            } else {
				ViewHolder holder;

				if (convertView != null) {
					Object a = convertView.getTag();
					if (!(a instanceof ViewHolder)) {
						convertView = null;
					}
				}
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.list_item_itemsview, null);

					holder = new ViewHolder();
					holder.name = (TextView) convertView.findViewById(R.id.name);
					holder.subject = (TextView) convertView.findViewById(R.id.subject);
					holder.comment = (TextView) convertView.findViewById(R.id.comment);
					holder.iconnew = (ImageView) convertView.findViewById(R.id.iconnew);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				HashMap<String, Object> item;;
				item = arrayItems.get(position);
				String board = (String) item.get("group") + item.get("board");
				String date = (String) item.get("date");
				String name = (String) item.get("name");
				String subject = (String) item.get("subject");
				String comment = (String) item.get("comment");
				int isNew = (Integer) item.get("isNew");
				// Bind the data efficiently with the holder.
				name = board + "&nbsp; <b>" + name + "</b>&nbsp; " + date;
				holder.name.setText(Html.fromHtml(name));
				holder.subject.setText(subject);
				holder.comment.setText(comment);
				if (isNew == 1) {
					holder.iconnew.setImageResource(R.drawable.circle);
				} else {
					holder.iconnew.setImageResource(0);
				}
				if (comment.length() > 0) {
					holder.comment.setBackgroundResource(R.drawable.layout_circle);
				} else {
					holder.comment.setBackgroundResource(0);
				}

				return convertView;
			}
        }

		static class ViewHolder {
			TextView name;
			TextView subject;
			TextView comment;
			ImageView iconnew;
		}

		static class MoreHolder {
            TextView title;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
		m_listView = (ListView) findViewById(R.id.listView);
		m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == m_arrayItems.size()) {
					m_nPage++;
					m_pd = ProgressDialog.show(NewActivity.this, "", "로딩중입니다. 잠시만 기다리십시오...", true, false);

					Thread thread = new Thread(NewActivity.this);
					thread.start();
				} else {
					HashMap<String, Object> item;
					item = m_arrayItems.get(position);
					Intent intent = new Intent(NewActivity.this, ArticleViewActivity.class);

					intent.putExtra("boardId", (String) item.get("boardId"));
					intent.putExtra("boardNo", (String) item.get("boardNo"));
					startActivityForResult(intent, REQUEST_VIEW);
				}
			}
		});

		AdView m_adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		m_adView.loadAd(adRequest);

		m_app = (TheGilApplication)getApplication();

        intenter();

		setTitle(m_strBoardName);

        m_nPage = 1;
        m_arrayItems = new ArrayList<>();

        LoadingData();
    }

    public void LoadingData() {
        m_pd = ProgressDialog.show(this, "", "로딩중", true,
				false);

        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
    	if (!getData()) {
            // Login
			Login login = new Login();
			m_LoginStatus = login.LoginTo(NewActivity.this, m_app.m_httpRequest, m_app.m_strUserId, m_app.m_strUserPw);
			m_strErrorMsg = login.m_strErrorMsg;

    		if (m_LoginStatus > 0) {
    			if (getData()) {
    				m_LoginStatus = 1;
    			}
    		}
    	} else {
			m_LoginStatus = 1;
    	}
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
    		displayData();
    	}
    };

    public void displayData() {
		if (m_LoginStatus == -1) {
			AlertDialog.Builder ab = null;
			ab = new AlertDialog.Builder( NewActivity.this );
			ab.setMessage( "로그인 정보가 설정되지 않았습니다. 설정 메뉴를 통해 로그인 정보를 설정하십시오.");
			ab.setPositiveButton(android.R.string.ok, null);
			ab.setTitle( "로그인 오류" );
			ab.show();
		} else if (m_LoginStatus == 0){
			AlertDialog.Builder ab = null;
			ab = new AlertDialog.Builder( NewActivity.this );
			ab.setMessage( "로그인을 실패했습니다 설정 메뉴를 통해 로그인 정보를 변경하십시오.");
			ab.setPositiveButton(android.R.string.ok, null);
			ab.setTitle( "로그인 오류" );
			ab.show();
		} else {
    		if (m_nPage == 1) {
    			m_adapter = new EfficientAdapter(NewActivity.this, m_arrayItems);
    			m_listView.setAdapter(m_adapter);
    		} else {
        		m_adapter.notifyDataSetChanged();
    		}
		}
    }

    public void intenter() {
//    	Intent intent = getIntent();  // 값을 가져오는 인텐트 객체생성
		Bundle extras = getIntent().getExtras();
		// 가져온 값을 set해주는 부분

		m_strBoardId = extras.getString("boardId");
		m_strBoardName = extras.getString("boardName");
	}

    protected boolean getData() {
		String Page = Integer.toString(m_nPage);
		String url = GlobalConst.WWW_SERVER + "/2014/bbs/new.php?gr_id=&view=&mb_id=&page=" + Page;
        String result = m_app.m_httpRequest.requestGet(url, "");

        if (result.length() < 200) {
        	return false;
        }

        // 각 항목 찾기
		String tbody = Utils.getMatcherFirstString("(<tbody>)(.|\\n)*?(</tbody>)", result);

        HashMap<String, Object> item;

		Matcher m = Utils.getMatcher("(<tr)(.|\\n)*?(</tr>)", tbody);
        while (m.find()) { // Find each match in turn; String can't do this.
            item = new HashMap<String, Object>();
            String matchstr = m.group(0);

           	item.put("isNotice", 0);

            // subject
	        String strSubject;
			strSubject = Utils.getMatcherFirstString("(<td><a href)(.|\\n)*?(</td>)", matchstr);
			strSubject = Utils.removeSpan(strSubject);
			strSubject = Utils.repalceHtmlSymbol(strSubject);
            item.put("subject", strSubject);

			// boardId
			String strBoardId = Utils.getMatcherFirstString("(?<=bo_table=)(.|\\n)*?(?=\\\")", matchstr);
			item.put("boardId", strBoardId);

			// boardNo
			String strBoardNo = Utils.getMatcherFirstString("(?<=wr_id=)(.|\\n)*?(?=\\\")", matchstr);
			item.put("boardNo", strBoardNo);

			// comment
			String strComment = Utils.getMatcherFirstString("(?<=<span class=\\\"cnt_cmt\\\">)(.|\\n)*?(?=</span>)", matchstr);
            item.put("comment", strComment);

           	item.put("isNew", 0);

           	item.put("isReply", 0);

			// name
			String strName = Utils.getMatcherFirstString("(<a href=\\\"http://thegil.org/2014/bbs/profile.php)(.|\\n)*?(</a>)", matchstr);
			strName = Utils.repalceHtmlSymbol(strName);
			item.put("name", strName);

	        // date
			String strDate = Utils.getMatcherFirstString("(?<=<td class=\\\"td_date\\\">)(.|\\n)*?(?=</td>)", matchstr);
            item.put("date", strDate);

			// 그룹명
			String strGroup = Utils.getMatcherFirstString("(<td class=\\\"td_group)(.|\\n)*?(</td>)", matchstr);
			strGroup = Utils.repalceHtmlSymbol(strGroup);
			item.put("group", strGroup);

			// 보드명
			String strBoard = Utils.getMatcherFirstString("(<td class=\\\"td_board)(.|\\n)*?(</td>)", matchstr);
			strBoard = Utils.repalceHtmlSymbol(strBoard);
			item.put("board", strBoard);

			m_arrayItems.add( item );
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	switch(requestCode) {
    		case REQUEST_WRITE:
			case REQUEST_VIEW:
				if (resultCode == RESULT_OK) {
					m_arrayItems.clear();
					m_adapter.notifyDataSetChanged();
					m_nPage = 1;

					m_pd = ProgressDialog.show(this, "", "로딩중", true,
							false);

					Thread thread = new Thread(this);
					thread.start();
				}
				break;
			default:
				break;

    	}
    }
}