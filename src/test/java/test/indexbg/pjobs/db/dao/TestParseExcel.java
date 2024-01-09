package test.indexbg.pjobs.db.dao;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.mchange.io.FileUtils;

import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.system.SystemData;

public class TestParseExcel {

	
	@Test
	public void testIt() {
		String fileName = "C:\\Users\\vassil\\Desktop\\zaplati.xlsx";
		
		try {
			Long userId = -1L;
			byte[] bytes = FileUtils.getBytes(new File(fileName));
			SystemData sd = new SystemData();
			new AdministrationDAO(userId, sd).updateSalaryTables(bytes);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
	}
	
	
	
	
	
}
