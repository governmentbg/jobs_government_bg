let size = localStorage.getItem('font');
if (size) {
	$("body").css({ fontSize: size + 'px'});
}

function font(increase) {
  let body =  document.querySelector('body');
  let wrapper =  document.querySelector('.wrapper');
  let size = window.getComputedStyle(body).getPropertyValue('font-size').replace('px', '');
  let max = 20;
  let min = 10;
  if (increase) {
    size <= max ? size++ : '';
  } else {
    size >= min ? size-- : '';
  }
  $("body").css({ fontSize: size + 'px'});
  localStorage.setItem('font', size);
}

function resetFont() {
	$("body").css({fontSize: ""});
	localStorage.removeItem('font');
}

function colorBlind() {
	document.querySelector('body').classList.toggle('color-blind');

	let path = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)); // /PJobs2

	if(document.cookie.match(/^(.*;)?\s*colorBlind\s*=\s*[^;]+(.*)?$/)) {
		document.cookie = "colorBlind=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=" + path;
	}
	else {
		let d = new Date();
		d.setTime(d.getTime() + (365*24*60*60*1000));
		let cookieColor = "colorBlind=true; path=" + path + "; expires=" + d.toUTCString();
		document.cookie = cookieColor;
	}
  setContentHeight();
}

function readedDopInfo(labelParam) {
  let dopinfo = localStorage.getItem(labelParam);
  if(dopinfo)  {
	  localStorage.removeItem(labelParam);
  } else {
	  localStorage.setItem(labelParam, 'false');
  }
}

function cookiesAccepted(labelParam) {
	localStorage.setItem(labelParam, 'true');
	$('#cookies').animate({opacity: 0, height: 0, padding: 0}, 500); 
}

function attachTabClickListeners() {
	window.addEventListener("resize", resizeTabs, false);
	resizeTabs();
	
	$(".tab-layout").each(function() {
		let tabLayout = this;
		$(tabLayout).find(".tab").bind("click", function() {
			if(!$(this).hasClass("active")) {
				let id = $(this).attr("id");
				let idParts = id.split("-");
				let contentsId = "tab-content-" + idParts[1];

				$(tabLayout).find(".tab.active").removeClass("active");
				$(this).addClass("active");

				$(tabLayout).find(".column-contents").slideUp(function() {
					// hide currently active content
					$(tabLayout).find(".tab-content.active").toggle().removeClass("active");
					// show new content
					$(tabLayout).find("#" + contentsId).slideDown().addClass("active");
					$(tabLayout).find(".column-contents").slideDown();
				});
			}
		});
		
		$(this).find(".tab").first().trigger("click");
	});
}

function resizeTabs() {
	if(window.innerWidth <= 991) {
		$(".tab-layout").each(function() {
			let tabLayout = this;
			let tabCount = $(this).find(".tab").length;
			let width = 100 / tabCount;
			$(this).find(".tab").css("width", width + "%");
		});
	}
	else {
		$(".tab-layout").each(function() {
			$(this).find(".tab").css("width", "");
		});
	}
}

function detectBrowserIE() {
	var ua = window.navigator.userAgent;
	
	var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        //console.log("IE 10: ", parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10));
        return true;
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        // var rv = ua.indexOf('rv:');
        // console.log("IE 11: ", parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10));
        return true;
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
       // Edge (IE 12+) => return version number
       // console.log("Edge: ", parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10));
       return false;
    }

    // other browser
    // console.log("Other");
    return false;
}

function fitImagesIE() {
	if(detectBrowserIE() || true) {
		$(".fittable-image").each(function() {
			let src = $(this).attr("src");
			$(this).attr("src", "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='240' height='135'%3E%3C/svg%3E");
			$(this).css("background-image", "url(" + src + ")");
			$(this).css("background-position-x", "center");
			$(this).css("background-position-y", "center");
			$(this).css("background-repeat", "no-repeat");
			$(this).css("background-origin", "content-box");
			$(this).css("background-size", "cover");
		});
	}
}

/**
 * Show ajax status
 * @param show
 */
function ajaxStatus(show){
	if (show) {
		$(".ajax-status-wrapper").css("display", "block");
	}else{
		$(".ajax-status-wrapper").css("display", "none");
	}
}




/**
 * SlideShowImage
 * 
 * */
var slideIndex = 1;

function plusSlides(n) {
  showSlides(slideIndex += n);
}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("hideImage");

  if (n > slides.length) {slideIndex = 1}    
  if (n < 1) {slideIndex = slides.length}
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none";  
  }
  slides[slideIndex-1].style.display = "block";  

}

function setContentHeight() {
	let windowHeight = $(window).height();
	windowHeight -= $("header").outerHeight(true);
	windowHeight -= $("nav.navbar").outerHeight(true);
	windowHeight -= $("footer").outerHeight(true);
	windowHeight -= parseInt($(".page-content").css("margin-top"), 10);
	windowHeight -= parseInt($(".page-content").css("margin-bottom"), 10);
	$(".page-content").css("min-height", windowHeight + "px");
}


function addCert(){
	   $.ajax({
        type:"POST",
        url: location.origin + "/AAServer/api/cert/addCert",
        success: function(data) {
	           console.log(data);
	           if(data){
	        	   alert(data);
	           }else{
	        	   loadCerts();
	           }
        },
        error: function (xhr, status, errorThrown) {
     	   alert(xhr.status + ":" + xhr.responseText);
	       }
	   });
}

function loadCerts(){
	   $.ajax({
        type:"POST",
        url: location.origin + "/AAServer/api/cert/getUserCerts",
        success: function(data) {
	           console.log(data);
	           if(data){
	        	   certArray = JSON.parse(data);
		           var $table = $('.cert_table tbody');
		           $table.html("");
		           for(var cert in certArray){
		        	   $table.append("<tr>" + 
		        	   "<td>" + certArray[cert]['issuer'] + "</td>" +
		        	   "<td>" + certArray[cert]['exp_date'] + "</td>" +
		        	   "<td>" + certArray[cert]['active'] + "</td>" +
		        	   "<td><a data-id='" + certArray[cert]['id'] + "' class='waves-effect waves-light btn-flat' onclick='delCert(this)'><i class='material-icons'>clear</i></a></td>" +
		        	   "</tr>");
		           }
	           }  
        },
        error: function (xhr, status, errorThrown) {
     	   alert(xhr.status + ": Грешка в системата");
	       }
	   });
}

function delCert(button){
	   var id = $(button).data("id");
	   $.ajax({
        type:"POST",
        url: location.origin + "/AAServer/api/cert/delCert",
        data: {"id": id},
        success: function(data) {
	           console.log(data);
	           $(button).parent().parent().remove();
        },
        error: function (xhr, status, errorThrown) {
     	   alert(xhr.status + ": Грешка в системата");
	       }
	   });
}


/* Това се слага, за да може в календара на PrimeFaces като зададеш locale="bg" да се заредят месеците, дните от седмиците и часа да излязат на български  */

PrimeFaces.locales['bg'] =
{
    closeText: 'Затваряне',
    prevText: 'Предишен',
    nextText: 'Следващ',
    currentText: 'Start',
    monthNames: ['Януари', 'Февруари', 'Март', 'Април', 'Май', 'Юни', 'Юли', 'Август', 'Септември', 'Октомври', 'Ноември', 'Декември'],
    monthNamesShort: ['Ян', 'Фев', 'Март', 'Апр', 'Май', 'Юни', 'Юли', 'Авг', 'Сеп', 'Окт', 'Ноем', 'Дек'],
    dayNames: ['Неделя', 'Понеделник', 'Вторник', 'Сряда', 'Четвъртък', 'Петък', 'Събота'],
    dayNamesShort: ['Нед', 'Пон', 'Вт', 'Ср', 'Чтв', 'Пт', 'Съб'],
    dayNamesMin: ['Нд', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
    weekHeader: 'Седмица',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Само време',
    timeText: 'Време',
    hourText: 'Час',
    minuteText: 'Минути',
    secondText: 'Секунди',
    currentText: 'Днес',
    ampm: false,
    month: 'Месец',
    week: 'Седмица',
    day: 'Ден',
    allDayText: 'Цял ден'
};

/*Позволява писанетото само на цифри и знаци в полето + цифри!*/
function onlyLatWithNumbers(id,message){
	var x = document.getElementById(id);
	var z = x.value;
	var regexp = /[\u0400-\u04FF]/gi;// cyrilic letters
	var regexp1 = /[\u0041-\u007a]/gi;//latin letters
	if(z.match(regexp) != null || z.match(regexp1) != null){
		x.value = "";
		x.focus(); 
		alert(message);
	}
}

function highlightElement(el ,elType){
	$(el).closest(elType).siblings(elType).removeClass('ui-state-highlight');
	$(el).closest(elType).toggleClass('ui-state-highlight',true);
}

/*
 * Когато се извика, скролва екрана нагоре до p:messages съобщенията.
 */
var scrolling = false;
function scrollToErrors() {
	if(!scrolling) {
		scrolling = true;
		setTimeout(function() {
		    $([document.documentElement, document.body]).animate({scrollTop: $("#messagesGl").scrollTop()}, 666, "swing", function() {scrolling = false;});
	    });
	}
}