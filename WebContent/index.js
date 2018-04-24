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

//function handleShow(value, selectObj) {
//	 // get the index of the selected option 
//	 var idx = selectObj.selectedIndex; 
//	 // get the value of the selected option 
//	 var num = selectObj.options[idx].value;
//	 window.location.href = "search?query=" + value + "&numOfMovies=" + num;
//}
//
//document.getElementById('myselect').onchange = function() { 
//	handleShow(decodeURIComponent($.urlParam('query')), $("#myselect option:selected").val()) 
//};