package su.rss.data;

import java.util.ArrayList;

public class User {
	String userID;  //�û�ID��Ĭ��IMSI
	String mPassword;  //��¼����
	ArrayList<RSSFeed>  rssList;  //���û�����Դ�б�
	
	public void setUserID(String id)  //�����û�ID
	{
		this.userID = id;
	}
	public String getUserID()  //��ȡ�û�ID
	{
		return this.userID;
	}
	public void setPassword(String password)  //���õ�¼����
	{
		this.mPassword = password;
	}
	public String getPassword()  //��ȡ��¼����
	{
		return this.mPassword;
	}
	public void setRSSFeedList(ArrayList list)  //����Դ�б�
	{
		this.rssList = list;
	}
	public ArrayList<RSSFeed> getRSSFeedList()  //��ȡԴ�б�
	{
		return this.rssList;
	}

}
