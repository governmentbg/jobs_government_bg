package indexbg.pjobs.db.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexbg.system.db.JPA;
import com.indexbg.system.db.TrackableDAO;
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.BaseException;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.exceptions.UnexpectedResultException;
import com.indexbg.system.utils.DateUtils;
import com.indexbg.system.utils.SearchUtils;
import com.indexbg.system.utils.StringUtils;

import indexbg.pjobs.db.Administration;
import indexbg.pjobs.db.OrgStructure;
import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.SystemData;
import indexbg.pjobs.wsclient.admreg.ArrayOfBatchIdentificationInfoType;
import indexbg.pjobs.wsclient.admreg.ArrayOfBatchType;
import indexbg.pjobs.wsclient.admreg.ArrayOfString;
import indexbg.pjobs.wsclient.admreg.BatchIdentificationInfoType;
import indexbg.pjobs.wsclient.admreg.BatchStatusEnum;
import indexbg.pjobs.wsclient.admreg.BatchType;
import indexbg.pjobs.wsclient.admreg.CorrespondenceDataPhoneType;
import indexbg.pjobs.wsclient.admreg.IBatchInfoService;
import indexbg.pjobs.wsclient.admreg.PositionType;
import indexbg.pjobs.wsclient.admreg.UnitPositionCommonDataType;
import indexbg.pjobs.wsclient.admreg.UnitType;

public class AdministrationDAO extends TrackableDAO<Administration> {	

	static final Logger LOGGER = LoggerFactory.getLogger(AdministrationDAO.class);
	
	private SystemData sd;
	
	public AdministrationDAO (Long userId ,SystemData sd){
		
		super(userId);	
		this.sd = sd;
	}
	

	private void mergeResSubject(Administration oldSubj, Administration newSubj) {
		
		oldSubj.setAddInfo(newSubj.getAddInfo());
		oldSubj.setAddress(newSubj.getAddress());
		oldSubj.setAdmLevel(newSubj.getAdmLevel());
		oldSubj.setAdmRegister(newSubj.getAdmRegister());		
		oldSubj.setEik(newSubj.getEik());
		oldSubj.setEmail(newSubj.getEmail());
		oldSubj.setFax(newSubj.getFax());
		oldSubj.setMunicipality(newSubj.getMunicipality());
		oldSubj.setNomerRegister(newSubj.getNomerRegister());
		oldSubj.setPhone(newSubj.getPhone());
		oldSubj.setRegion(newSubj.getRegion());
		oldSubj.setSubjectName(newSubj.getSubjectName());
		oldSubj.setTown(newSubj.getTown());
		//oldSubj.setZipCode(newSubj.getZipCode());
		
	}
	
	
	private ArrayList<String> getClosedSujectsIds(Date dat) throws UnexpectedResultException {
		
		ArrayList<String> ids = new ArrayList<String>();
		
		try {
			URL url = new URL("https://iisda.government.bg/Services/RAS/RAS.Integration.Host/BatchInfoService.svc?singleWsdl");
			QName qname = new QName("http://iisda.government.bg/RAS/IntegrationServices", "BatchInfoService");
			//		http://wsmf.delo.indexbg.com
			Service service = Service.create(url, qname);

			// Hello helloPort = service.getHelloPort();
			IBatchInfoService sPort = (IBatchInfoService) service
					.getPort(IBatchInfoService.class,new AddressingFeature(true, true));
			
			BatchStatusEnum status = BatchStatusEnum.CLOSED;
			ArrayOfBatchIdentificationInfoType searchBatchesIdentificationInfo = sPort.searchBatchesIdentificationInfo(null, null, null, null, status , DateUtils.toXMLGregorianCalendar(dat), null);
			List<BatchIdentificationInfoType> organizations = searchBatchesIdentificationInfo.getBatchIdentificationInfoType();			
			for (BatchIdentificationInfoType org : organizations) {
				ids.add(org.getIdentificationNumber());
			}
			
			return ids;
			
		} catch (Exception e) {
			throw new UnexpectedResultException("Грешка при извикаване търсене на неактивни субекти", e);
		}
		
	}
	
	
	
	public String updateAdmRegisterEntries() throws DbErrorException, UnexpectedResultException {
		
		long brUpdated = 0;
		long brInserted = 0;
		long brClosed = 0;
		
		
		try {
			JPA.getUtil().begin();
			
			ArrayList<Administration> records = getSubjectsfromAdmRegister(new Date());
			
			
			for (Administration tek: records) {
				//System.out.println(tek.getSubjectName());
				
//				if (tek.getNomerRegister().contains("0000000154")) {
//					System.out.println("*****");
//				}
				
				
				
				Administration subject = findByNomReg(tek.getNomerRegister());
				Long subjectId = null;
				if (subject != null) {					
					mergeResSubject(subject, tek);
			//		System.out.println("Updating " + tek.getSubjectName() + "с id="+tek.getId());
					save(subject);
					subjectId = subject.getId();
					brUpdated++;
				}else {
		//			System.out.println("Saving " + tek.getSubjectName() + "с id="+tek.getId());
					save(tek);
					subjectId = tek.getId();
					brInserted++;
				}
				insertAdmStructures(subjectId, tek.getStructures());
			}
			
			ArrayList<String> ids = getClosedSujectsIds(new Date());
			for (String id : ids) {
				Administration subject = findByNomReg(id);				
				if (subject != null && subject.getDateTo() == null) {
					subject.setDateTo(new Date());
					save(subject);
					brClosed++;
				}
			}
			
			JPA.getUtil().commit();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			String result = "Дата и час на извикване : " + sdf.format(new Date()) + "\r\n";
			result += "Брой нови: " + brInserted + "\r\n";
			result += "Брой променени: " + brUpdated + "\r\n";
			result += "Брой затворени: " + brClosed + "\r\n";
			return result;
			
			
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			LOGGER.error("DBError in  adm register !", e);
			throw new DbErrorException("Грешка при запис на субекти от административния регистър !\r\n" + StringUtils.stack2string(e)); 
		} catch (UnexpectedResultException e) {
			JPA.getUtil().rollback();
			LOGGER.error("Unexpected error in adm registry load !");
			throw new UnexpectedResultException("Грешка при четене на данни за субекти от административния регистър !\r\n" + StringUtils.stack2string(e));
		}
	}
	
	
	
	


	


	/** Търсене на задлжени субекти по номер от адм.регистър
	 * @param nomRegister
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public Administration findByNomReg(String nomRegister) throws DbErrorException {
		try {
			
			
			Query q = JPA.getUtil().getEntityManager().createQuery("from  Administration where nomerRegister = :nom");
			q.setParameter("nom", nomRegister);
			List<Administration> subjects = q.getResultList();
			if (subjects.size() > 0) {
				return subjects.get(0);
			}else {
				return null;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbErrorException("Грешка при извикаване на запис за задължен субек по номер от адм. регистър", e);
		}
		
	}
	
	
	
	private ArrayList<Administration> getSubjectsfromAdmRegister(Date dat) throws UnexpectedResultException, DbErrorException {
		
		
		List<SystemClassif> classif = sd.getSysClassification(Constants.CODE_SYSCLASS_VID_ADM, dat, Constants.CODE_DEFAULT_LANG, -1L);
		
		
		ArrayList<Administration> subjects = new ArrayList<Administration>();
		try {
			URL url = new URL("https://iisda.government.bg/Services/RAS/RAS.Integration.Host/BatchInfoService.svc?singleWsdl");
			QName qname = new QName("http://iisda.government.bg/RAS/IntegrationServices", "BatchInfoService");
			//		http://wsmf.delo.indexbg.com
			Service service = Service.create(url, qname);

			// Hello helloPort = service.getHelloPort();
			IBatchInfoService sPort = (IBatchInfoService) service
					.getPort(IBatchInfoService.class,new AddressingFeature(true, true));
			
			BatchStatusEnum status = BatchStatusEnum.ACTIVE;
			ArrayOfBatchIdentificationInfoType searchBatchesIdentificationInfo = sPort.searchBatchesIdentificationInfo(null, null, null, null, status , DateUtils.toXMLGregorianCalendar(dat), null);
			@SuppressWarnings("unused")
						
			List<BatchIdentificationInfoType> organizations = searchBatchesIdentificationInfo.getBatchIdentificationInfoType();
			
			for (BatchIdentificationInfoType org : organizations) {
				
				//System.out.println(org.getName() + " ---> " + org.getIdentificationNumber());
				ArrayOfString batchIdentificationNumber = new ArrayOfString() ;
				batchIdentificationNumber .getString().add(org.getIdentificationNumber());
				
//				if (org.getIdentificationNumber().equals("0000000081")) {
//					TerritorialAdmStructure terr = sPort.getTerritorialAdmStructure(org.getUIC());
//					System.out.println("");
//				}
				
							
				 ArrayOfBatchType all = sPort.getBatchDetailedInfo(batchIdentificationNumber, DateUtils.toXMLGregorianCalendar(dat), null);
				 if (all != null && all.getBatchType() != null && all.getBatchType().size() == 1) {					 
					 Administration res = convertToResponseSubject(all.getBatchType().get(0), classif);					
					 if (res != null) {
						 ArrayList<OrgStructure> orgStructures = getSubjectAdministrations(all.getBatchType().get(0));
						 res.setStructures(orgStructures);
						 subjects.add(res);
						 //System.out.print("*");
					 }
				 }
				 
				 
				 
				 
			}	 	
		} catch (Exception e) {
			LOGGER.error("Error in IBatchInfoService Web Service !", e);
			throw new UnexpectedResultException("Грешка при извикване на административния регистър !", e);
		}
		
		
		return subjects;
	}
	
	
	@SuppressWarnings("unchecked")
	private Administration convertToResponseSubject(BatchType batchType, List<SystemClassif> classif) throws UnexpectedResultException {
		try {
			
			
			
			
			
			Administration subject = new Administration();
			subject.setAdmRegister(true);
			subject.setSubjectName(batchType.getName());
			subject.setNomerRegister(batchType.getIdentificationNumber());
			subject.setDateTo(null);
			
//			if (subject.getSubjectName().equals("Антидопингов център")) {
//				System.out.println("dsfsd");
//			}
			
			
			
			
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");				
			subject.setDateFrom(sdf.parse("01.01.1900"));			
			
			
			
			if (batchType.getAdmStructureKind() != null) {
				
				Long admCode = null;
				for (SystemClassif item : classif) {
					if (item.getCodeExt() != null && item.getCodeExt().equalsIgnoreCase(batchType.getAdmStructureKind().value())) {
						admCode = item.getCode();
						break;
					}					
				}
				if (admCode!= null) {
					subject.setAdmLevel(admCode);
				}else {
					subject.setAdmLevel(Constants.CODE_ZNACHENIE_DRUGA_ADMINISTRACIA);
				}
				
				
				//subject.setAddInfo(batchType.getAdmStructureKind().value());
			}else {
				subject.setAdmRegister(true);
				subject.setAdmLevel(Constants.CODE_ZNACHENIE_DRUGA_ADMINISTRACIA);
			}
			
			
			
			if (batchType.getAdministration() != null) {
				
				if (batchType.getAdministration().getUIC() != null) {
					subject.setEik(batchType.getAdministration().getUIC());
				}else {
					subject.setEik("N/A");
				}
				
				if (batchType.getAdministration().getAddress() != null) {
					long ekatte = Long.valueOf(batchType.getAdministration().getAddress().getEkatteAddress().getSettlementEkatteCode());
					subject.setAddress(batchType.getAdministration().getAddress().getAddressText());
					subject.setTown(ekatte);
					if (batchType.getAdministration().getAddress().getEkatteAddress() != null && batchType.getAdministration().getAddress().getEkatteAddress().getSettlementEkatteCode() != null) {
						Query q = JPA.getUtil().getEntityManager().createNativeQuery("select ekatte_oblasti.id obl_id, ekatte_obstini.id obsht_id from ekatte_att, ekatte_oblasti, ekatte_obstini where ekatte_att.oblast = ekatte_oblasti.oblast and  ekatte_att.obstina = ekatte_obstini.obstina and  ekatte_att.ekatte = :cod");
						q.setParameter("cod", ekatte);
						
						List<Object[]> mesta = q.getResultList();
						if (mesta.size() == 1) {
							subject.setMunicipality(SearchUtils.asLong(mesta.get(0)[1]));
							subject.setRegion(SearchUtils.asLong(mesta.get(0)[0]));
						}
					}
					
					
				}
				
				if (batchType.getAdministration().getCorrespondenceData() != null) {
					//String code = batchType.getAdministration().getCorrespondenceData().getInterSettlementCallingCode();
				    
					String phone = "";
					for (CorrespondenceDataPhoneType tek : batchType.getAdministration().getCorrespondenceData().getPhone()) {
						//if (tek.isIncludesSettlementCallCode()) {
							phone += tek.getPhoneNumber() + "; ";
	//					}else {
	//						phone += code + " " + tek.getPhoneNumber() + ";";
	//					}
						
					}			
					subject.setPhone(phone);
					
					subject.setFax(batchType.getAdministration().getCorrespondenceData().getFaxNumber());
					subject.setEmail(batchType.getAdministration().getCorrespondenceData().getEmail());
				}
				
			}else {				
				//return null;
				subject.setEik("N/A");				
			}
			
			
				
			
			
			
			
			
	//		if (batchType.getAdministration() != null && batchType.getAdministration().getAddress() != null) {
	//			subject.setAddress(batchType.getAdministration().getAddress().getAddressText());
	//		}
		
		
		
		return subject;
		} catch (Exception e) {
			LOGGER.error("Error in IBatchInfoService Web Service !", e);
			throw new UnexpectedResultException("Грешка при извикване и обработване на данни от  административния регистър !", e);
		}
	}
	
	
	
	public ArrayList<OrgStructure> getSubjectAdministrations(BatchType bt) {
		
		//if (bt.getAdministration().get)
		
		
		
		boolean print = false;
		if (bt.getIdentificationNumber().equals("0000000001")) {
			print = true;
			System.out.println("------------->" + bt.getName());
		}
		
		ArrayList<OrgStructure> all = new  ArrayList<OrgStructure>();		
			
		if (bt != null && bt.getAdministration() != null ) {
			for (indexbg.pjobs.wsclient.admreg.UnitPositionCommonDataType str : bt.getAdministration().getUnitPositionsInAdm()) {
				
				recAddStructure((UnitType)str, null, all, "", print);
			}
			
			for (Object obj : bt.getAdministration().getUnitPositionsSubjectOfHeadOfAdm()) {
				if (obj instanceof UnitType) {
					recAddStructure((UnitType)obj, null, all, "", print);
				}else {
					if (obj instanceof PositionType) {
						addPositionAsStr((PositionType)obj, null, all, "", print);
					}
				}
			}
			
			if (bt.getAdministration().getSecretaryPosition() != null && bt.getAdministration().getSecretaryPosition().getChildPositionsAndUnit() != null && bt.getAdministration().getSecretaryPosition().getChildPositionsAndUnit().size() > 0) {
				
				addPositionAsStr((PositionType)bt.getAdministration().getSecretaryPosition(), null, all, "", print);
				
				for (Object obj :  bt.getAdministration().getSecretaryPosition().getChildPositionsAndUnit()) {
					if (obj instanceof UnitType) {
						recAddStructure((UnitType)obj, null, all, "", print);
					}else {
						if (obj instanceof PositionType) {
							addPositionAsStr((PositionType)obj, null, all, "", print);
						}
					}
				}
			}





		}
		
		return all;
			
	}
	
	
	@SuppressWarnings("unchecked")
	private void recAddStructure(UnitType str, Long idSubject, ArrayList<OrgStructure> all, String otm, boolean print) {
		
//		if (str == null || str.getClosingDate() != null) {
//			return ;
//		}
		
		
		
		
		//System.out.println("*** " + str.getName());
		OrgStructure orgStr = new OrgStructure();
		orgStr.setDatFrom(DateUtils.toDate(str.getCreationDate()));
		orgStr.setDatTo(DateUtils.toDate(str.getClosingDate()));
		orgStr.setStrName(str.getName());
		orgStr.setIdParent(str.getParentID());
		orgStr.setIdSubject(idSubject);
		orgStr.setId(str.getUnitPosID());
		
		if (print && orgStr.getDatTo() == null) {
			System.out.println(otm + str.getName());
		}
		
		
		if (str.getAddress() != null ) {
			
			orgStr.setAddress(str.getAddress().getAddressText() );
					
			if (str.getAddress().getEkatteAddress() != null && str.getAddress().getEkatteAddress().getSettlementEkatteCode() != null) {
				
				long ekatte = Long.valueOf(str.getAddress().getEkatteAddress().getSettlementEkatteCode());
				
				orgStr.setTown(ekatte);
				
				Query q = JPA.getUtil().getEntityManager().createNativeQuery("select ekatte_oblasti.id obl_id, ekatte_obstini.id obsht_id from ekatte_att, ekatte_oblasti, ekatte_obstini where ekatte_att.oblast = ekatte_oblasti.oblast and  ekatte_att.obstina = ekatte_obstini.obstina and  ekatte_att.ekatte = :cod");
				q.setParameter("cod", ekatte);
				
				List<Object[]> mesta = q.getResultList();
				if (mesta.size() == 1) {
					orgStr.setMunicipality(SearchUtils.asLong(mesta.get(0)[1]));
					orgStr.setRegion(SearchUtils.asLong(mesta.get(0)[0]));
				}
			}
		}
		
		all.add(orgStr);
		
		
		if (str.getChildPositionsAndUnit() != null) {
			for (UnitPositionCommonDataType child : str.getChildPositionsAndUnit()) {
				recAddStructure((UnitType)child, idSubject, all, otm+ "\t" , print);				
			}
		}
	}
		
		
	@SuppressWarnings("unchecked")
	private void addPositionAsStr(PositionType str, Long idSubject, ArrayList<OrgStructure> all, String otm, boolean print) {
		
				
		
		//System.out.println("*** " + str.getName());
		OrgStructure orgStr = new OrgStructure();
		orgStr.setDatFrom(DateUtils.toDate(str.getCreationDate()));
		orgStr.setDatTo(DateUtils.toDate(str.getClosingDate()));
		orgStr.setStrName(str.getName());
		orgStr.setIdParent(str.getParentID());
		orgStr.setIdSubject(idSubject);
		orgStr.setId(str.getUnitPosID());
		
		if (print && orgStr.getDatTo() == null) {
			System.out.println(otm + str.getName());
		}
		
		all.add(orgStr);
	}	
		
		
	
	
	
	private void insertAdmStructures(Long subjectId, List<OrgStructure> structures) throws DbErrorException {
		
		
		try {
			JPA.getUtil().getEntityManager().createNativeQuery("delete from jobs_administration_structures where id_subject = ?").setParameter(1, subjectId).executeUpdate();
			
			for (OrgStructure org : structures) {
	//			System.out.println("inserting str " + org.getStrName());							
				Query q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_administration_structures(id, str_name, date_from, date_to, id_parent, id_subject, region, municipality, town, address) 	VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				q.setParameter(1, org.getId());
				q.setParameter(2, org.getStrName());
				q.setParameter(3, org.getDatFrom());				
				q.setParameter(4, new TypedParameterValue(StandardBasicTypes.DATE, org.getDatTo()));				
				q.setParameter(5, new TypedParameterValue(StandardBasicTypes.LONG, org.getIdParent()));				
				q.setParameter(6, subjectId);
				q.setParameter(7, new TypedParameterValue(StandardBasicTypes.LONG, org.getRegion()));	
				q.setParameter(8, new TypedParameterValue(StandardBasicTypes.LONG, org.getMunicipality()));	
				q.setParameter(9, new TypedParameterValue(StandardBasicTypes.LONG, org.getTown()));	
				q.setParameter(10, new TypedParameterValue(StandardBasicTypes.STRING, org.getAddress()));	
				q.executeUpdate();
			}
			
			
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при записване на организационна структура към администрация !", e);
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<SystemClassif> builAdmdStrOfSubject(Long idSubject, Date dat) throws DbErrorException {
		
		
		try {
		
			List<SystemClassif> classif = new ArrayList<SystemClassif>();
			
			Query q = JPA.getUtil().getEntityManager().createNativeQuery("SELECT id, str_name, date_from, date_to, id_parent, id_subject 	FROM jobs_administration_structures where id_subject = ? and date_from <= ? and (date_to >= ? or date_to is null )");
			q.setParameter(1, idSubject);
			q.setParameter(2, dat);
			q.setParameter(3, dat);
			ArrayList<Object[]> structures = (ArrayList<Object[]>) q.getResultList();		
			//System.out.println(structures.size());
			long codePrev = 0;
			for (Object[] str : structures) {
				if (str[4] == null) {
					
					BigDecimal idD = (BigDecimal)str[0];
					
					SystemClassif item = new SystemClassif();
					item.setCode(new Long(idD.longValue()));
					item.setCodeClassif(-999L);
					item.setCodeParent(0L);
					item.setCodePrev(codePrev);
					item.setDateDo((Date)str[3]);
					item.setDateOt((Date)str[2]);
					item.setId(item.getCode());
					item.setLevelNumber(1);
					item.setTekst((String)str[1]);
					
					classif.add(item);
					recAddAdmChildren(item, structures, classif);
					codePrev = item.getCode();
				}
			}
			
			
			return classif;
		
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изграждане на дирекции и отдели !", e);
		}
	}
	
	
	private void recAddAdmChildren(SystemClassif tek, ArrayList<Object[]> structures, List<SystemClassif> classif ) {
		
		long codePrev = 0;
		for (Object[] str : structures) {
			if (str[4] != null && ((BigDecimal)str[4]).longValue() == tek.getCode()) {
				
				
				BigDecimal idD = (BigDecimal)str[0];
				
				SystemClassif item = new SystemClassif();
				item.setCode(new Long(idD.longValue()));
				item.setCodeClassif(-999L);
				item.setCodeParent(tek.getCode());
				item.setCodePrev(codePrev);
				item.setDateDo((Date)str[3]);
				item.setDateOt((Date)str[2]);
				item.setId(item.getCode());
				item.setLevelNumber(tek.getLevelNumber()+1);
				item.setTekst((String)str[1]);
				
				classif.add(item);
				recAddAdmChildren(item, structures, classif);
				codePrev = item.getCode();
				
			}
			
		}
	}
	
	public void updateSalaryTables(byte[] salaryExcelTable) throws BaseException {
		
		InputStream is = new ByteArrayInputStream(salaryExcelTable);
		
		try { 
			
			JPA.getUtil().begin();
			
			JPA.getUtil().getEntityManager().createNativeQuery("delete from jobs_salary_level").executeUpdate();
			JPA.getUtil().getEntityManager().createNativeQuery("delete from jobs_salary").executeUpdate();
			
			
			
			List<SystemClassif> classif = sd.getSysClassification(Constants.CODE_SYSCLASS_POSITION_LEVEL, new Date(), Constants.CODE_DEFAULT_LANG, getUserId());
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(is);
			
			XSSFSheet sheet = workbook.getSheetAt(0);
			int idTable = 1;
			int count = 0;
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				XSSFRow row = sheet.getRow(i);
				Long codeFromClassif = null;
				String tekst = "" + row.getCell(2);
				
				for (SystemClassif tek : classif) {
					if (tek.getTekst().trim().equalsIgnoreCase(tekst.trim())) {
						codeFromClassif = tek.getCode();
						break;		
					}
				}
				//System.out.println(tekst + "  --> " + found);
				if (codeFromClassif != null) {
					
					count++;
					
					long nivo = (long) row.getCell(0).getNumericCellValue();
					long dlNivo = (long) row.getCell(1).getNumericCellValue();
					
					double min1 = (double) row.getCell(3).getNumericCellValue();
					double max1 = (double) row.getCell(4).getNumericCellValue();
					
					double min2 = (double) row.getCell(5).getNumericCellValue();
					double max2 = (double) row.getCell(6).getNumericCellValue();
					
					double min3 = (double) row.getCell(7).getNumericCellValue();
					double max3 = (double) row.getCell(8).getNumericCellValue();
					
					double min4 = (double) row.getCell(9).getNumericCellValue();
					double max4 = (double) row.getCell(10).getNumericCellValue();
					//System.out.println(max1);
					
					
					Query q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_salary(id, level_code, level) VALUES(?1,?2, ?3)");
					q.setParameter(1, nivo);
					q.setParameter(2, dlNivo);
					q.setParameter(3, codeFromClassif);
					q.executeUpdate();
					
					
					q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_salary_level(id, salary_id, salary_level, min, max) 	VALUES(?1, ?2, ?3, ?4, ?5)");
					q.setParameter(1, idTable);
					q.setParameter(2, nivo);
					q.setParameter(3, 1);
					q.setParameter(4, min1);
					q.setParameter(5, max1);
					q.executeUpdate();
					idTable++;
					
					q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_salary_level(id, salary_id, salary_level, min, max) 	VALUES(?1, ?2, ?3, ?4, ?5)");
					q.setParameter(1, idTable);
					q.setParameter(2, nivo);
					q.setParameter(3, 2);
					q.setParameter(4, min2);
					q.setParameter(5, max2);
					q.executeUpdate();
					idTable++;

					q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_salary_level(id, salary_id, salary_level, min, max) 	VALUES(?1, ?2, ?3, ?4, ?5)");
					q.setParameter(1, idTable);
					q.setParameter(2, nivo);
					q.setParameter(3, 3);
					q.setParameter(4, min3);
					q.setParameter(5, max3);
					q.executeUpdate();
					idTable++;
					
					q = JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO jobs_salary_level(id, salary_id, salary_level, min, max) 	VALUES(?1, ?2, ?3, ?4, ?5)");
					q.setParameter(1, idTable);
					q.setParameter(2, nivo);
					q.setParameter(3, 4);
					q.setParameter(4, min4);
					q.setParameter(5, max4);
					q.executeUpdate();
					idTable++;
					
				}
				
			}
			
			if (count > 0) {
				JPA.getUtil().commit();
			}else {
				throw new RuntimeException("Непознат формат на данните във файла !");
			}
			
			
			
		} catch (Exception e) {
			JPA.getUtil().rollback();
			throw new BaseException("Неочаквана грешка при наливане на файл за заплати !",e);
		}finally {
			JPA.getUtil().closeConnection();
		}
	}
	
}
