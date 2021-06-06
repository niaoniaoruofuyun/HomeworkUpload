package UploadServlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

class WebDav implements Runnable{  
	private String fileName;
	private byte[] data;
	private HttpServletRequest request;
	private HttpServletResponse response;
	// WebDav初始化
	public WebDav(String fileName, byte[] data,HttpServletRequest request,HttpServletResponse response) {
    	this.fileName = fileName;
    	this.data = data;
    	this.request = request;
    	this.response = response;
	}

	// 在类中覆盖Runnable接口中的run方法.  
    public void run() { 
    	try {
    		// 在run方法中编写需要执行的操作  
        	String TomcatHome = System.getProperty("catalina.home");
    		Properties properties = new Properties();
    		 // 使用InPutStream流读取properties文件
    		BufferedReader bufferedReader;
    		bufferedReader = new BufferedReader(new FileReader(TomcatHome+"/conf/File.properties"));
    		properties.load(bufferedReader);
    		 // 获取key对应的value值
    		String Uri = properties.getProperty("Uri");
    		String UserName = properties.getProperty("UserName");
    	    String PassWord = properties.getProperty("PassWord");
    	    String SortName = new String(properties.getProperty("FileName").getBytes(),"UTF-8"); // 包含中文内容需要这样处理
    	    String FileFolder = new String(properties.getProperty("FileFolder").getBytes(),"UTF-8");
    	    String Other = properties.getProperty("OtherFolder");
    	    // 按',,,'进行分割
    	    String[] SortNameList = SortName.split(",,,");  // 类别
    	    String[] FileFolderList = FileFolder.split(",,,");  // 类别对应文件夹名
    	    // 默认文件夹为其他
    	    String Folder = "";
    	    if(Other.equalsIgnoreCase("True"))
    	    	Folder = "其他";
    	    for(int i=0;i<FileFolderList.length;i++) {
    	    	if(fileName.contains(SortNameList[i])) {
    	    		Folder=FileFolderList[i];
    	    	}
    	    }
    	    // 新建WebDav连接
    	    Sardine sardine = SardineFactory.begin(UserName, PassWord);
    	    List<DavResource> resources = null;
    	    boolean flag = false;
    	    resources = sardine.list(Uri+"/");//如果是目录一定别忘记在后面加上一个斜杠
    	    for (DavResource res : resources)
    	    {
    	        if(res.toString().contains(Folder)) {
    	        	flag = true;
    	        }
    	    }
    	    if(!flag) {  //查看文件夹是否存在
    	    	sardine.createDirectory(Uri+Folder+"/");
    	    }
    	    SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间 
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// 设置时间格式  
            Date date = new Date();// 获取当前时间 
            System.out.println(sdf.format(date)+": "+fileName); // 输出已经格式化的现在时间（24小时制）
    	    fileName = URLEncoder.encode(fileName,java.nio.charset.StandardCharsets.UTF_8.toString());
    	    fileName = fileName.replaceAll("\\+", "%20");
            sardine.put(Uri+Folder+"/"+ fileName,data);
            sardine.shutdown();
    	}catch (Exception e) {
    		// 异常信息判断
			String message = "文件上传失败（错误码: 0）未知原因";
			if(e.getClass().toString().contains("com.github.sardine.impl.SardineException"))
			{
				if(e.toString().contains("status code: 404"))
					message = "文件上传失败（错误码: 3）可能原因: 网络不佳，文件服务器掉线";
				else if (e.toString().contains("status code: 404"))
					message = "文件上传失败（错误码: 3）可能原因: 已经上传相同名称文件，文件正在保存到文件服务器，如要重新提交请稍后再上传";
				else
					message = "文件上传失败（错误码: 2）可能原因: 未选择文件";
			}
			else if(e.getClass().toString().contains("org.apache.http.conn.HttpHostConnectException") || e.getClass().toString().contains("java.net.UnknownHostException"))
			{
				 message = "文件上传失败（错误码: 3）可能原因: 网络不佳，文件服务器掉线";
			}
			// 打印异常信息
			System.out.println(e);
			request.setAttribute("message", "错误信息: " + message);
			try {
				request.getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
			} catch (ServletException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    }
}  