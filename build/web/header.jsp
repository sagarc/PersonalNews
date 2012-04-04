<meta name="keywords" content="" />
<meta name="description" content="" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>News aggregator</title>
<link href="highway/style.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" src="jquery-1.6.4.js"></script>
<script type="text/javascript" src="jquery.cookie.js"></script>
<script type="text/javascript">
           var req_param = 0;
            $(document).ready(function() {
               $(".category").click(function(){
                  var cat_id = $(this).attr("id"); 
                  alert(cat_id);
                  req_param = 1;
            
               });
               
               $(".newshit").click(function() {
                  $.post("SessionManager", {
                           type: "newshit",
                           newsid: this.title
                        },
                        function(data, textStatus) {
                            if(data){
                                
                            }
                        });
               });
            });
            
            
            </script>