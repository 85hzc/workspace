package su.rss.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	public static final String DETAILPAGE_DIR = "/sdcard/su/rss/detail_page/";  //��ϸ�����ļ��洢·��
	public static final String PIC_DIR = "/sdcard/su/rss/pic/";  //ͼƬ�洢·��
	public static final String CATEGORY_DIR = "/sdcard/su/rss/cate/";  //�����б���·��
	
	/**
	 * ת���ļ���ռ��С��λ��С��1M��KB���� ,����1M ��MΪ��λ����
	 * ��������λС��
	 */
	public static String formatFileSize(long size)  
	{
		String str= new String();
    	float f;
    	//С��1M ��KB����
    	if(size < 1024 * 1024) {
    		f = (float)((float)size /(float)1024);
     	   str = String.format("%.2fKB", f);
    	}//����1M ��MΪ��λ����
    	else{
    		f = (float) ((float) size / (float) (1024 * 1024));
    		str = String.format("%.2fMB", f);
    	   }    	
    	
    	return str;
	}
	
	/**
	 * �������
	 */
	public static void clearCache()  
	{
		File detailPath = new File(DETAILPAGE_DIR);
    	File picPath = new File(PIC_DIR);
    	if(detailPath.exists())
    	deleteDir(DETAILPAGE_DIR);
    	if(picPath.exists())
    	deleteDir(PIC_DIR);
	}
	
	/**
	 * ɾ���ļ�
	 */
	public static void deleteFile(String fileName) 
	{
		
			File deletedFile = new File(fileName);
			if(deletedFile.exists())
				deletedFile.delete();		
	}
	
	/**
	 * ����content���ļ�filename�У�Ŀ¼���ļ����Բ����ڣ����Զ�����
	 * filenameΪȫ·��
	 * append��true ׷�� flase ����
	 */
	public static void write(String filename, String content, boolean append) throws Exception //д�ļ�
	{
		//����ļ�����Ŀ¼���������Զ�����
		File targetFile = new File(filename);	
		int index = filename.lastIndexOf('/');//�ҵ��ļ�����Ŀ¼
		File targetDir = new File(filename.substring(0, index));
		if(!targetDir.exists())//�ж�Ŀ¼�Ƿ���ڣ����������ȴ���
		{
			targetDir.mkdirs();
		}
		if(!targetFile.exists())
		{
			targetFile.createNewFile();
		}

		FileWriter fw=new FileWriter(filename,append);
		fw.write(content);
		fw.close();
	}
		
	/**
	 * ͨ�������浽�ļ�filename�У�Ŀ¼���ļ����Բ����ڣ����Զ�������
	 * filenameΪȫ·�� �磺/sdcard/su/rss/1.txt
	 * iStream�������������ɵ����߹ر�
	 * append��true ׷�� flase ����
	 * @throws IOException 
	 */
	public static void write(String filename, InputStream iStream, boolean append) throws IOException//д�ļ�
	{
		//����ļ�����Ŀ¼���Զ�����
		File targetFile = new File(filename);	
		int index = filename.lastIndexOf('/');//�ҵ��ļ�����Ŀ¼
		File targetDir = new File(filename.substring(0, index));
		if(!targetDir.exists())//�ж�Ŀ¼�Ƿ���ڣ����������ȴ���
		{
			targetDir.mkdirs();
		}
		if(!targetFile.exists())
		{
			targetFile.createNewFile();
		}
		
		FileOutputStream outStream = new FileOutputStream(targetFile,append);
		byte[] buffer = new byte[1024];
		int len = 0;		
		while( (len = iStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);
		}
		outStream.close();
	}
		
	/**
	 * ָ���ļ����������ݣ���String����
	 * filenameΪȫ·�� �磺/sdcard/su/rss/1.txt
	 * iStream�������������ɵ����߹ر�
	 * append��true ׷�� flase ����
	 */
	public static String read(String filename) throws Exception  //���ļ�
	{
		File file = new File(filename);
		if(!file.exists())
		{
			Logger.w("pwb", "File "+filename+" not exists");
			return "";
		}
		FileInputStream inStream =new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while( (len = inStream.read(buffer))!= -1){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();//�õ��ļ��Ķ���������
		outStream.close();
		inStream.close();
		return new String(data);
	}
		
	/**
     * �����ļ��У�����һ�δ����༶Ŀ¼��
     * 
     */
	public static void createDir(String path)  //throws Exception//�����ļ���
	{
		
			File myPath = new File(path);
			if(!myPath.exists()){
				if(!myPath.mkdirs()){
					Logger.w("FileUtil","createDir "+path+" error!");
					}
			}
	}
		
	/**
     * ɾ���ļ����������ļ�������Ŀ¼
     * 
     */
	public static void deleteDir(String path)  //ɾ���ļ���
	{

            delAllFile(path); //ɾ����������������
            String filePath = path;
            filePath = filePath.toString();
            java.io.File myFilePath = new File(filePath);
            if(myFilePath.exists()){
            	if(!myFilePath.delete()) //ɾ�����ļ���
            	{
            		Logger.e("FileUtil","deleteDir "+ path+" error!");
            	}
            }
	}
		
	 /**
     * ɾ���ļ�������������ļ�
     * @param path String �ļ���·�� �� c:/fqf
     */
    public static void delAllFile(String path) {
            File file = new File(path);
            if (!file.exists()) {
                    return;
            }
            if (!file.isDirectory()) {
           return;
            }
            String[] tempList = file.list();
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                    if (path.endsWith(File.separator)) {
                            temp = new File(path + tempList[i]);
                    }
                    else {
                            temp = new File(path + File.separator + tempList[i]);
                    }
                    if (temp.isFile()) {
                            temp.delete();
                    }
                    if (temp.isDirectory()) {
                            delAllFile(path+"/"+ tempList[i]);//��ɾ���ļ���������ļ�
                            deleteDir(path+"/"+ tempList[i]);//��ɾ�����ļ���
                    }
            }
    } 
    
    public static boolean exists(String filename) {
    	File file = new File(filename);
    	return file.exists();
    }

}
