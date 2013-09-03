package su.rss.data;

import java.io.Serializable;

public class RSSItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String mTitle; // Item名称
	String mDescription; // item 描述
	String mLink; // item详细信息链接
	String pubDate; // item建立时间
	String mSource; // item来源
	boolean isHaveImage; // item是否包含图片
	String UID; // item id
	boolean isRead; // 是否阅读
	String feedID; // 所属源ID
	String mImgPath; //图片链接

	public void setTitle(String title) // 设置标题
	{
		this.mTitle = title;
	}

	public String getTitle() // 获取标题
	{
		return this.mTitle;
	}

	public void setDescription(String des) // 设置描述
	{
		this.mDescription = des;
	}

	public String getDescription() // 获取描述
	{
		return this.mDescription;
	}

	public void setLink(String link) // 设置链接
	{
		this.mLink = link;
	}

	public String getLink() // 获取链接
	{
		return this.mLink;
	}

	public void setDate(String date) // 设置建立时间
	{
		this.pubDate = date;
	}

	public String getDate() // 获取建立时间
	{
		return this.pubDate;
	}

	public void setHaveImage(Boolean have) // 设置是否有图片
	{
		this.isHaveImage = have;
	}

	public Boolean isHaveImage() // 是否有图片
	{
		return this.isHaveImage;
	}

	public void setUID(String id) // 设置ID
	{
		this.UID = id;
	}

	public String getUID() // 获取ID
	{
		return this.UID;
	}

	public void setReaded(Boolean read) // 设置是否已阅读
	{
		this.isRead = read;
	}

	public Boolean isRead() // 是否已阅读
	{
		return this.isRead;
	}

	public void setFeedID(String id) // 设置所属源ID
	{
		this.feedID = id;
	}

	public String getFeedID() // 获取所属源ID
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
