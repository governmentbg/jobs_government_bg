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
import com.indexbg.system.db.dto.SystemClassif;
import com.indexbg.system.exceptions.DbErrorException;
import com.indexbg.system.utils.JSFUtils;

import indexbg.pjobs.system.Constants;
import indexbg.pjobs.system.PJobsBean;

@Named
@ViewScoped
public class GameBean extends PJobsBean {
	
	private static final long serialVersionUID = -2339637246090547808L;
	
	static final Logger LOGGER = LoggerFactory.getLogger(GameBean.class);
	
	private final long SYSCLASS_GAME = 10129L;
	
	private List<SystemClassif> choice1;
	private List<SystemClassif> choice2;
	private List<SystemClassif> choice3;
	private boolean buttonDisabled = true;
	private String question;

	private int progress = 0;
	private int stage = 1;
	private int code1;
	
	@PostConstruct
	public void initData(){
		loadChoice1();
	}
	
	private void loadChoice1() {
		try {
			choice1 = new ArrayList<SystemClassif>();
			choice1.addAll(getSystemData().getChildrenOnNextLevel(SYSCLASS_GAME, 0, new Date(), getCurrentLang(), null));
			question = getMessageResourceString("labels", "game.q1") + ":";
			choice2 = null;
			choice3 = null;
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		} catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	private void loadChoice2(int code) {
		try {
			choice2 = new ArrayList<SystemClassif>();
			choice2.addAll(getSystemData().getChildrenOnNextLevel(SYSCLASS_GAME, code, new Date(), getCurrentLang(), null));
			choice1 = null;
			choice3 = null;
			question = getMessageResourceString("labels", "game.q2") + ":";
			progress = 50;
			stage = 2;
			buttonDisabled = false;
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		} catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	private void loadChoice3(int code) {
		try {
			choice3 = new ArrayList<SystemClassif>();
			choice3.addAll(getSystemData().getChildrenOnNextLevel(SYSCLASS_GAME, code, new Date(), getCurrentLang(), null));
			choice1 = null;
			choice2 = null;
			question = getMessageResourceString("labels", "game.q3") + ":";
			progress = 100;
			stage = 3;
			buttonDisabled = false;
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages, "general.errDataBaseMsg"));
		} catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(Constants.beanMessages,"general.unexpectedResult"));
			LOGGER.error(e.getMessage(), e);
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	public void step(int code) {
		if(stage == 1) {
			code1 = code;
			loadChoice2(code1);
		} 
		else if(stage == 2) {
			loadChoice3(code);
		}
	}
	
	public void back() {
		if(stage == 2) {
			loadChoice1();
			progress = 0;
			stage = 1;
			buttonDisabled = true;
		}
		else if(stage == 3) {
			loadChoice2(code1);
			progress = 50;
			stage = 2;
			buttonDisabled = false;
		}
	}

	public List<SystemClassif> getChoice1() {
		return choice1;
	}

	public void setChoice1(List<SystemClassif> choice1) {
		this.choice1 = choice1;
	}

	public List<SystemClassif> getChoice2() {
		return choice2;
	}

	public void setChoice2(List<SystemClassif> choice2) {
		this.choice2 = choice2;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isButtonDisabled() {
		return buttonDisabled;
	}

	public void setButtonDisabled(boolean buttonDisabled) {
		this.buttonDisabled = buttonDisabled;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public List<SystemClassif> getChoice3() {
		return choice3;
	}

	public void setChoice3(List<SystemClassif> choice3) {
		this.choice3 = choice3;
	}

}

