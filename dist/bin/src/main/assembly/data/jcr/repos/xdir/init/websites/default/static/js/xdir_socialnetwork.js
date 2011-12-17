$(function() {
	$(".sn a.provider").each(function() {
		var providerLink=$(this)
		var providerUrl=providerLink.attr("href")
		var snAccount=providerUrl.substring(providerUrl.lastIndexOf("/")+1)
		if(providerUrl.indexOf("twitter.com")>0){			
			var useAsIconButton=$("<button>use as icon</button>");
			useAsIconButton.click(function() {
				var userInfoUrl="http://twitter.com/users/show/"+snAccount+".xml";
				$.ajax({url:userInfoUrl,format:"xml", success:function(data) {
				alert("data:"+data);
				  alert(data.profile_image_url);
				}});
			});
			providerLink.after(useAsIconButton);
		}
	});
});
