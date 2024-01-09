package test.indexbg.pjobs.db.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.indexbg.system.SysClassifAdapter;
import com.indexbg.system.db.JPA;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.system.SystemData;

public class TestJobsClassif {

	public static void main(String[] args) {

		try {
			//List<SystemClassif> classif = new SysClassifAdapter(new SystemData()).createJobsClassif(111L, 1L, -1L);
			
			SystemData sd = new SystemData();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			Date dat = sdf.parse("03.04.2020");
			
			
			
			
			System.out.println("-------------------------------------------------------------------------------------------------------------");
			
			List<SystemClassif> classif = sd.getSysClassification(10112L, dat, 1L, -1L);
			System.out.println("-------------------->" + classif.size());
			System.out.println();
			
			SysClassifUtils.doSortClassifPrev(classif);
			
			for (SystemClassif tek : classif) {
				
				for (int i = 0; i < tek.getLevelNumber(); i++) {
					System.out.print("\t");
				}
				System.out.println(tek.getCode() + "|" + tek.getCodeParent()+ " " + tek.getTekst() + "\t " + tek.getDateOt());
			}
			
			
			
			
			
			
			System.out.println(classif.size());
			
//			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//			
//			Date start1 = sdf.parse("01.01.2001");
//			Date end1 = sdf.parse("01.01.2010");
//			
//			Date start2 = sdf.parse("01.01.2000");
//			Date end2 = sdf.parse("01.01.2016");
//			
//			System.out.println(DateUtils.isOverLaped(start1, end1, start2, end2));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JPA.getUtil().closeConnection();
		}
		
        System.exit(0);

	}

}
