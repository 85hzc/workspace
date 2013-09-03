package su.rss.data;

import java.io.Serializable;

public class RSSFeed implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int PROPERTY_ORG = 1;  //固化
	public final static int PROPERTY_PUSH = 2;  //服务器推送
	public final static int PROPERTY_CUSTOM = 3;  //用户自定义
	
	String mTitle;  //源名称
    String pubDate;  //源建立时间
    String imagaPath;  //图片网络地址
    String mLink;  //源链接地址
    int mProperty;  //源属性：固化、添加、自定义
	String UID;  //源ID
	int mPosition;  //源顺序
	String userID;  //用户ID，默认IMSI
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.mTitle = title;
	}
	
	public String getTitle()  //获取名称
	{
		return this.mTitle;
	}
	
	public void setDate(String date)  //设置时间
	{
		this.pubDate = date;
	}
	
	public String getDate()  //获取时间
	{
		return this.pubDate;
	}

	public void setImagePath(String link)  //设置图片链接路径
	{
		this.imagaPath = link;
	}
	
	public String getImagePath()  //获取图片链接路径
	{
		return this.imagaPath;
	}
	public void setUID(String id)  //设置源ID
	{
		this.UID = id;
	}
	public String getUID()  //获取源ID
	{
		return this.UID;
	}
	public void setLink(String link)  //设置源地址
	{
		this.mLink = link;
	}
	public String getLink()  //获取源地址
	{
		return this.mLink;
	}
	public void setProperty(int property)  //设置源属性
	{
		this.mProperty = property;
	}
	public int getProperty()  //获取源属性
	{
		return this.mProperty;
	}
	public void setPosition(int pos)  //设置源位置
	{
		this.mPosition = pos;
	}
	public int getPosition()  //获取源位置
	{
		return this.mPosition;
	}
	public void setUserID(String id)  //设置用户ID
	{
		this.userID = id;
	}
	public String getUserID()  //获取用户ID
	{
		return this.userID;
	}
}
