function validateForm() {
	var form =  document.forms["advancedSearch"];
	if (form["title"].value == "" && form["year"].value == "" && form["director"].value == "" && form["star"].value == "") {
		window.alert("Please enter something to search!");
		return false;
	}
}

// bind pressing enter key to a handler function
jQuery('#query').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleSearch($('#query').val())
	}
})

function handleSearch(value) {
	window.location.href = "search?query=" + value + "&numOfMovies=25&page=1&sortby=null";
}

// bind pressing the button to a handler function
window.onload = function(){
	document.getElementById('search').onclick = function() { 
		handleSearch($('#query').val()) 
	}
};