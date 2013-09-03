package su.rss.data;

import java.util.ArrayList;

public class User {
	String userID;  //用户ID，默认IMSI
	String mPassword;  //登录密码
	ArrayList<RSSFeed>  rssList;  //该用户所有源列表
	
	public void setUserID(String id)  //设置用户ID
	{
		this.userID = id;
	}
	public String getUserID()  //获取用户ID
	{
		return this.userID;
	}
	public void setPassword(String password)  //设置登录密码
	{
		this.mPassword = password;
	}
	public String getPassword()  //获取登录密码
	{
		return this.mPassword;
	}
	public void setRSSFeedList(ArrayList list)  //设置源列表
	{
		this.rssList = list;
	}
	public ArrayList<RSSFeed> getRSSFeedList()  //获取源列表
	{
		return this.rssList;
	}

}
