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

    // �ϴ�����
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 10;  // 10MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
 
 
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings({ "unused" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// ����Ƿ�Ϊ��ý���ϴ�
		if (!ServletFileUpload.isMultipartContent(request)) {
		    // ���������ֹͣ
		    PrintWriter writer = response.getWriter();
		    writer.println("Error: ��������� enctype=multipart/form-data");
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
		// ��������ļ��ϴ�ֵ
        upload.setFileSizeMax(MAX_FILE_SIZE);
		
		// Set overall request size constraint
		upload.setSizeMax(MAX_REQUEST_SIZE);

       
 
		// Parse the request
		try {
			List<FileItem> items = upload.parseRequest(request);
			
			//2.����items������һ����򣬴�ӡ��Ϣ
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
			    	//�����ļ����򱣴�
			    	if (!item.isFormField()) {
			    		String fileName = item.getName();
			    		if(fileName=="")
			    		{
			    			request.setAttribute("message", "������Ϣ: �ļ��ϴ�ʧ�ܣ�������: 1������ԭ��: δѡ���ļ�");
			    		}
			    		else {
			    			// �½�WebDav��
			    			WebDav wd = new WebDav(fileName,item.get(),request,response);
			                Thread t = new Thread(wd);  
			                t.start();
			    			Thread.sleep(2000);
				    	    request.setAttribute("message",
		                            "�ļ��ϴ��ɹ�!");
			    		}
			    	}
			    	
			    }
			}
			
		} catch (Exception e) {
			// �쳣��Ϣ�ж�
			String message =  "�ļ��ϴ�ʧ�ܣ�������: 0��δ֪ԭ��";
			if(e.getClass().toString().contains("com.github.sardine.impl.SardineException"))
			{
				 message = "�ļ��ϴ�ʧ�ܣ�������: 2������ԭ��: δѡ���ļ�";
			}
			else if(e.getClass().toString().contains("org.apache.http.conn.HttpHostConnectException"))
			{
				 message = "�ļ��ϴ�ʧ�ܣ�������: 3������ԭ��: ���粻�ѣ��ļ�����������";
			}
			// ��ӡ�쳣��Ϣ
			System.out.println(e);
			request.setAttribute("message", "������Ϣ: " + message);
		}
		
		request.getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);	
	}
 
}