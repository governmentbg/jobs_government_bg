"use strict"
$(document).ready(function() {
	// Stores true if the window is >991px; 
	// Used in the window resize listener;
	let wideScreen;
	let threshold = 992;
	
	// Determine if the window is large or small when page loads;
	(function() {
		if(window.innerWidth < threshold) {
			$("nav.navbar").removeClass("navbar-main");
			$("nav.navbar").addClass("navbar-mobile");
			enableToggling();
		}
		
		wideScreen = window.innerWidth >= threshold;
	})();
	
	// Listen to window resizing after the page has loaded;
	window.addEventListener("resize", function() {
		// Switch to mobile menu;
		if(window.innerWidth < threshold && wideScreen) {
			wideScreen = false;
			$("nav.navbar").removeClass("navbar-main");
			$("nav.navbar").addClass("navbar-mobile");

			enableToggling()
		}
		// Switch to normal menu;
		else if(window.innerWidth >= threshold && !wideScreen) {
			wideScreen = true;
			$("nav.navbar").removeClass("navbar-mobile");
			$("nav.navbar").addClass("navbar-main");
			$(".navbar .navbar-content").css("display", "");
			
			disableToggling();
			resetDisplay();			
		}
		
		if(window.innerWidth > 640) {
			if($('.main-icons-container').hasClass('opened')) {
				toggleNavbarOptions();
			}
		}
	});	
    
    /***********************************************/
    /* Mobile navbar toggle button                 */
    /***********************************************/
    let rotateInterval;
    let degrees = 0;
    $(".navbar-toggle-button").click(function() {
        let $navbar = $(".navbar-mobile .navbar-content");
        if($navbar.css("display") === "none") {
            $navbar.slideDown();
        }
        else {
            $navbar.slideUp();
        }

        clearInterval(rotateInterval);
        rotateInterval = setInterval(rotate180, 10);
    });

    function rotate180() {
        degrees += 15;
        $(".navbar-mobile .navbar-toggle-button").css("transform", "rotate(" + degrees + "deg)");
        $(".navbar-mobile .navbar-toggle-button").css("-webkit-transform", "rotate(" + degrees + "deg)");
        $(".navbar-mobile .navbar-toggle-button").css("-o-transform", "rotate(" + degrees + "deg)");
        $(".navbar-mobile .navbar-toggle-button").css("-moz-transform", "rotate(" + degrees + "deg)");

        if(degrees == 180) {
            clearInterval(rotateInterval);
            degrees = 0;
        }
     }


    /***********************************************/
    /* Mobile navbar expanding submenus            */
    /***********************************************/
    function enableToggling() {
    	// Enable clicking on the main navigation items;
	    $(".navbar-mobile .navbar-item-main").click(function(event) {
	        let $dropdown = $(this).children(".navbar-dropdown");
	        if($dropdown.css("display") == "none") {
	            
	        	// Close all opened menus before opening the clicked one;
	            $(".navbar-mobile .navbar-item-main .navbar-dropdown").each(function() {
	                if(this != $dropdown) {
	                    $(this).slideUp();
	                    $(this).find(".navbar-dropdown-item.expand").removeClass("expanded");
	                }
	            });
	            $dropdown.slideDown();
	        }
	        else {
	            $dropdown.slideUp();
	            $(this).find(".navbar-dropdown-item.expand").removeClass("expanded");
	        }
	    });

	    // Enable clicking on submenus
	    $(".navbar-mobile .navbar-dropdown .navbar-dropdown-item.expand").click(function() {
	        let $dropdown = $(this).children(".navbar-dropdown-submenu");
	
	        if($dropdown.css("display") == "none") {
	            // Close all other opened submenus first;
	        	//$(".navbar-mobile .navbar-dropdown .navbar-dropdown-item.expand").each(function() {
	            $(this).siblings(".navbar-dropdown-item.expand").each(function() {
	                $(this).removeClass("expanded");
	                let subDropdown = $(this).children(".navbar-dropdown-submenu");
	            	if(subDropdown != $dropdown) {
	                    $(subDropdown).slideUp();
	                }
	            });
	            
	            $(this).find(".navbar-dropdown-item.expand").removeClass("expanded").children(".navbar-dropdown-submenu").slideUp();
	
	            $dropdown.slideDown();
	            $(this).addClass("expanded");
	        }
	        else {
	            $dropdown.slideUp();
	            $(this).removeClass("expanded");
	        }
	
	    });
    }
    
    // If a submenu is clicked, stop propagation of the click to the parent;
    $(".navbar-mobile .navbar-dropdown").click(function(event) {
    	event.stopPropagation();
    });
    
    // Disable toggling of the menus when switching to larger screen;
    function disableToggling() {
    	$(".navbar-main .navbar-item-main").unbind("click");
    	$(".navbar-main .navbar-dropdown .navbar-dropdown-item.expand").unbind("click");
    }
    
    // Reset the display property of all items when switching to larger screen;
    function resetDisplay() {
    	$(".navbar .navbar-dropdown").css("display", "");
    	$(".navbar .navbar-dropdown-item.expand").removeClass("expanded");
    }
});

//Prevent dropdowns from getting off screen when hovered
$(".navbar-main .navbar-item-main").mouseenter(function() {
	let dropdown = $(this).children(".navbar-dropdown");
	if(dropdown.length > 0) {
		let width = $(dropdown).width();
		let left = $(this).offset().left;
		if(left + width > window.innerWidth) {
			$(dropdown).css({"left": (window.innerWidth - (left + width + 18)) + "px"}); // scrollbar size: 17px
		}
		else {
			$(dropdown).css({"left": "0px"});
		}
	}
});

$(".navbar-main .navbar-dropdown-item.expand").mouseenter(function(){
	let dropdown = $(this).children(".navbar-dropdown-submenu");
	let width = $(dropdown).width();
	let parentLeft = $(this).offset().left;
	let parentWidth = $(this).width();
	if(parentLeft + parentWidth + width > window.innerWidth) {
		$(dropdown).css({"left": -width + "px"}); // scrollbar size: 17px
	}
	else {
		$(dropdown).css({"left": "100%"});
	}
});

function toggleNavbarOptions() {
	$('.main-icons-container').toggleClass('opened');
	$('.options-toggle').toggleClass('opened');
}