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
	// WebDav��ʼ��
	public WebDav(String fileName, byte[] data,HttpServletRequest request,HttpServletResponse response) {
    	this.fileName = fileName;
    	this.data = data;
    	this.request = request;
    	this.response = response;
	}

	// �����и���Runnable�ӿ��е�run����.  
    public void run() { 
    	try {
    		// ��run�����б�д��Ҫִ�еĲ���  
        	String TomcatHome = System.getProperty("catalina.home");
    		Properties properties = new Properties();
    		 // ʹ��InPutStream����ȡproperties�ļ�
    		BufferedReader bufferedReader;
    		bufferedReader = new BufferedReader(new FileReader(TomcatHome+"/conf/File.properties"));
    		properties.load(bufferedReader);
    		 // ��ȡkey��Ӧ��valueֵ
    		String Uri = properties.getProperty("Uri");
    		String UserName = properties.getProperty("UserName");
    	    String PassWord = properties.getProperty("PassWord");
    	    String SortName = new String(properties.getProperty("FileName").getBytes(),"UTF-8"); // ��������������Ҫ��������
    	    String FileFolder = new String(properties.getProperty("FileFolder").getBytes(),"UTF-8");
    	    String Other = properties.getProperty("OtherFolder");
    	    // ��',,,'���зָ�
    	    String[] SortNameList = SortName.split(",,,");  // ���
    	    String[] FileFolderList = FileFolder.split(",,,");  // ����Ӧ�ļ�����
    	    // Ĭ���ļ���Ϊ����
    	    String Folder = "";
    	    if(Other.equalsIgnoreCase("True"))
    	    	Folder = "����";
    	    for(int i=0;i<FileFolderList.length;i++) {
    	    	if(fileName.contains(SortNameList[i])) {
    	    		Folder=FileFolderList[i];
    	    	}
    	    }
    	    // �½�WebDav����
    	    Sardine sardine = SardineFactory.begin(UserName, PassWord);
    	    List<DavResource> resources = null;
    	    boolean flag = false;
    	    resources = sardine.list(Uri+"/");//�����Ŀ¼һ���������ں������һ��б��
    	    for (DavResource res : resources)
    	    {
    	        if(res.toString().contains(Folder)) {
    	        	flag = true;
    	        }
    	    }
    	    if(!flag) {  //�鿴�ļ����Ƿ����
    	    	sardine.createDirectory(Uri+Folder+"/");
    	    }
    	    SimpleDateFormat sdf = new SimpleDateFormat();// ��ʽ��ʱ�� 
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// ����ʱ���ʽ  
            Date date = new Date();// ��ȡ��ǰʱ�� 
            System.out.println(sdf.format(date)+": "+fileName); // ����Ѿ���ʽ��������ʱ�䣨24Сʱ�ƣ�
    	    fileName = URLEncoder.encode(fileName,java.nio.charset.StandardCharsets.UTF_8.toString());
    	    fileName = fileName.replaceAll("\\+", "%20");
            sardine.put(Uri+Folder+"/"+ fileName,data);
            sardine.shutdown();
    	}catch (Exception e) {
    		// �쳣��Ϣ�ж�
			String message = "�ļ��ϴ�ʧ�ܣ�������: 0��δ֪ԭ��";
			if(e.getClass().toString().contains("com.github.sardine.impl.SardineException"))
			{
				if(e.toString().contains("status code: 404"))
					message = "�ļ��ϴ�ʧ�ܣ�������: 3������ԭ��: ���粻�ѣ��ļ�����������";
				else if (e.toString().contains("status code: 404"))
					message = "�ļ��ϴ�ʧ�ܣ�������: 3������ԭ��: �Ѿ��ϴ���ͬ�����ļ����ļ����ڱ��浽�ļ�����������Ҫ�����ύ���Ժ����ϴ�";
				else
					message = "�ļ��ϴ�ʧ�ܣ�������: 2������ԭ��: δѡ���ļ�";
			}
			else if(e.getClass().toString().contains("org.apache.http.conn.HttpHostConnectException") || e.getClass().toString().contains("java.net.UnknownHostException"))
			{
				 message = "�ļ��ϴ�ʧ�ܣ�������: 3������ԭ��: ���粻�ѣ��ļ�����������";
			}
			// ��ӡ�쳣��Ϣ
			System.out.println(e);
			request.setAttribute("message", "������Ϣ: " + message);
			try {
				request.getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
			} catch (ServletException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    }
}  