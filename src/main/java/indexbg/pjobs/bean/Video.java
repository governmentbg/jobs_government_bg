package indexbg.pjobs.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;

import indexbg.pjobs.db.Files;
import indexbg.pjobs.db.dao.FilesDAO;
import indexbg.pjobs.system.Constants;

public class Video extends HttpServlet {


    /**
	 * 
	 */
	private static final long serialVersionUID = 198104179662228558L;

	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        
		
		String param = request.getParameter("param");
		
		if(param!=null && !param.isEmpty()) {
			
			
			
			param = new String(Base64.getDecoder().decode(param));
			String[] params = param.split(";");
		
		
			response.setContentType("video/mp4");
	        ServletOutputStream out = response.getOutputStream();
	        
	        InputStream input =null;
	        
	        try {
				List<Files> filesList = (List<Files>)new FilesDAO(Constants.PORTAL_USER).loadAllData(Constants.CODE_OBJECT_PUBLICATION, Long.valueOf(params[0]), Long.valueOf(params[1]));
				input = new ByteArrayInputStream(filesList.get(0).getContent());
	        } catch (DbErrorException e) {
			 	e.printStackTrace();
			} finally {
				JPA.getUtil().closeConnection();
			}
	        
	    	int read = 0;
	    	byte[] outputByte = new byte[1024];
		    if(input!=null) {
		       while((read=input.read(outputByte)) != -1) {
		    	   out.write(outputByte, 0, read);
		       }
		       input.close();
		    }   
	        out.flush();
	        out.close();
		}
		
    }
}
