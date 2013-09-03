package su.rss.data;

public class PicData {
	String imagePath;  //图片路径
	String imageLink;  //图片链接
	String ID;  //图片ID，使用uuid 
	String relateID;  //图片所属ID：源或源栏目的ID
	int mWidth;  //图片宽度
	int mHeight;  //图片高度
	
	public void setImagePath(String path)  //设置图片路径
	{
		this.imagePath = path;
	}
	
	public String getImagePath()  //获取图片路径
	{
		return this.imagePath;
	}
	
	public void setID(String id)  //设置图片ID
	{
		this.ID = id;
	}
	
	public String getID()  //获取图片ID
	{
		return this.ID;
	}
	
	public void setRelateID(String id)  //设置图片相关联的ID
	{
		this.relateID = id;
	}
	
	public String getRelateID()  //获取图片相关联的ID
	{
		return this.relateID;
	}
	
	public void setWidth(int w)  //设置图片宽度
	{
		this.mWidth = w;
	}
	
	public int getWidth()  //获取图片宽度
	{
		return this.mWidth;
	}
	
	public void setHeight(int h)  //设置图片高度
	{
		this.mHeight = h;
	}
	
	public int getHeight()  //获取图片高度
	{
		return this.mHeight;
	}

	public void setImageLink(String url) {
		this.imageLink = url;
	}
	
	public String getImageLink() {
		return this.imageLink;
	}
}
