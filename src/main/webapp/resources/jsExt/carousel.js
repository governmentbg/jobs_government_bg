/**
 * Carousel елементът, с който се превърта наляво/надясно списък с панели по екрана
 */
"use strict"
$(document).ready(function() {
	
	scaleCarousels();
	attachClickListeners();
		window.addEventListener("resize", function() {
			scaleCarousels();
			removeClickListeners();
			attachClickListeners();
			setArrows();
	});
	setArrows();

});

function scaleCarousels() {
	$(".carousel").each(function() {
		let carousel = this;
		
		// calculate the count of the items and size of a single one
		let itemCount = $(carousel).find(".carousel-item").length;
		let $item = $(carousel).find(".carousel-item").first();
		let itemWidth = parseInt($item.css("width"), 10);
		itemWidth += parseInt($item.css("margin-left"), 10);
		itemWidth += parseInt($item.css("margin-right"), 10);
	
		// set the size of the .contents-container element - max 97%
		let maxWidth = Math.floor(parseInt($(carousel).css("width"), 10) * 0.97);
		if((itemCount * itemWidth) < maxWidth) {
			$(carousel).find(".contents-container").css("width", (itemCount * itemWidth) + "px");
		}
		else {
			$(carousel).find(".contents-container").css("width", (maxWidth - (maxWidth % itemWidth)) + "px");
		}
		
		// calculate the size of all items ordered next to each other
		let allItemsWidth = 0;
		$(carousel).find(".carousel-item").each(function(){
			allItemsWidth += parseInt($(this).css("width"), 10);
			allItemsWidth += parseInt($(this).css("margin-left"), 10);
			allItemsWidth += parseInt($(this).css("margin-right"), 10);
		});
		
		/* 
		 * Set size of the .contents-wide element;
		 * flex MUST be after width!
		 */
		let $contentsWide = $(carousel).find(".contents-wide");
		$contentsWide.css("width", allItemsWidth + "px");
		$contentsWide.css("display", "flex");
		$contentsWide.css("justify-content", "center");
		
		// reset position to first item
		$contentsWide.css("left", "0px");	
		
		// calculate number of pages displayed below
		let visibleWidth = parseInt($(carousel).find(".contents-container").css("width"));
		let wholeWidth = parseInt($(carousel).find(".contents-wide").css("width"));
		let pagesCount = 1;
		pagesCount += Math.floor((wholeWidth - visibleWidth) / itemWidth);
		
		/* 
		 * Create the pagination dots;
		 */
		let pageIndex = 1;
		let $dotsContainer = $(carousel).find(".carousel-dots");
		$dotsContainer.empty();
		for(let i = 0; i < pagesCount; i++) {
			$dotsContainer.append("<div class='dot'></div>");
		}
		$dotsContainer.children().first().addClass("active");
	});
}

function attachClickListeners() {
	$(".carousel").each(function() {
		let carousel = this;
		let $dotsContainer = $(carousel).find(".carousel-dots");
		let $contentsWide = $(carousel).find(".contents-wide");
		let itemCount = $(carousel).find(".carousel-item").length;
		let $item = $(carousel).find(".carousel-item").first();
		let itemWidth = parseInt($item.css("width"), 10);
		itemWidth += parseInt($item.css("margin-left"), 10);
		itemWidth += parseInt($item.css("margin-right"), 10);
		let pagesCount = $dotsContainer.children(".dot").length;
		let leftAnimating = false;
		let rightAnimating = false;
		let currentPageIndex = 0;
		
		/* 
		 * Set click listener for the dots
		 */
		$dotsContainer.children().each(function(index, dot) {
			$(dot).bind("click", function() {
				if(index != currentPageIndex) {
					$dotsContainer.children().eq(currentPageIndex).removeClass("active");
					$(dot).addClass("active");
					
					$contentsWide.animate({left: -(index * itemWidth) + "px"}, 500, "easeOutExpo", function() {
						rightAnimating = false;
					});
					
					currentPageIndex = index;
					
					if(currentPageIndex == 0) {
						$(carousel).find(".slider-left").addClass("disabled");
					}
					if(currentPageIndex == pagesCount - 1) {
						$(carousel).find(".slider-right").addClass("disabled");
					}
					if(currentPageIndex > 0) {
						$(carousel).find(".slider-left").removeClass("disabled");
					}
					if(currentPageIndex < pagesCount - 1) {
						$(carousel).find(".slider-right").removeClass("disabled");
					}
				}
				
			});
		});
		
		
		/*
		 * Set click listeners for the left/right arrows;
		 * Listener does not run again until the current animation is complete;
		 */
		
		$(carousel).find(".slider-left, .slider-right").bind("click", function() {
			
			let currentValue = parseInt($contentsWide.css("left"), 10);
	
			// click the left arrow 
			if($(this).hasClass("slider-left") && !leftAnimating) {
				if(currentPageIndex == 0) {
					
					leftAnimating = true;
					$contentsWide.animate({left: (currentValue + 6) + "px"}, 200, "easeOutCirc", function() {
						$contentsWide.animate({left: (currentValue) + "px"}, 100, "easeOutCirc", function() {
							leftAnimating = false;
						});
					});
					
					return;
				}
				else {
					leftAnimating = true;
					$contentsWide.animate({left: (currentValue + itemWidth) + "px"}, 500, "easeOutExpo", function() {
						leftAnimating = false;
					});
					
					$dotsContainer.children().eq(currentPageIndex).removeClass("active");
					currentPageIndex--;
					$dotsContainer.children().eq(currentPageIndex).addClass("active");
					
					// disable left arrow, because we're on first page
					if(currentPageIndex == 0) {
						$(this).addClass("disabled");
					}
					
					//enable right arrow
					if(pagesCount > 1) {
						$(carousel).find(".slider-right").removeClass("disabled");
					}
				}
			}
			
			// click the right arrow
			if($(this).hasClass("slider-right") && !rightAnimating) {
				if(currentPageIndex == pagesCount - 1) {
										
					rightAnimating = true;
					$contentsWide.animate({left: (currentValue - 6) + "px"}, 200, "easeOutCirc", function() {
						$contentsWide.animate({left: (currentValue) + "px"}, 100, "easeOutCirc", function() {
							rightAnimating = false;
						});
					});
					
					return;
				}
				else {
					rightAnimating = true;
					$contentsWide.animate({left: (currentValue - itemWidth) + "px"}, 500, "easeOutExpo", function() {
						rightAnimating = false;
					});
					
					$dotsContainer.children().eq(currentPageIndex).removeClass("active");
					currentPageIndex++;
					$dotsContainer.children().eq(currentPageIndex).addClass("active");
					
					// disable right arrow, because we're on last page
					if(currentPageIndex == pagesCount - 1) {
						$(this).addClass("disabled");
					}
					
					//enable left arrow
					if(pagesCount > 1) {
						$(carousel).find(".slider-left").removeClass("disabled");
					}
				}
			}
	
		});
	});
}

function removeClickListeners() {
	$(".carousel").each(function() {
		
		let $dotsContainer = $(this).find(".carousel-dots");
		$dotsContainer.children().each(function(index, dot) {
			$(dot).unbind("click");
		});
		
		
		$(this).find(".slider-left, .slider-right").unbind("click");
		
	});
}

function setArrows() {
	$(".carousel").each(function() {
		
		let carousel = this;
		let $dotsContainer = $(carousel).find(".carousel-dots");
		let pagesCount = $dotsContainer.children(".dot").length;		
		
		$(carousel).find(".slider-left").addClass("disabled");
		if(pagesCount == 1) {
			$(carousel).find(".slider-right").addClass("disabled");
		}
		else {
			$(carousel).find(".slider-right").removeClass("disabled");
		}
	});
}