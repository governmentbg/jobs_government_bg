package indexbg.pjobs.bean;

	
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.indexbg.system.db.JPA;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.pagination.SelectMetadata;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.db.Questions;
import indexbg.pjobs.db.dao.QuestionsDAO;

import indexbg.pjobs.system.PJobsBean;

@Named("questExtList")
@ViewScoped
public class QuestExternalBean extends PJobsBean{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6682991683868742770L;
	/**
	 * Основен javaBean клас за показване на често задавани въпроси във външния портал
	 */
	
	
	static final Logger LOGGER = LoggerFactory.getLogger(QuestExternalBean.class);
	private Long idUser=null;
	
	private List<Questions> questListT = new ArrayList<Questions>();

	private SelectMetadata smd = null;
	private String sortCol = "";
	private Long codeGrupa=null;
	
	
	private int gridcolumn =1;
	/**
	 * Инициира/сетва първоначалните стойности на атрибутите на филтъра за търсене. Чете предадените параметри от други екрани
	 */
	@PostConstruct
	public void initData(){
	
		this.idUser = -1L;
		actionClear();
//		actionFind();
	}
	
	

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Date getCurrentDate() {
		return new Date();
	}

	
	/**Изтрива стойностите на филтъра за търсене на публикациите
	 * 
	 */
	public void actionClear(){
		this.setCodeGrupa(null);
	}
	
	
	/** Метод за търсене в БД на публикации по зададените критерии/филтър 
	 * 
	 */
	public void actionFind(){
		 	
		this.setQuestListT(null);

		try {
			this.questListT = new QuestionsDAO(this.idUser).findGrupaQuestFilter(this.codeGrupa, getCurrentLang());
			
			this.questListT.size();
		
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString("beanMessages","general.errDataBaseMsg"), e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
		
			
	}


	public SelectMetadata getSmd() {
		return smd;
	}

	public void setSmd(SelectMetadata smd) {
		this.smd = smd;
	}

	public String getSortCol() {
		return sortCol;
	}

	public void setSortCol(String sortCol) {
		this.sortCol = sortCol;
	}

	public int getGridcolumn() {
		return gridcolumn;
	}

	public void setGridcolumn(int gridcolumn) {
		this.gridcolumn = gridcolumn;
	}
	
	public void actionGridTable() {
		gridcolumn=2;
	}
	
	public void actionListTable() {
		gridcolumn=1;
	}

	public Long getCodeGrupa() {
		return codeGrupa;
	}

	public void setCodeGrupa(Long codeGrupa) {
		this.codeGrupa = codeGrupa;
	}

	public void changeGrupa(Long code) {
		this.setCodeGrupa(code);
		actionFind();
	}

	public List<Questions> getQuestListT() {
		return questListT;
	}

	public void setQuestListT(List<Questions> questListT) {
		this.questListT = questListT;
	}

}
