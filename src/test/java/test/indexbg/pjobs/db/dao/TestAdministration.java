package test.indexbg.pjobs.db.dao;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.indexbg.system.SysClassifAdapter;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.db.Administration;
import indexbg.pjobs.db.OrgStructure;
import indexbg.pjobs.db.dao.AdministrationDAO;
import indexbg.pjobs.system.SystemData;

public class TestAdministration {

	@Test
	public void TestSimpleSave() {
		
		
		     
		System.out.println("Старт");
		Long userId = -1L;
		
		Administration adm = new Administration();
		adm.setAddInfo("1");
		adm.setAddress("2");
		adm.setAdmLevel(3L);
		adm.setAdmRegister(true);
		adm.setDateFrom(new Date());
		adm.setDateTo(new Date());
		adm.setEik("4");
		adm.setEmail("5");
		adm.setFax("6");
		adm.setMunicipality(7L);
		adm.setNomerRegister("8");
		adm.setPhone("9");
		adm.setRegion(10L);
		adm.setSubjectName("11 - Test Administration");
		adm.setTown(12L);
		adm.setZipCode(13L);
		
		
		
		
		
		SystemData sd = new SystemData();
		
		try {
			JPA.getUtil().begin();
			AdministrationDAO dao = new AdministrationDAO(userId, sd);
			dao.save(adm);
			
			JPA.getUtil().commit();
			
		} catch (Exception e) {
			//JPA.getUtil().rollback();
			fail();
		}
		
		
	}
	
	
	
	
	
	
	
	@Test
	public void fillRegister() {
		
		SystemData sd = new SystemData();
		AdministrationDAO dao = new AdministrationDAO(-1L, sd);
		try {
			dao.updateAdmRegisterEntries();
		} catch (DbErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (UnexpectedResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	@Test
	public void findRegister() {
		
		SystemData sd = new SystemData();
		AdministrationDAO dao = new AdministrationDAO(-1L, sd);
		
		
			
		
		
		try {
			for (int i = 0; i < 3; i++) {
			dao.findByNomReg("123");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		
		}
	}
	
	
	
	@Test
	public void testCreateAdmClassif() {
		SystemData sd = new SystemData();
		
		List<SystemClassif> classif;
		try {
			classif = sd.getSysClassification(10100, new Date(), 1L, -1L);
			SysClassifUtils.doSortClassifPrev(classif);
			for (SystemClassif tek : classif) {
				for (int j = 0; j < tek.getLevelNumber(); j++) {
					System.out.print("\t");
				}
				System.out.println(tek.getTekst());
				
			}
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	@Test
	public void testStrBySubject() {
		
		
		try {
			Long idSubject = 13793L;
			Date dat = new Date();
			
			SystemData sd = new SystemData();
			AdministrationDAO dao = new AdministrationDAO(-1L, sd);
			
			List<SystemClassif> classif = dao.builAdmdStrOfSubject(idSubject, dat);
			SysClassifUtils.doSortClassifPrev(classif);
			
			System.out.println("classif.size="+classif.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	@Test
	public void testCreateFlatAdmStrClassif() {
		SystemData sd = new SystemData();
		
		
//		try {
//			
//			List<SystemClassif> classif = new SysClassifAdapter(sd).createFlatAdmStr();
//			for (SystemClassif tek : classif) {				
//				System.out.println(tek.getTekst());				
//			}
//			
//			
//			
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
	}
	
	
}
