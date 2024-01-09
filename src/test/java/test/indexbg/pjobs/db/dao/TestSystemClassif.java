package test.indexbg.pjobs.db.dao;


import java.util.Date;
import java.util.List;

import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.SysClassifUtils;

import indexbg.pjobs.system.SystemData;

public class TestSystemClassif {

	public static void main(String[] args) {


		SystemData sd = new SystemData();
		
		try {
			List<SystemClassif> classif = sd.getSysClassification(10100L, new Date(), 1L, 1L);
			System.out.println("-----");
			SysClassifUtils.doSortClassifPrev(classif);
			System.out.println("-----------");
			for (SystemClassif tek : classif) {
				for (int i = 0; i < tek.getLevelNumber(); i ++ ) {
					System.out.print("\t");
				}
				System.out.println(tek.getTekst());
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
