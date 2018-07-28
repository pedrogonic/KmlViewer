/* global ctx */

var pop;

/**
 * Função para chamar o Upload servlet via POST e fazer upload de um ou múltiplos arquivos.
 * 
 * @param {type} context passar <%=request.getContextPath()%>
 * @param {type} form formulario no qual o input file se encontra Ex: $("#uploadForm")
 * @param {type} method método do UploadServlet que deve ser chamado
 * @param {type} listOfParams array de Objetos com campo name e value representando os parametros do request
 * @returns {undefined}
 */
function uploadFile(form, method, listOfParams) {
                        
    var form = $("#uploadForm");
    var formData = new FormData(form[0]);

    if (pop !== undefined)
        pop.close();
    pop = $("#loadingDiv").bPopup();
    
    var url = ctx + "/UploadServlet?method=" + method;
            
    $.each(listOfParams, function(index, param) {
        url += "&" + param.name + "=" + param.value;
    });

    $.ajax({
        url: url,
        method: "POST",
        data: formData,
        accepts: 'application/json',
        contentType: false,
        processData: false
    }).done(function(data) {
        cancelprompt();
        var txt = "<p>" + data.message + "</p>";
        if (data.errors !== undefined && data.errors.length > 0) {
            $.each(data.errors, function(index, value){
                txt += "<br/>" + value;
            }); 
        }
        $("#msgDiv").html("<p>" + txt + "</p>");
        pop = $("#msgDiv").bPopup();
    }).error(function(data) {
        cancelprompt();
        var txt = "<p>Ocorreu um erro!</p>";
        $("#msgDiv").html("<p>" + data + "</p>");
        pop = $("#msgDiv").bPopup();
    });
}

/**
 * 
 * @param {type} context passar <%=request.getContextPath()%>
 * @param {type} method método do DownloadServlet que deve ser chamado
 * @param {type} listOfParams array de Objetos com campo name e value representando os parametros do request
 * @returns {undefined}
 */
function downloadFile(method, listOfParams) {
                    
    var url = ctx + "/DownloadServlet?method=" + method;
    
    $.each(listOfParams, function(index, param) {
        url += "&" + param.name + "=" + param.value;
    });
    
    if (pop !== undefined)
        pop.close();
    pop = $("#loadingDiv").bPopup();
    
    $.ajax({
        url: url,
        type: 'POST',
        accepts: 'application/json'
    }).done(function(data) {
        if (data.errors !== undefined) {
            var txt = "<p>Ocorreu um erro!</p>";
            if (data.errors.length > 0) {
                $.each(data.errors, function(index, value){
                    txt += "<br/>" + value;
                }); 
                $("#msgDiv").html("<p>" + txt + "</p>");
                pop = $("#msgDiv").bPopup();
            }
        } else { 
            window.location = url + "&downloadExistingFile=true";
            pop.close();
        }
    });

}
