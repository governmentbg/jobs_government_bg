package indexbg.pjobs.bean;

import java.util.ArrayList;
import java.util.Iterator;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;



@Named("errorMsg")
@RequestScoped
public class Errors {
	public Errors() {
	}

	public ArrayList<FacesMessage> getMessages() {
		Iterator<FacesMessage> it = FacesContext.getCurrentInstance()
				.getMessages();
		ArrayList<FacesMessage> msg = new ArrayList<FacesMessage>();
		if (it != null) {
			while (it.hasNext()) {
				msg.add(it.next());
			}
		}
		return msg;
	}

}
