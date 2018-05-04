/**
 * Handle the data returned by CheckoutServlet
 * @param resultDataString jsonObject
 */
function handleCheckoutResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle checkout response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#checkout_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitCheckoutForm(formSubmitEvent) {
    console.log("submit checkout form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "checkout",
        // Serialize the login form to the data sent by POST request
        jQuery("#checkout_form").serialize(),
        (resultDataString) => handleCheckoutResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#checkout_form").submit((event) => submitCheckoutForm(event));