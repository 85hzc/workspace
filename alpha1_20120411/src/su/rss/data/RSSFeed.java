package su.rss.data;

import java.io.Serializable;

public class RSSFeed implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int PROPERTY_ORG = 1;  //�̻�
	public final static int PROPERTY_PUSH = 2;  //����������
	public final static int PROPERTY_CUSTOM = 3;  //�û��Զ���
	
	String mTitle;  //Դ����
    String pubDate;  //Դ����ʱ��
    String imagaPath;  //ͼƬ�����ַ
    String mLink;  //Դ���ӵ�ַ
    int mProperty;  //Դ���ԣ��̻�����ӡ��Զ���
	String UID;  //ԴID
	int mPosition;  //Դ˳��
	String userID;  //�û�ID��Ĭ��IMSI
	
	/**
	 * ���ñ���
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.mTitle = title;
	}
	
	public String getTitle()  //��ȡ����
	{
		return this.mTitle;
	}
	
	public void setDate(String date)  //����ʱ��
	{
		this.pubDate = date;
	}
	
	public String getDate()  //��ȡʱ��
	{
		return this.pubDate;
	}

	public void setImagePath(String link)  //����ͼƬ����·��
	{
		this.imagaPath = link;
	}
	
	public String getImagePath()  //��ȡͼƬ����·��
	{
		return this.imagaPath;
	}
	public void setUID(String id)  //����ԴID
	{
		this.UID = id;
	}
	public String getUID()  //��ȡԴID
	{
		return this.UID;
	}
	public void setLink(String link)  //����Դ��ַ
	{
		this.mLink = link;
	}
	public String getLink()  //��ȡԴ��ַ
	{
		return this.mLink;
	}
	public void setProperty(int property)  //����Դ����
	{
		this.mProperty = property;
	}
	public int getProperty()  //��ȡԴ����
	{
		return this.mProperty;
	}
	public void setPosition(int pos)  //����Դλ��
	{
		this.mPosition = pos;
	}
	public int getPosition()  //��ȡԴλ��
	{
		return this.mPosition;
	}
	public void setUserID(String id)  //�����û�ID
	{
		this.userID = id;
	}
	public String getUserID()  //��ȡ�û�ID
	{
		return this.userID;
	}
}
