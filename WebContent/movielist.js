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
document.getElementById('back').onclick = function() { 
	window.location.href = "index.html";
};

document.getElementById('prev').onclick = function() {
	var query = getUrlParameter('query');
	var genre = getUrlParameter('genre');
	var prefix = getUrlParameter('prefix');
	var limit = getUrlParameter('numOfMovies');
	var sort = getUrlParameter('sortby');
	var offset = parseInt(getUrlParameter('page')) - 1;
	if (offset <= 0) {
		offset = 1;
	}
	if (query != null) {
		window.location.href = "search?query=" + query + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
	} else if (genre != null) {
		window.location.href = "browse?genre=" + genre + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
	} else if (prefix != null) {
		window.location.href = "browse?prefix=" + prefix + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
	} else {
		window.alert("Something went wrong. Back to the main page.");
		window.location.href = "index.html";
	}
};

document.getElementById('next').onclick = function() { 
	var query = getUrlParameter('query');
	var genre = getUrlParameter('genre');
	var prefix = getUrlParameter('prefix');
	var limit = getUrlParameter('numOfMovies');
	var offset = parseInt(getUrlParameter('page')) + 1;
	var sort = getUrlParameter('sortby');
	if (query != null) {
		window.location.href = "search?query=" + query + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
	} else if (genre != null) {
		window.location.href = "browse?genre=" + genre + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
	} else if (prefix != null) {
		window.location.href = "browse?prefix=" + prefix + "&numOfMovies=" + limit + "&page=" + offset + "&sortby=" + sort;
	} else {
		window.alert("Something went wrong. Back to the main page.");
		window.location.href = "index.html";
	}
};