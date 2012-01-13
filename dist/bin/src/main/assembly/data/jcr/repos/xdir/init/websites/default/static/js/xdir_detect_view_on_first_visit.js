// redirect to preferred view on first visit
$(function() {
    if($.cookie('f')==null){
        $.cookie('f', 'n', {expires: 365,path: '/'});
        var mobile = (/iphone|ipad|ipod|android/i.test(navigator.userAgent.toLowerCase()));
        var targetUrl=document.location.href;
        if(targetUrl.indexOf(".xhtml")>0){
            if(!mobile){
                document.location = targetUrl.replace(".xhtml",".html");
            }
        }else{
            if(mobile){
                document.location = targetUrl.replace(".html",".xhtml");
            }
        }
    }
});