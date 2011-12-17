function bindActionFormButtons(preAction,afterAction){
    var actionForm=$("#action_form");
    $("#action_submit_button",actionForm).click(function(){
    	if(preAction){
    		if(!preAction()){
    			return false;
    		}
    	}
        performAction(function(data){
	            actionForm.hide();
	            $("#action_form_success").show();  
		    	if(afterAction){
		    		afterAction(data);
		    	}
        });
    });
    $("#action_update_button",actionForm).click(function(){
    	if(preAction){
    		if(!preAction()){
    			return false;
    		}
    	}

        performAction(function(){
            $("#action_form span.error").html("Updated successfully @ "+$.xdir.utils.timestampString());
        });
    });
    $("#action_cancel_button",actionForm).click(function(){
            $("#action_dialog").dialog("close");
    });
    $("input.clickable",actionForm).click(function(){
    	var filedName=$(this).attr("name");
    	var fieldValue="";
    	$("input[@name='"+filedName+"']",actionForm).each(function(){
    		if($(this).attr("checked")){
    			fieldValue+=","+$(this).attr("value");
    		}
    	});
    	if(fieldValue!=""){
    		fieldValue=fieldValue.substr(1);
    	}
    	$("input[@name='"+filedName.substr(1)+"']",actionForm).attr("value",fieldValue);
    });
    $("a.add_property",actionForm).click(function(){
    	var content="<tr><td class=\"name\"><input class=\"xdirinput required\" name=\"properties"+new Date().getTime()+"\" value=\"\"/></td>";
        content+="<td><textarea class=\"xdirinput\" name=\"properties"+new Date().getTime()+"2\"/></td>";
        content+="<td style=\"text-align:center;\"><div class=\"button close\">X</div><div class=\"button expand\">&or;</div></td></tr>";
        var trElement=$(content);
        $("tr.button_tr",actionForm).before(trElement);
        $("div.close",trElement).click(function(){
        	$(this).parent().parent().remove();
        });
        bindExpandButton($("div.expand",trElement));
        return false;
    });
    bindExpandButton($("div.expand",actionForm));
    $("input.date_picker",actionForm).datepicker({dateFormat:'yy-mm-dd' });
    $("input.search",actionForm).each(function(){
    	var fieldElement=$(this);
    	$.xdir.actions.bindSearchField(fieldElement,function(v){
			fieldElement.attr("value",$.xdir.jcr.getNameFromPath(v.extra));
		},fieldElement.attr("title"));
    });
    $("input.xdirinput[name='displayName']",actionForm).focus();
 }
 
 function bindExpandButton(elements){
      elements.click(function(){
      	if($("textarea",$(this).parent().parent()).css("height")=="50px"){
      		$("textarea",$(this).parent().parent()).css("height",300);
      	}else{
      		$("textarea",$(this).parent().parent()).css("height",50);
      	}
      });
 }
 
 
function performAction(callbackAction,submitUrl){
	if(submitUrl==pageContext){
		submitUrl=submitUrl+"/";
	}
	var params="timestamp="+new Date().getTime();
    var actionForm=$("#action_form");
    var tags="";
    var fullpath="";
    var currentpath="";
	$(".xdirinput",actionForm).each(function(){
		var inputElement=$(this);
		if(inputElement.attr("type")=="checkbox" && !inputElement.attr("checked")){
			return;
		}
		var name=inputElement.attr("name");
		var value=inputElement.attr("value");
		if(!value){
			value="";
		}
		if(name.indexOf("properties")==0){
			if(inputElement.hasClass("search")){
				if(value!=""){
					value=inputElement.attr("title")+"="+value;
				}
			}
			if(inputElement.hasClass("required") && !$.xdir.utils.endswith(name,"Value") && value.indexOf(":")<0){
				value=$.xdir.jcr.normalizePathName(value);
			}
		}
		params+="&"+name+"="+encodeURIComponent(value);
	});
	
	if(!submitUrl){
		submitUrl=pagePath;
	}
	$.ajax({
          url: submitUrl+".json",
          data: params,
          dataType: 'json',
          type: 'post',
          success: function (data) {
	   		if(data.status==0){
	   			$("#action_form span.error").html("");
	   			callbackAction(data);
	   		}else{
	   			$("#action_form span.error").html(data.status);
	   		}
          }
     });
}

function bindNodeSelector(container, displayElement, paths){
	if(!paths){
		paths=$.xdir.jcr.paths;
	}
	$(paths).each(function(){
		var ulElment=$("<ul class=\"nodeul\"></ul>");
		container.append(ulElment);
		createExpandableLink(this.path,$.xdir.jcr.getNameFromNode(this),this.childcount,ulElment,function(link){
			var targeturl=$(link).attr("href");
			targeturl=targeturl.substring(0,targeturl.lastIndexOf("."));
			if(targeturl.lastIndexOf("/")==targeturl.length-1){
				targeturl=targeturl.substring(0,targeturl.length-1);
			}
			displayElement.attr("value",targeturl);
			return false;
		});
		container=$("li:first",ulElment);
	});
}

function initSearchFiled(){
	$.xdir.actions.bindSearchField($("#xdir_search"),function(v){loadXdirNode(v.extra);});
}


function ajaxFileUpload()
    {
        $.ajaxFileUpload
        (
            {
                url:pagePath+".json?action="+$("#action_form input[@name='action']").attr("value")+"&timestamp="+new Date().getTime(),
                secureuri:false,
                fields:$("#action_form .xdirinput"),
                dataType: 'json',
                success: function (data, status)
                {
                	if(data.status==0){
			            refreshNodeView();
			            $("#action_dialog").dialog("close");
		           }else{
                    	alert("error status:"+ data.status);
                    }
                },
                error: function (data, status, e)
                {
                	if(data.status==0){
		                $("#action_form").hide();
		            	$("#action_form_success").show();
		                $("#action_form_success .node_name").html($("#action_form input.xdirinput[name='pathName']").attr("value"));
                    }else{
                    	alert("error status:"+ data.status);
                    }
                 	alert(data+":"+e);
                }
            }
        )

    } 