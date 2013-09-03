package su.rss.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	public static final String DETAILPAGE_DIR = "/sdcard/su/rss/detail_page/";  //详细内容文件存储路径
	public static final String PIC_DIR = "/sdcard/su/rss/pic/";  //图片存储路径
	public static final String CATEGORY_DIR = "/sdcard/su/rss/cate/";  //分类列表保存路径
	
	/**
	 * 转换文件所占大小单位，小于1M以KB返回 ,大于1M 以M为单位返回
	 * 保留了两位小数
	 */
	public static String formatFileSize(long size)  
	{
		String str= new String();
    	float f;
    	//小于1M 以KB返回
    	if(size < 1024 * 1024) {
    		f = (float)((float)size /(float)1024);
     	   str = String.format("%.2fKB", f);
    	}//大于1M 以M为单位返回
    	else{
    		f = (float) ((float) size / (float) (1024 * 1024));
    		str = String.format("%.2fMB", f);
    	   }    	
    	
    	return str;
	}
	
	/**
	 * 清除缓存
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
	 * 删除文件
	 */
	public static void deleteFile(String fileName) 
	{
		
			File deletedFile = new File(fileName);
			if(deletedFile.exists())
				deletedFile.delete();		
	}
	
	/**
	 * 保存content到文件filename中，目录和文件可以不存在，会自动创建
	 * filename为全路径
	 * append：true 追加 flase 覆盖
	 */
	public static void write(String filename, String content, boolean append) throws Exception //写文件
	{
		//如果文件或者目录不存在则自动创建
		File targetFile = new File(filename);	
		int index = filename.lastIndexOf('/');//找到文件所在目录
		File targetDir = new File(filename.substring(0, index));
		if(!targetDir.exists())//判断目录是否存在，不存在则先创建
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
	 * 通过流保存到文件filename中，目录和文件可以不存在，会自动创建，
	 * filename为全路径 如：/sdcard/su/rss/1.txt
	 * iStream：输入流，且由调用者关闭
	 * append：true 追加 flase 覆盖
	 * @throws IOException 
	 */
	public static void write(String filename, InputStream iStream, boolean append) throws IOException//写文件
	{
		//如果文件或者目录则自动创建
		File targetFile = new File(filename);	
		int index = filename.lastIndexOf('/');//找到文件所在目录
		File targetDir = new File(filename.substring(0, index));
		if(!targetDir.exists())//判断目录是否存在，不存在则先创建
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
	 * 指定文件名读出数据，以String返回
	 * filename为全路径 如：/sdcard/su/rss/1.txt
	 * iStream：输入流，且由调用者关闭
	 * append：true 追加 flase 覆盖
	 */
	public static String read(String filename) throws Exception  //读文件
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
		byte[] data = outStream.toByteArray();//得到文件的二进制数据
		outStream.close();
		inStream.close();
		return new String(data);
	}
		
	/**
     * 创建文件夹，可以一次创建多级目录。
     * 
     */
	public static void createDir(String path)  //throws Exception//创建文件夹
	{
		
			File myPath = new File(path);
			if(!myPath.exists()){
				if(!myPath.mkdirs()){
					Logger.w("FileUtil","createDir "+path+" error!");
					}
			}
	}
		
	/**
     * 删除文件夹下所有文件，包括目录
     * 
     */
	public static void deleteDir(String path)  //删除文件夹
	{

            delAllFile(path); //删除完里面所有内容
            String filePath = path;
            filePath = filePath.toString();
            java.io.File myFilePath = new File(filePath);
            if(myFilePath.exists()){
            	if(!myFilePath.delete()) //删除空文件夹
            	{
            		Logger.e("FileUtil","deleteDir "+ path+" error!");
            	}
            }
	}
		
	 /**
     * 删除文件夹里面的所有文件
     * @param path String 文件夹路径 如 c:/fqf
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
                            delAllFile(path+"/"+ tempList[i]);//先删除文件夹里面的文件
                            deleteDir(path+"/"+ tempList[i]);//再删除空文件夹
                    }
            }
    } 
    
    public static boolean exists(String filename) {
    	File file = new File(filename);
    	return file.exists();
    }

}
