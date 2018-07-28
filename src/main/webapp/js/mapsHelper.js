/* global google, map, fullURL */

/**
 * Carregar api do GoogleMaps
 */
var mapsAPIKey = 'AIzaSyDSqOOr60v8IkRBj4xUIUJki9XgnRdDzjI';

//var fileref = document.createElement('script');
//    fileref.setAttribute("type","text/javascript");
//    fileref.setAttribute("src", "http://maps.googleapis.com/maps/api/js?key="
//                                + mapsAPIKey);
//                                + "&sensor=true"); //Sensor not required!!

/**----------------------------- GLOBAIS ------------------------------------**/

var map = undefined;
var kmzLayer = undefined;
var markers = undefined;
var userMarkers = undefined;
var routes = undefined;
var userRoutes = undefined;
var markerClusterer = undefined;

/**----------------------------- FUNÇÕES ------------------------------------**/

/**
 * Checa se a API foi inicializada.
 * @returns {undefined}
 */
function isMapsInit() { 
    return (typeof google === 'object' && typeof google.maps === 'object');
}

/**
 * Inicia API do Google Maps, definindo o objeto google 
 * @returns {undefined}
 */
function mapsInit(fn) {
    
    var divId = arguments[1];
    console.log('Requesting Google Maps...');
    $.loadScript(
                "http://maps.googleapis.com/maps/api/js?key=" + mapsAPIKey
                , function() {
                    console.log('Google Maps started!');
                    fn(divId);
                });
                
}

/**
 * Carrega um mapa
 * @param {string} divId id do div que receberá o map (Ex: map_canvas)
 * @returns {undefined}
 */
function carregaMapa(divId) {
    
    console.log("carrega mapa fn");
    
    var drawMap = function (divId) {
                console.log("Callback after started.");
                function initialize() {
                    console.log("Done! Creating map!");
                    
                    var mapOptions = {
                                        mapTypeId: google.maps.MapTypeId.ROADMAP,
                                        zoom: 4
                                    };
                    map = new google.maps.Map(document.getElementById(divId), mapOptions);

                    google.maps.event.addListener(map, "rightclick", function(event) {
                        showCoordinates(event, 'console'); // TODO: mudar para opção em JSON de config
                    });

                    center('Brazil');
                    
                }
                console.log("Waiting for window load...");
                google.maps.event.addDomListener(window, "load", initialize);

            };
    
    console.log("Checking google object...");
    if(!isMapsInit()) {
        console.log("Not started");
        mapsInit(drawMap, divId); // fn(callback, arguments[1], arguments[2], ..)
    } else {
        console.log("Started");
        drawMap(divId);
    }
    
}

/**
 * Adiciona uma box no mapa
 * @param {type} fileId
 * @returns {undefined}
 */
function adicionaBox() {
    // TODO
}

function addRoute() {
    //TODO
}

function addClusterer() {
    //TODO
}

function center(country) {
    var geocoder = new google.maps.Geocoder();

    geocoder.geocode( {'address' : country}, function(results, status) {
    if (status === google.maps.GeocoderStatus.OK) {
        map.setCenter(results[0].geometry.location);
    } else 
        console.log('Geocoder didn´t find location');
});
}

function showCoordinates(event, output) {
    var lat = event.latLng.lat();
    var lng = event.latLng.lng();
    if (false) {
        // TODO tratar opção via JSON
    } 
        
    console.log("Lat=" + lat + "; Lng=" + lng);
}

/**
* Não funciona em localhost pois a API do Google precisa de
* um link público para um KML/KMZ!!!!!!
* @param fileId Id do KML/KMZ na tabela correspondente no banco.
* @returns {undefined}
*/
function showKMZ(fileId) {

    /**
     * Utilização do DownloadServlet para baixar o KML/KMZ desejado.
     */
    var src = fullURL + '/DownloadServlet?method=KMZDownload&fileId=' + fileId + '&downloadExistingFile=true';
    
    /**
     * Testes em localhost: Descomentar a linha abaixo, que aponta para um KML
     * público.
     */
    //src = 'https://developers.google.com/maps/documentation/javascript/examples/kml/westcampus.kml';
    
    if (fileId !== undefined && fileId !== '') { // Mostra KML/KMZ
        kmzLayer = new google.maps.KmlLayer(src);
        kmzLayer.setMap(map);
    } else if (kmzLayer !== undefined) // Esconde um KML/KMZ sendo mostrado
        kmzLayer.setMap(null);
    
}