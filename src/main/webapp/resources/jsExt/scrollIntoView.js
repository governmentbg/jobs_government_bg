(function(){
	window.addEventListener('scroll', throttle(function() {
		elementFromTop(document.querySelectorAll('.scrollable'),  'scrolled',  100, 'percent'); // as top of element hits top of viewport 
    }, 100), false);

	window.addEventListener('resize', debounce(function() {
		elementFromTop(document.querySelectorAll('.scrollable'),  'scrolled',  100, 'percent'); // as top of element hits top of viewport
    }, 100), false);
})();

function debounce(fn, ms) {
	var time = null;
	return function() {
		var a = arguments, t = this;
		clearTimeout(time);
		time = setTimeout(function() { fn.apply(t, a); }, ms);
    }
}

function throttle(fn, ms) {
	var time, last = 0;
	return function() {
        var a = arguments, 
            t = this, 
            now = +(new Date), 
            exe = function() { 
                last = now; fn.apply(t, a); 
            };
		clearTimeout(time);
		(now >= last + ms) ? exe() : time = setTimeout(exe, ms);
    }
}

function hasClass(el, cls) {
	if (el.className.match('(?:^|\\s)'+cls+'(?!\\S)')) { return true; } 
}

function addClass(el, cls) {
	if (!el.className.match('(?:^|\\s)'+cls+'(?!\\S)')) { el.className += ' '+cls; } 
}

function delClass(el, cls) {
	el.className = el.className.replace(new RegExp('(?:^|\\s)'+cls+'(?!\\S)'),'');
}

// params: element, classes to add, distance from top, unit ('percent' or 'pixels')
function elementFromTop(elem, classToAdd, distanceFromTop, unit) {
	var winY = window.innerHeight || document.documentElement.clientHeight, 
	elemLength = elem.length, distTop, distPercent, distPixels, distUnit, i;
	for (i = 0; i < elemLength; ++i) {
		distTop = elem[i].getBoundingClientRect().top;
		distPercent = Math.round((distTop / winY) * 100);
		distPixels = Math.round(distTop);
		distUnit = unit == 'percent' ? distPercent : distPixels;
		if (distUnit <= distanceFromTop) {
			if (!hasClass(elem[i], classToAdd)) { addClass(elem[i], classToAdd); }
        } else {
        	//delClass(elem[i], classToAdd); // за да не изчезват, след като са се анимирали веднъж
        }
    }
}