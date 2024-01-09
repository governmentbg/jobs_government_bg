package indexbg.pjobs.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.component.FacesComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.indexbg.indexui.CompObjAuditSys;
import com.indexbg.indexui.report.uni.SprObject;
import com.indexbg.system.ObjectsDifference;
import com.indexbg.system.SysConstants;
import com.indexbg.system.db.dto.SystemJournal;
import com.indexbg.system.utils.JAXBHelper;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.comparators.ObjectComparator;
import indexbg.pjobs.db.AdmGroups;
import indexbg.pjobs.db.AdmUsers;
import indexbg.pjobs.db.Advertisement;
import indexbg.pjobs.db.Practice;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.SystemData;



/** */
@FacesComponent(value = "compObjAudit", createTag = true)
public class CompObjAudit extends CompObjAuditSys {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompObjAudit.class);
	
		
	/**
	 * Зарежда текущите разликите - сегашно и предишно състояние
	 * @param selectedEvent
	 */
	@Override
	public List<ObjectsDifference> loadCurrentDiff(SystemJournal currentEventTmp,SystemJournal previousEventTmp) {
		List<ObjectsDifference> compareResult=new ArrayList<ObjectsDifference>(); 
		
		
		LOGGER.info("LoadCurrentDiff between {} and {}",currentEventTmp!=null?currentEventTmp.getId():null,previousEventTmp!=null?previousEventTmp.getId():null);
		
		try {
						
//			Object xmlToObject2 = JAXBHelper.xmlToObject2(getSelectedEvent().getObjectXml());
//			System.out.println("==========================="+xmlToObject2.getClass());
			
			Object currentObj=null,prevObj=null;
			Long codeObject=currentEventTmp!=null?currentEventTmp.getCodeObject():previousEventTmp.getCodeObject();
			Long codeAction=currentEventTmp!=null?currentEventTmp.getCodeAction():previousEventTmp.getCodeAction();
			
			if (codeAction != null && codeAction.equals(SysConstants.CODE_DEIN_UNISEARCH)) {
				if (previousEventTmp.getObjectXml() != null) {
					SprObject spr = JAXBHelper.xmlToObject(SprObject.class, previousEventTmp.getObjectXml());
					
					return convertSprObjectToDifferences(spr, getSystemData(), previousEventTmp.getDateAction()) ;
				}
			}
			
			
			switch (codeObject.intValue()) {
				//TODO
				case ((int)Constants.CODE_OBJECT_PRACTICE):
					if (codeAction != null && codeAction.equals(SysConstants.CODE_DEIN_SEARCH)) {
//						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, currentEventTmp.getObjectXml()):new DocSearch();
//						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, previousEventTmp.getObjectXml()):new DocSearch();
					}else {
						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Practice.class, currentEventTmp.getObjectXml()):new Practice();
						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Practice.class, previousEventTmp.getObjectXml()):new Practice();
					}
				break;	
				
				
				case ((int)Constants.CODE_OBJECT_ADVERTISEMENT):
					if (codeAction != null && codeAction.equals(SysConstants.CODE_DEIN_SEARCH)) {
//						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, currentEventTmp.getObjectXml()):new DocSearch();
//						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, previousEventTmp.getObjectXml()):new DocSearch();
					}else {
						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Advertisement.class, currentEventTmp.getObjectXml()):new Advertisement();
						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Advertisement.class, previousEventTmp.getObjectXml()):new Advertisement();
					}
				break;	
				
				case ((int)Constants.CODE_OBJECTS_USERS):
					if (codeAction != null && codeAction.equals(SysConstants.CODE_DEIN_SEARCH)) {
//						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, currentEventTmp.getObjectXml()):new DocSearch();
//						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, previousEventTmp.getObjectXml()):new DocSearch();
					}else {
						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(AdmUsers.class, currentEventTmp.getObjectXml()):new Advertisement();
						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(AdmUsers.class, previousEventTmp.getObjectXml()):new Advertisement();
					}
				break;	
				
				case ((int)Constants.CODE_OBJECTS_GROUPUSER):
					if (codeAction != null && codeAction.equals(SysConstants.CODE_DEIN_SEARCH)) {
//						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, currentEventTmp.getObjectXml()):new DocSearch();
//						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, previousEventTmp.getObjectXml()):new DocSearch();
					}else {
						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(AdmGroups.class, currentEventTmp.getObjectXml()):new Advertisement();
						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(AdmGroups.class, previousEventTmp.getObjectXml()):new Advertisement();
					}
				break;				
			
				default:
					LOGGER.error("Object code="+currentEventTmp.getCodeObject()+" not implemented");
				break;
			}
//			Doc currentDoc = JAXBHelper.xmlToObject(Doc.class, getSelectedEvent().getObjectXml());
//			Doc prevDoc = previousEventTmp!=null?JAXBHelper.xmlToObject(Doc.class, previousEventTmp.getObjectXml()):new Doc();
//			
			 compareResult = new ObjectComparator(
					previousEventTmp!=null?previousEventTmp.getDateAction():new Date(),
					currentEventTmp!=null?currentEventTmp.getDateAction():new Date(),
							(SystemData) JSFUtils.getManagedBean("systemData"), 
							null).compare( prevObj,currentObj);
			 
			 
			
			 
			
		} catch (Exception e1) {
			LOGGER.error("",e1);
			e1.printStackTrace();
		} 
		return compareResult;

	}
	


	
	
}