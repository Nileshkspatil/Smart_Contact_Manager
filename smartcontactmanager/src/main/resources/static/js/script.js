console.log("this is script file");

const toggleSidebar=()=>{

	if($(".sidebar").is(":visible")){
		
		//true
		//band krna hai
		
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","2%");	
	}else{
		//false
		//show karna hai
		
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");	
		
		
	}	
	
	
	
	
};
