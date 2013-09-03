package su.rss.data;

import java.io.Serializable;

public class RSSItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String mTitle; // Item����
	String mDescription; // item ����
	String mLink; // item��ϸ��Ϣ����
	String pubDate; // item����ʱ��
	String mSource; // item��Դ
	boolean isHaveImage; // item�Ƿ����ͼƬ
	String UID; // item id
	boolean isRead; // �Ƿ��Ķ�
	String feedID; // ����ԴID
	String mImgPath; //ͼƬ����

	public void setTitle(String title) // ���ñ���
	{
		this.mTitle = title;
	}

	public String getTitle() // ��ȡ����
	{
		return this.mTitle;
	}

	public void setDescription(String des) // ��������
	{
		this.mDescription = des;
	}

	public String getDescription() // ��ȡ����
	{
		return this.mDescription;
	}

	public void setLink(String link) // ��������
	{
		this.mLink = link;
	}

	public String getLink() // ��ȡ����
	{
		return this.mLink;
	}

	public void setDate(String date) // ���ý���ʱ��
	{
		this.pubDate = date;
	}

	public String getDate() // ��ȡ����ʱ��
	{
		return this.pubDate;
	}

	public void setHaveImage(Boolean have) // �����Ƿ���ͼƬ
	{
		this.isHaveImage = have;
	}

	public Boolean isHaveImage() // �Ƿ���ͼƬ
	{
		return this.isHaveImage;
	}

	public void setUID(String id) // ����ID
	{
		this.UID = id;
	}

	public String getUID() // ��ȡID
	{
		return this.UID;
	}

	public void setReaded(Boolean read) // �����Ƿ����Ķ�
	{
		this.isRead = read;
	}

	public Boolean isRead() // �Ƿ����Ķ�
	{
		return this.isRead;
	}

	public void setFeedID(String id) // ��������ԴID
	{
		this.feedID = id;
	}

	public String getFeedID() // ��ȡ����ԴID
	{
		return this.feedID;
	}
	
	public void setSource(String source) {
		mSource = source;
	}
	
	public String getSource() {
		return mSource;
	}
	
	public void setImgPath(String url) {
		mImgPath = url;
	}
	
	public String getImgPath() {
		return mImgPath;
	}
}
