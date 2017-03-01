package com.panicstyle.thegil;


import android.content.Context;

import org.json.JSONObject;

public class Login {
	public String m_strErrorMsg = "";

	public int LoginTo(Context context, HttpRequest httpRequest, String strUserId, String strUserPw) {

		String referer = "http://www.gongdong.or.kr";
		String url = "http://www.gongdong.or.kr/index.php";
		String logoutURL = "http://www.gongdong.or.kr/index.php?mid=front&act=dispMemberLogout";

		String strLoginParam = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
				"<methodCall>\n" +
				"<params>\n" +
				"<_filter><![CDATA[login]]></_filter>\n" +
				"<error_return_url><![CDATA[/]]></error_return_url>\n" +
				"<mid><![CDATA[front]]></mid>\n" +
				"<act><![CDATA[procMemberLogin]]></act>\n" +
				"<user_id><![CDATA[" + strUserId + "]]></user_id>\n" +
				"<password><![CDATA[" + strUserPw +"]]></password>\n" +
				"<module><![CDATA[member]]></module>\n" +
				"</params>\n" +
				"</methodCall>";

		System.out.println("Login Param : " + strLoginParam);

		// Logout
//		httpRequest.requestGet(httpClient, httpContext, logoutURL, referer, "utf-8");
		// Login 호출후 302 리턴됨. /front 를 다시 호출해야지 로그인 결과를 알 수 있음.
		String result = httpRequest.requestPost(url, strLoginParam, referer, "utf-8");
		System.out.println("Login Result : " + result);

		if (result.indexOf("<error>0</error>") <= 0) {
			String errMsg = "Login Fail";
	    	System.out.println(errMsg);
			// link
			m_strErrorMsg = Utils.getMatcherFirstString("(?<=<message>)(.|\\n)*?(?=</message>)", result);
	        return 0;
		}
    	System.out.println("Login Success");

		return 1;
	}
}
