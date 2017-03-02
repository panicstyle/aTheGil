package com.panicstyle.thegil;

import android.content.Context;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class Login {
	public String m_strErrorMsg = "";

	public int LoginTo(Context context, HttpRequest httpRequest, String strUserId, String strUserPw) {

		String referer = GlobalConst.WWW_SERVER + "/2014/bbs/login.php";
		String url = GlobalConst.WWW_SERVER + "/2014/bbs/login_check.php";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("url", "http%3A%2F%2Fthegil.org%2F2014"));
		nameValuePairs.add(new BasicNameValuePair("mb_id", strUserId));
		nameValuePairs.add(new BasicNameValuePair("mb_password", strUserPw));

		// Login 호출후 302 리턴됨. /front 를 다시 호출해야지 로그인 결과를 알 수 있음.
		String result = httpRequest.requestPost(url, nameValuePairs, referer);
		System.out.println("Login Result : " + result);

		if (result.indexOf("<title>오류안내 페이지") > 0) {
			String errMsg = "Login Fail";
	    	System.out.println(errMsg);
			// link
			m_strErrorMsg = Utils.getMatcherFirstString("(?<=alert\\(\\\")(.|\\n)*?(?=\\\"\\))", result);
	        return 0;
		}
    	System.out.println("Login Success");

		return 1;
	}
}
