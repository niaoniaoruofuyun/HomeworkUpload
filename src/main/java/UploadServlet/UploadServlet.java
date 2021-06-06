package UploadServlet;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 10;  // 10MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
 
 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings({ "unused" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 检测是否为多媒体上传
		if (!ServletFileUpload.isMultipartContent(request)) {
		    // 如果不是则停止
		    PrintWriter writer = response.getWriter();
		    writer.println("Error: 表单必须包含 enctype=multipart/form-data");
		    writer.flush();
		    return;
		}
		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
 
		// Configure a repository (to ensure a secure temp location is used)
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);
		
		//factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		
		// Set factory constraints
		factory.setSizeThreshold(MEMORY_THRESHOLD);
 
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		// 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
		
		// Set overall request size constraint
		upload.setSizeMax(MAX_REQUEST_SIZE);

       
 
		// Parse the request
		try {
			List<FileItem> items = upload.parseRequest(request);
			
			//2.遍历items，若是一般表单域，打印信息
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
			    FileItem item = iter.next();
 
			    if (item.isFormField()) {
			    	// Process a regular form field
			    	if (item.isFormField()) {
			    	    String name = item.getFieldName();
			    	    String value = item.getString();
			    	    System.out.println(name + ":" + value);
			    	}
			    	
			    } else {
			    	//若是文件，则保存
			    	if (!item.isFormField()) {
			    		String fileName = item.getName();
			    		if(fileName=="")
			    		{
			    			request.setAttribute("message", "错误信息: 文件上传失败（错误码: 1）可能原因: 未选择文件");
			    		}
			    		else {
			    			// 新建WebDav类
			    			WebDav wd = new WebDav(fileName,item.get(),request,response);
			                Thread t = new Thread(wd);  
			                t.start();
			    			Thread.sleep(2000);
				    	    request.setAttribute("message",
		                            "文件上传成功!");
			    		}
			    	}
			    	
			    }
			}
			
		} catch (Exception e) {
			// 异常信息判断
			String message =  "文件上传失败（错误码: 0）未知原因";
			if(e.getClass().toString().contains("com.github.sardine.impl.SardineException"))
			{
				 message = "文件上传失败（错误码: 2）可能原因: 未选择文件";
			}
			else if(e.getClass().toString().contains("org.apache.http.conn.HttpHostConnectException"))
			{
				 message = "文件上传失败（错误码: 3）可能原因: 网络不佳，文件服务器掉线";
			}
			// 打印异常信息
			System.out.println(e);
			request.setAttribute("message", "错误信息: " + message);
		}
		
		request.getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);	
	}
 
}