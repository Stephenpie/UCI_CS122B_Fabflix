var cachedSuggestion = {};

function validateForm() {
	var form =  document.forms["advancedSearch"];
	if (form["title"].value == "" && form["year"].value == "" && form["director"].value == "" && form["star"].value == "") {
		window.alert("Please enter something to search!");
		return false;
	}
}

function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	
	// TODO: if you want to check past query results first, you can do it here
	if (cachedSuggestion[query] != null) {
		console.log("using cache");
		doneCallback( { suggestions: cachedSuggestion[query] } );
	} else {
		// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
		// with the query data
		console.log("sending AJAX request to backend Java Servlet")
		console.log("new request");
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "suggestion?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	
	// TODO: if you want to cache the result into a global variable you can do it here
	cachedSuggestion[query] = jsonData;
//	console.log(cachedSuggestion);

	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"])
	var url = "movies?movie=" + suggestion["value"];
	window.location.href = url;
	console.log(url)
}


$('#query').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set the groupby name in the response json data field
    minChars: 3,
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


// bind pressing enter key to a handler function
jQuery('#query').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleSearch($('#query').val())
	}
})

function handleSearch(value) {
	if (value == "") {
		window.alert("Please enter something to search!");
	} else {
		window.location.href = "search?query=" + value + "&numOfMovies=25&page=1&sortby=null";
	}
}

// bind pressing the button to a handler function
window.onload = function(){
	document.getElementById('search').onclick = function() { 
		handleSearch($('#query').val()) 
	}
};