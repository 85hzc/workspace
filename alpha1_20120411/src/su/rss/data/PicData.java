package su.rss.data;

public class PicData {
	String imagePath;  //ͼƬ·��
	String imageLink;  //ͼƬ����
	String ID;  //ͼƬID��ʹ��uuid 
	String relateID;  //ͼƬ����ID��Դ��Դ��Ŀ��ID
	int mWidth;  //ͼƬ���
	int mHeight;  //ͼƬ�߶�
	
	public void setImagePath(String path)  //����ͼƬ·��
	{
		this.imagePath = path;
	}
	
	public String getImagePath()  //��ȡͼƬ·��
	{
		return this.imagePath;
	}
	
	public void setID(String id)  //����ͼƬID
	{
		this.ID = id;
	}
	
	public String getID()  //��ȡͼƬID
	{
		return this.ID;
	}
	
	public void setRelateID(String id)  //����ͼƬ�������ID
	{
		this.relateID = id;
	}
	
	public String getRelateID()  //��ȡͼƬ�������ID
	{
		return this.relateID;
	}
	
	public void setWidth(int w)  //����ͼƬ���
	{
		this.mWidth = w;
	}
	
	public int getWidth()  //��ȡͼƬ���
	{
		return this.mWidth;
	}
	
	public void setHeight(int h)  //����ͼƬ�߶�
	{
		this.mHeight = h;
	}
	
	public int getHeight()  //��ȡͼƬ�߶�
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
