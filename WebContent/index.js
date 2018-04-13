function handleShowList() {
	window.location.href = "searchresult.html?query=" + value;
	
}

// bind pressing the button to a handler function
document.getElementById('show').onclick = function() { 
	handleShowList() 
};