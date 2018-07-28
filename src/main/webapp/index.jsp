<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>KML Viewer</title>
        
        <c:set var="fileList" value="${sessionScope.files}" />
        <c:remove var="fileList" scope="session" />
        
        <script type="text/javascript"> 
			
            var files;

            $(document).ready(function() {

                files = ${empty fileList? "undefined":fileList};

                if (files !== undefined) {
                    var dropdown = $("#filesDropdown");
                    $.each(files, function(i, item) {
                        dropdown.append($('<option value="'+item.id+'">'+item.file.path+'</option>'));
                    });
                }   

                console.log("carrega mapa");
                carregaMapa("map_canvas");

            });
            
        </script>
        
    </head>
    <body>
        <section class="center map">
            <div id="map_canvas" style="width:100%; height:100%"></div>
            <button id="uploadFile" value="Upload file"/>
            <select id="filesDropdown" onChange="showKMZ(this.value);">
                <option></option>
            </select>
        </section>
    </body>
</html>
