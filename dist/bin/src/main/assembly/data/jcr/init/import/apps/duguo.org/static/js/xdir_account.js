$(function() {
	displayAccountLinks();
});


function displayAccountLinks(){
	var accountContainer=$(".pageheader .account" );
	if(accountContainer.length>0){
		$(".accountlink",accountContainer).hide();
		var logedinUser=$.cookie("u");
		if(logedinUser!=null){
			if(logedinUser.indexOf("\"")==0){
				logedinUser=logedinUser.substring(1,logedinUser.length-1);
			}
			$(".user",accountContainer).html(logedinUser);
			$(".login",accountContainer).hide();
			$(".logout",accountContainer).show();
		}else{
			$(".logout",accountContainer).hide();
			$(".login",accountContainer).show();
		}
	}
}