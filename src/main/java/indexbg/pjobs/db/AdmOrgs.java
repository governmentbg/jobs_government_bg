package indexbg.pjobs.db;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.indexbg.system.db.TrackableEntity;


@Entity
@Table(name = "ADM_ORGS")
public class  AdmOrgs extends TrackableEntity{
	



	/**
	 * 
	 */
	private static final long serialVersionUID = 5537854521173079467L;

	
		@SequenceGenerator(name = "AdmOrgs", sequenceName = "SEQ_ADM_ORGS", allocationSize = 1)
		@Id
		@GeneratedValue(strategy = SEQUENCE, generator = "AdmOrgs")
		@Column(name = "ID", nullable = false, precision = 10, scale = 0)
		private Long id;

		@Column(name = "ORG_NAME", length = 255)
		private String orgName; 
		
		@Column(name = "EIK", length = 255)
		private String eik;

		@Column(name = "EKATTE", precision = 10, scale = 0)
		private Long ekatte;

		@Column(name = "EMAIL", length = 50)
		private String email;

		@Column(name = "PHONE", length = 50)
		private String phone;

		
		@Column(name = "WEBSITE", length = 100)
		private String website ; 

		@Column(name = "ADDRESS", length = 255)
		private String adress;

		/** default constructor */
		public AdmOrgs() {
			
		}

		/**
		 *
		 * @param id
		 * @param orgName
		 * @param eik
		 * @param ekatte
		 * @param email
		 * @param phone
		 * @param website
		 * @param adress
		 */
		public AdmOrgs(Long id, String orgName, String eik, Long ekatte, String email, String phone, String website,
				String adress) {
			

			this.id = id;
			this.orgName = orgName;
			this.eik = eik;
			this.ekatte = ekatte;
			this.email = email;
			this.phone = phone;
			this.website = website;
			this.adress = adress;
		}
		

		/** @return the id */
		@Override
		public Long getId() {
			return this.id;
		}
	
		/** @return the orgName */
		public String getOrgName() {
			return this.orgName;
		}

		/** @return the eik */
		public String getEik() {
			return this.eik;
		}

		/** @return the ekatte */
		public Long getЕkatte() {
			return this.ekatte;
		}

		/** @see PersistentEntity#getCodeMainObject() */
		@Override
		public Long getCodeMainObject() {
			// IMPLME getCodeMainObject
			return null;
		}

		/** @return the email */
		public String getEmail() {
			return this.email;
		}

		/** @return the phone */
		public String getPhone() {
			return this.phone;
		}

		/** @return the website */
		public String getWebsite() {
			return this.website;
		}

		/** @return the adress */
		public String getAdress() {
			return this.adress;
		}
		
		/** @return the ekatte */
		public Long getEkatte() {
			return this.ekatte;
		}

		/**
		 * @param id
		 */
		public void setId(Long id) {
			this.id = id;
		}

		
		/**
		 * @param orgName
		 */
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}

		/**
		 * @param eik
		 */
		public void setEik(String eik) {
			this.eik = eik;
		}

		/**
		 * @param ekatte
		 */
		public void setEkatte(Long ekatte) {
			this.ekatte = ekatte;
		}

		/**
		 * @param email
		 */
		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * @param phone
		 */
		public void setPhone(String phone) {
			this.phone = phone;
		}

		/**
		 * @param website
		 */
		public void setWebsite(String website) {
			this.website = website;
		}

		/**
		 * @param adress
		 */
		public void setAdress(String adress) {
			this.adress = adress;
		}	
		
}
