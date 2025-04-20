$(document).ready(function(){
    $("#spaServiceLink").on("click", function(e){
        e.preventDefault();
        var url = $(this).attr("href");
        $(".content-wrapper").load(url, function(response, status, xhr){
            if(status === "error"){
                let msg = "Error loading content: ";
                $(".content-wrapper").html(msg + xhr.status + " " + xhr.statusText);
            }
        });
    });
});
