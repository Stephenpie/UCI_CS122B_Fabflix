var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

//bind pressing the button to a handler function
//document.getElementById('back').onclick = function() { 
//	window.location.href = "index.html";
//};

var homeButton = document.getElementById('back');
if (homeButton) {
	homeButton.onclick = function() {
		window.location.href = "index.html";
	}
}

var homeButton = document.getElementById('home');
if (homeButton) {
	homeButton.onclick = function() {
		window.location.href = "index.html";
	}
}

var nextButton = document.getElementById('next');
if (nextButton) { 
	nextButton.onclick = function() {
		var query = getUrlParameter('query');
		var genre = getUrlParameter('genre');
		var prefix = getUrlParameter('prefix');
		var limit = getUrlParameter('numOfMovies');
		var offset = parseInt(getUrlParameter('page')) + 1;
		var sort = getUrlParameter('sortby');
		
		var title = getUrlParameter('title');
		var year = getUrlParameter('year');
		var director = getUrlParameter('director');
		var star = getUrlParameter('star');
		
		if (query != null) {
			window.location.href = "search?query=" + query + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
		} else if (genre != null) {
			window.location.href = "browse?genre=" + genre + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
		} else if (prefix != null) {
			window.location.href = "browse?prefix=" + prefix + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
		} else if (title != null || year != null || director != null || star != null) {
			window.location.href = "advanced?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&numOfMovies=" + limit + "&page=" + offset +"&sortby=" + sort;
		} else {
			window.alert("Something went wrong. Back to the main page.");
			window.location.href = "index.html";
		}
	}
};

var prevButton = document.getElementById('prev');
if (prevButton) {
	prevButton.onclick = function() {
		var query = getUrlParameter('query');
		var genre = getUrlParameter('genre');
		var prefix = getUrlParameter('prefix');
		var limit = getUrlParameter('numOfMovies');
		var sort = getUrlParameter('sortby');
		var offset = parseInt(getUrlParameter('page')) - 1;
		
		var title = getUrlParameter('title');
		var year = getUrlParameter('year');
		var director = getUrlParameter('director');
		var star = getUrlParameter('star');
		
		if (query != null) {
			window.location.href = "search?query=" + query + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
		} else if (genre != null) {
			window.location.href = "browse?genre=" + genre + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
		} else if (prefix != null) {
			window.location.href = "browse?prefix=" + prefix + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
		} else if (title != null || year != null || director != null || star != null) {
			window.location.href = "advanced?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&numOfMovies=" + limit + "&page=" + offset +"&sortby=" + sort;
		} else {
			window.alert("Something went wrong. Back to the main page.");
			window.location.href = "index.html";
		}
	}
};



function updateItem(movie, qtyId) {
	var qty = document.getElementById(qtyId).value;
	if (!Number.isInteger(qty) || qty <= 0) {
		window.alert("Invalid quantity!");
	} else {
		window.location.href = "cart?act=update&item=" + movie + "&qty=" + qty;
	}
};

function deleteItem(movie) {
	window.location.href = "cart?act=delete&item=" + movie;
};

function addToCart(movie) {
	window.location.href = "cart?act=add&item=" + movie;
};

function checkout() {
	window.location.href = "checkout";
};

function viewCart() {
	window.location.href = "cart";
};