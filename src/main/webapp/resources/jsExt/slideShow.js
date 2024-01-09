"use strict"
$(document).ready(function() {

    let imageIndex = 0;
    let play = false;
    let animSpeed = 500; //  скоростта на анимацията [ms]; да бъде < (animDelay / 2)
    let animDelay = 6000; // колко често да се сменят [ms]
    
    if($(".sliding-image").length > 1) {
    	startSlideShow();
    }
    
    window.onfocus = function() {
    	if($(".sliding-image").length > 1) {
    		startSlideShow();
    	}
    }

    window.onblur = function() {
        stopSlideShow();
    }
    
    
    function slide() {
 
       	// move current image out of the view
    	let $imageHide = $($(".sliding-image")[imageIndex]);
		
		// move next image into view
		imageIndex = (imageIndex + 1) % ($(".sliding-image").length);
		let $imageShow = $($(".sliding-image")[imageIndex]);
		
		// after previous image is hidden, put it back on right side
		let prevIndex = (imageIndex + ($(".sliding-image").length) - 1) % ($(".sliding-image").length); 
		let imageReset = $($(".sliding-image")[prevIndex]);
				
		
    	$imageHide.animate({right: "100%"}, animSpeed);
    	$imageHide.css("z-index", "-1");
		
		$imageShow.animate({left: "0"}, animSpeed);
		$imageShow.css("z-index", "0");
		
		setTimeout(function() {
			$(imageReset).css({"left" : "100%", "z-index" : "0"});
		}, animDelay / 2);
		
		
		if(play) {
			setTimeout(slide, animDelay);
		}
		
    }
    
    function startSlideShow() {
    	console.log('starting slideshow');
    	if(!play) {
    		play = true;
    		setTimeout(slide, animDelay);
    	}
    }
    
    function stopSlideShow() {
    	console.log('stopping slideshow');
    	play = false;
    }
});