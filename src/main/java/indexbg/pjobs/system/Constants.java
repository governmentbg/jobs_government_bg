package indexbg.pjobs.system;

import com.indexbg.system.SysConstants;

public class Constants extends SysConstants {

		public static final String beanMessages="beanMessages";
		public static final String LABELS="labels";
		
		/**	Нерегистриран потребител  */
		public static final long UNREGISTERED_USER = -100L;
		
		/**	Портал потребител  */
		public static final long PORTAL_USER = -1L;
		
		
		//Системна класификация  - Тип/Вече Вид/ потребител
		/** код на значение Тип потребител - Модератор 
		    Кода е фиктивен(няма го в класификацията) но за да разпознаваме кой е модератор кой адми ни трябва*/
		public static final long CODE_ZNACHENIE_TYPE_USER_MODERATOR = -1; 
		
		
		// Кодове на обекти
		
		/** Потребители */
		public static final long CODE_OBJECTS_USERS = 8L;		
	
		 /**  Групи потребители */ 
		 public static final long CODE_OBJECTS_GROUPUSER = 9L;
		
		/**	ОБЯВА  ЗА РАБОТА/МОБИЛНОСТ  */
		public static final long CODE_OBJECT_ADVERTISEMENT = 19L;
		
		/**	ПУБЛИКАЦИЯ  */
		public static final long CODE_OBJECT_PUBLICATION = 20L;
		
		/**	ПУБЛИКАЦИЯ  LANG*/
		public static final long CODE_OBJECT_PUBL_LANG = 21L;
		
		/**	Стипендиантска програма  */
		public static final long CODE_OBJECT_BURSARY = 22L;		
		
		/**	Стипендиант  */
		public static final long CODE_OBJECT_BURSAR = 25L;
		
		/**	Администрация от адм. регистър  */
		public static final long CODE_OBJECT_ADMINISTRATION = 23L;
		
		public static final long CODE_OBJECT_CONTACT_EMAIL = 24L;
		
		/**	Често задавани въпроси  */
		public static final long CODE_OBJECT_QUESTIONS = 26L;
		
		/**	Контакти  */
		public static final long CODE_OBJECT_CONTACTS = 27L;
		
		/**	Статични текстове  */
		public static final long CODE_OBJECT_STATIC_TEXTS = 28L;
		
		/**	Тестове на потребител  */
		public static final long CODE_OBJECT_USERS_TESTS = 29L;
		
		/** TST - Тестова група  */
		public static final long CODE_OBJECT_TST_TEST_GROUP = 53L;
		
		/** Кампания  */
		public static final long CODE_OBJECT_CAMPAIGN = 55L;
		
		/** Обява за стаж  */
		public static final long CODE_OBJECT_PRACTICE = 56L;
		
		/** Кандидатсване на стаж  */
		public static final long CODE_OBJECT_APPLY = 57L;
		
		// Кодове на системни класификации			
	
		/**	 Код на системна класификация "Да/Не" */
		public static final long CODE_SYSCLASS_YESNO = 26L;
		
		/**	 Код на системна класификация "EKATTE" */
		public static final long CODE_SYSCLASS_EKATTE = 10026L;
		
		/**	 Код на системна класификация "Секции" */
		public static final long CODE_SYSCLASS_SECTION = 10071L;
		
		/**	 Код на системна класификация "Вид обява" */
		public static final long CODE_SYSCLASS_TYPE_ADVERTISEMENT = 10072L;
		
		/**	 Код на системна класификация "Правоотношение" */
		public static final long CODE_SYSCLASS_TREATMENT = 10073L;
		
		/**	 Код на системна класификация "Професионално направление" */
		public static final long CODE_SYSCLASS_PROFESSIONAL_FIELD = 10074L;
		
		/**	 Код на системна класификация "Тип мобилност" */
		public static final long CODE_SYSCLASS_MOBILITY_TYPE = 10075L;
		
		/**	 Код на системна класификация "Длъжностно ниво" */
		public static final long CODE_SYSCLASS_POSITION_LEVEL = 10076L;
		
		/**	 Код на системна класификация "Степен на образование" */
		public static final long CODE_SYSCLASS_EDUCATION_DEGREE = 10077L;
		
		/**	 Код на системна класификация "Професионален опит" */
		public static final long CODE_SYSCLASS_EXPERIENCE = 10078L;
		
		/**	 Код на системна класификация "Ранг" */
		public static final long CODE_SYSCLASS_RANK = 10079L;
		
		/**	 Код на системна класификация "Начини за провеждане на конкурс" */
		public static final long CODE_SYSCLASS_COMPETITION_TYPE = 10080L;
		
		/**	 Код на системна класификация "Специалност" */
		public static final long CODE_SYSCLASS_SUBJECT = 10081L;
		
		/**	 Код на системна класификация "Статус на стипендиантска програма" */
		public static final long CODE_SYSCLASS_STATUS_BURSARY = 10089L;
		
		/**	 Код на системна класификация "Вид администрация" */
		public static final long CODE_SYSCLASS_VID_ADM = 10091L;
		
		/**	 Код на системна класификация "Ниво на тест/сценарий" */
		public static final long CODE_SYSCLASS_NIVO_TEST= 10092L;
		
		/**	 Код на системна класификация "Ниво на тест/сценарий" */
		public static final long CODE_SYSCLASS_MODUL= 10093L;
			
		/**	 Код на системна класификация "Организации от адм.регистър" */
		public static final long CODE_SYSCLASS_ADM_REGISTER = 10100L;
		
		/**	 Код на системна класификация "Кандидат" */
		public static final long CODE_SYSCLASS_APPLY_FOR = 10106L;
		
		/**	 Код на системна класификация "Плоска класификация за разкодиране на отдели и дирекции" */
		public static final long CODE_SYSCLASS_ADM_FLAT = 10107L;
		
		/**	 Код на системна класификация "Секции на публикации" */
		public static final long CODE_SYSCLASS_SECT_PUBL = 10104L;
		
		/**	 Код на системна класификация "Курс" */
		public static final long CODE_SYSCLASS_STUDENT_COURSE = 10111L;
		
		/**	 Код на системна класификация "Езици" */
		public static final long CODE_SYSCLASS_LANGUAGES = 10109L;
		
		/**	 Код на системна класификация "Степен на владеене на език" */
		public static final long CODE_SYSCLASS_LANGUAGE_LEVEL = 10110L;
		
		
		/**	 Код на системна класификация "Длъжности" */
		public static final long CODE_SYSCLASS_JOBS = 10112L;
		
		/**	 Код на системна класификация "Меню на PJobs" */
//		public static final long CODE_SYSCLASS_MENU_PJOBS = 10113L;
		
		/**	 Код на системна класификация "Статус на кандидат за ДСл" */
		public static final long CODE_SYSCLASSIF_CANDIDATE_STATUS = 10117L;
		
		/**	 Код на системна класификация "Статус на кандидат за ДСл" */
		public static final long CODE_SYSCLASSIF_TEST_RESULTS = 10121L;
		
		/**	 Код на системна класификация "Групи на въпроси" */
		public static final long CODE_SYSCLASS_QUEST_GRUPA = 10128L;
		
		/**	 Код на системна класификация "Тип на публикация" */
		public static final long CODE_SYSCLASS_PUBL_TYPE = 10143L;
		
		/**	Код на системна класификация "Статус на е-майл" */
		public static final long CODE_SYSCLASS_STATUS_MAIL = 10144L;
		
		/**	Код на системна класификация "Тип на кампания" */
		public static final long CODE_SYSCLASS_CAMPAIGN_TYPE = 10263L;
		
		/**	Код на системна класификация "Статус на кампания" */
		public static final long CODE_SYSCLASS_CAMPAIGN_STATUS = 10264L;
		
		/**	Код на системна класификация "Статус на обява за стаж" */
		public static final long CODE_SYSCLASS_PRACTICE_STATUS = 10274L;
				
		/**	Код на системна класификация "Статус на кандидат за стаж" */
		public static final long CODE_SYSCLASS_STATUS_CANDIDATE = 10278L;
		
		/**	 Код на системна класификация "Университети" */
		public static final long CODE_SYSCLASS_UNIVERSITY = 10250L;
		
		// Кодове на значения в системни класификации
		
		// Системна класификация "Секции"
		/** код на значение Форма за администратор */
		public static final long CODE_ZNACHENIE_SECTION_ADMIN_FORM = 66L;
		
		//Системна класификация 10072 - Вид обява
		/** код на значение Вид обява - Работа */
		public static final long CODE_ZNACHENIE_TYPE_ADVERTISEMENT_JOB = 1;
		/** код на значение Вид обява - Мобилност */
		public static final long CODE_ZNACHENIE_TYPE_ADVERTISEMENT_MOBILITY = 2;
		
		public static final long CODE_ZNACHENIE_TYPE_GDPR = 42;
		public static final long CODE_ZNACHENIE_TYPE_USLOVIA = 43;
		
		//Системна класификация "Тип на публикация" 
		/** код на значение Материали */
		public static final long CODE_ZNACHENIE_TYPE_PUBL_MATERIALI = 1;
		/** код на значение Изображения */
		public static final long CODE_ZNACHENIE_TYPE_PUBL_IMAGES = 2;
		/** код на значение Видео */
		public static final long CODE_ZNACHENIE_TYPE_PUBL_VIDEO = 3;
		
		
		//Системна класификация 10091 - Вид "Вид администрация"
		/** код на значение Вид обява - Работа */
		public static final long CODE_ZNACHENIE_DRUGA_ADMINISTRACIA = 12L;
		
		//	 Код на системна класификация 10092L - "Ниво на тест/сценарий" 
		/** код на значение Висши държавни служители */
		public static final long CODE_ZNACHENIE_NIVO_TEST_VISHI = 1L;
		/** код на значение Висши Ръководители */
		public static final long CODE_ZNACHENIE_NIVO_TEST_RAKOVODITELI = 2L;
		/** код на значение Експерти */
		public static final long CODE_ZNACHENIE_NIVO_TEST_EKSPERTI = 3L;
		
		//Системна класификация 10089 - "Статус на стипендиантска програма"
		/** код на значение Регистрирана */
		public static final long CODE_ZNACHENIE_STATUS_BURSARY_REG = 1L;
		/** код на значение Одобрена */
		public static final long CODE_ZNACHENIE_STATUS_BURSARY_ODOBRENA = 2L;
		
		public static final long CODE_CONTACT_EMAIL = -0;
		
		
		//Системна класификация 10104 - "Секции на публикации"
		/** код на значение Принципи за ДА и ДСл */
		public static final long CODE_ZNACH_PUBL_PRINZIP_DA_DSL = 8;
		/** код на значение Структура и функции на административните структури */
		public static final long CODE_ZNACH_PUBL_STR_FUNC_ADM_STR = 18;
		/** код на значение Интервюта */
		public static final long CODE_ZNACH_PUBL_INTERVUTA = 22;
		/** код на значение Предимства на работата в ДА */
		public static final long CODE_ZNACH_PUBL_PRED_RAB_V_DA = 21;
		/** код на значение Изисквания към кандидатите за ДСл */
		public static final long CODE_ZNACH_PUBL_REQ_CAND_FOR_DSL = 20;
		/** код на значение Нормативна уредба в областта на държавната служба */
		public static final long CODE_ZNACH_PUBL_NORM_UREDBA_DSL = 19;
		/** код на значение Правила и инструкции */
		public static final long CODE_ZNACH_PUBL_PRAVILA_INSTR = 13;
		/** код на значение Материали за самоподготовка */
		public static final long CODE_ZNACH_PUBL_MATERIAL_SAMOPODG = 14;
		/** код на значение Стипендиантска програма в ДА */
		public static final long CODE_ZNACH_PUBL_STIPEND_PROGR_V_DA = 9;
		/** код на значение Портал за студентски стажове в ДА */
		public static final long CODE_ZNACH_PUBL_STUDENT_STAJOVE_V_DA = 25;
		/** код на значение Програма "Старт в кариерата" */
		public static final long CODE_ZNACH_PUBL_START_V_KARIERATA = 26;
		/** код на значение Програма "Примерен тест" */
		public static final long CODE_ZNACH_PUBL_PRIMEREN_TEST = 15;
		
		//Системна класификация 10106 кандидат за
		/** код на значение Стипендиант / Стажант */
		public static final long CODE_ZNACH_CANDIDATE_BURSARY = 3L;
		
		/** код на значение Одобрен за тест */
		public static final long CODE_ZNACH_CANDIDATE_APPROVED = 1L;
		/** код на значение Длъжности без изискване за опит */
		public static final long CODE_ZNACH_DLAJN_BEZ_OPIT = 58L;
		
		//Системна класификация 10117 - "Статус на кандидат за ДСл"
		/** код на значение Одобрен за тест */
		public static final long CODE_ZNACH_CANDIDATE_STATUS_ODOBREN = 1;
		/** код на значение Разпределен за тест */
		public static final long CODE_ZNACH_CANDIDATE_STATUS_RAZPREDELEN = 2;
		/** код на значение Издържал тест */
		public static final long CODE_ZNACH_TEST_RESULTS_IZDARJ = 4;
		/** код на значение Издържал тест */
		public static final long CODE_ZNACH_TEST_RESULTS_NEIZDARJ = 5;
		/** код на значение Издържал тест */
		public static final long CODE_ZNACH_TEST_RESULTS_NEYAVIL = 6;
		/** код на значение Издържал тест */
		public static final long CODE_ZNACH_TEST_RESULTS_DEAKTIVIRAN = 7;
		
		// Кодове на значения за извикване на шаблони за изпращане на уведомитени съобщения по мейл
		/** код на значение шаблон Допускане до тест до кандидат */
		public static final long CODE_ZNACHENIE_SHABLON_ALLOW_TO_TEST = 1;
		/** код на значение шаблон Определяне дата за тест до кандидат*/
		public static final long CODE_ZNACHENIE_SHABLON_SET_DATE_TEST = 2;
		/** код на значение шаблон Резултат от тест до администратор Човешки ресурси */
		public static final long CODE_ZNACHENIE_SHABLON_TEST_RESULT_TO_ADMIN = 3;
		/** код на значение шаблон Резултат от тест до кандидат */
		public static final long CODE_ZNACHENIE_SHABLON_TEST_RESULT_TO_CANDIDATE = 4;
		/** код на значение шаблон Абонамент за обяви до кандидат */
		public static final long CODE_ZNACHENIE_SHABLON_SUBSCRIBE_TO_ADVER = 5;
		/** код на значение шаблон Абонамент за обява за мобилност до кандидат */
		public static final long CODE_ZNACHENIE_SHABLON_SUBSCRIBE_TO_ADVER_MOBIL = 6;
		/** код на значение шаблон Изтриване на потребител */
		public static final long CODE_ZNACHENIE_SHABLON_DELETE_USER = 10;
		/** код на значение шаблон Кандидатстване за обява за стаж */
		public static final long CODE_ZNACHENIE_SHABLON_PRACTICE_APPLY = 11;
		/** код на значение шаблон Одобрен за интервю */
		public static final long CODE_ZNACHENIE_SHABLON_APPROVED_FOR_INTERVIEW = 12;
		/** код на значение шаблон Промяна на дата и адрес за интервю */
		public static final long CODE_ZNACHENIE_SHABLON_CHANGE_INTERVIEW_DATE_ADDRESS = 13;
		/** код на значение шаблон Класиран на първо класиране */
		public static final long CODE_ZNACHENIE_SHABLON_RANKED_FIRST_RANKING = 14;
		/** код на значение шаблон Класиран на второ класиране */
		public static final long CODE_ZNACHENIE_SHABLON_RANKED_SECOND_RANKING = 15;
		/** код на значение шаблон Приемане на стаж от администратор */
		public static final long CODE_ZNACHENIE_SHABLON_ACCEPT_PRACTICE_FROM_ADMIN = 16;
		/** код на значение шаблон Некласиран кандидат */
		public static final long CODE_ZNACHENIE_SHABLON_NOT_RANKED_CANDIDATE = 17;
		
		//Системна класификация 10121 - "Резултат от тест"
		/** код на значение Издържал тест преименуван на "Изпълнил тест" 10.01.2022,защото вече няма праг за издържал или неиздържал-
		 * всеки,който се е явил и е завършил теста е изпълнил теста,дори да има 0 точки*/ 
		public static final long CODE_ZNACH_TEST_RESULTS_IZDARJAL = 4;
		/** код на значение "Издържал тест" - това няма  да се ползва повече (засега)/* 
		 */
		public static final long CODE_ZNACH_TEST_RESULTS_NEIZDARJAL = 5;
		/** код на значение "Неявил се на тест" */
		public static final long CODE_ZNACH_TEST_RESULTS_NEYAVILSE = 6;
		/** код на значение "Деактивиран тест" */
		public static final long CODE_ZNACH_TEST_RESULTS_DEAKTIV = 7;
		
		//Системна класификация 10144 - Статус на е-майл
		/** код на значение Статус на е-майл - Неизпратен */
		public static final long CODE_ZNACHENIE_STATUS_MAIL_NOT_SEND = 1;
		/** код на значение Статус на е-майл - Изпратен */
		public static final long CODE_ZNACHENIE_STATUS_MAIL_SEND = 2;
		/** код на значение Статус на е-майл - Грешка */
		public static final long CODE_ZNACHENIE_STATUS_MAIL_ERROR = 3;
		
		 
		//Системна класификация 10143 - Тип публикация
		/** код на значение Статии */
		public static final long CODE_ZNACHENIE_TYPE_STATII = 1;
		/** код на значение Изображения */
		public static final long CODE_ZNACHENIE_TYPE_IMAGE = 2;
		/** код на значение Видео */
		public static final long CODE_ZNACHENIE_TYPE_VIDEO = 3;
		
		//	Системна класификация 10263 "Тип на кампания" */
		/** код на значение Централизирана */
		public static final long CODE_ZNACHENIE_TYPE_CAMPAIGN_CENTR = 1;
		/** код на значение Децентрализирана */
		public static final long CODE_ZNACHENIE_TYPE_CAMPAIGN_DECENTR = 2;
		
		//	Системна класификация 10264 "Статус на кампания" */
		/** код на значение Активна */
		public static final long CODE_ZNACHENIE_CAMPAIGN_STATUS_ACTIVE = 1;
		/** код на значение В процес на разработка */
		public static final long CODE_ZNACHENIE_CAMPAIGN_STATUS_V_PROCES = 2;
		/** код на значение Изтекла */
		public static final long CODE_ZNACHENIE_CAMPAIGN_STATUS_IZTEKLA = 3;
		
		//	Системна класификация 10274L "Статус на обява за стаж" */
		public static final long CODE_ZNACHENIE_PRACTICE_STATUS_ACTIVE = 1;
		
		//	Системна класификация 10278 "Статус на кандидат за стаж" */
		/** код на значение Кандидатствал */
		public static final long CODE_ZNACHENIE_STATUS_CAND_APPLY = 1;
		/** код на значение Класиран */
		public static final long CODE_ZNACHENIE_TYPE_STATUS_CAND_RANKED = 2;
		/** код на значение Приел стаж */
		public static final long CODE_ZNACHENIE_TYPE_STATUS_CAND_ACCEPTED = 3;		
		/** код на значение Одобрен за интервю */
		public static final long CODE_ZNACHENIE_TYPE_STATUS_APPROVED_FOR_INTERVIEW = 4;
	
	
		// Класификация 'Статус потребител' - класификация CODE_CLASSIF_STATUS_POTREB (класификация 9)
		
		public static final long    CODE_ZNACHENIE_OCHAKV_REG     = 1L;    // В процес (очакване) на регистрация   
		public static final long    CODE_ZNACHENIE_ACT            = 2L;    // Активен потребител
		public static final long    CODE_ZNACHENIE_ZAKL           = 3L;    // Заключен потребител
		public static final long    CODE_ZNACHENIE_NO_ACT         = 4L;    // Неактивен потребител		 
		

}
