
<?php if(isset($_POST['keyword'])):?>
<?php 
    
    $keyword = $_POST["keyword"];
    $catergory = $_POST["catergory"];
    $distance =  $_POST["distance"];
    $location =  $_POST["location"];
        
    if($distance == "")
        $distance = 10*1609;
    else
        $distance *=1609;

    function getSearch($geolocation, $radius, $type, $keyword, $key)
    {
        $searchurl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=".$geolocation."&radius=".$radius."&type=".$type."&keyword=".$keyword."&key=".$key;
        //echo $searchurl;
        $searchContent = file_get_contents($searchurl);
        $searchDecodejson = json_decode($searchContent, true);
        global $searchData;
        $searchData = $searchDecodejson['results'];
        //print_r($searchData );
        //return $searchData;
    }

    if(($location !='current'))
    {
        //echo "nothing";
        $location = str_replace(' ', '', $location);
        $url = "https://maps.googleapis.com/maps/api/geocode/json?address=".$location."&key=AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
             
        $urlContent = file_get_contents($url);
        $urldecodejson = json_decode($urlContent, true);
            //print_r($urldecodejson['results']);
        if(count($urldecodejson['results']) != 0)
        {
            $lat = $urldecodejson["results"][0]['geometry']['location']['lat'];
            $lng =$urldecodejson["results"][0]['geometry']['location']['lng'];

            $geolocation = $lat.",".$lng;
                //echo $geolocation;
            $key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
            getSearch($geolocation, $distance, $catergory,$keyword, $key);
            echo json_encode($searchData);
            return;
        }
        else
        {
            $searchData = array();
            echo $keyword;
            return;
        }
        }//location == current
    else
    {
        $lat = $_POST['lat'];
        $lng = $_POST['lng'];
        $geolocation = $lat.",".$lng;
        $key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
        getSearch($geolocation, $distance, $catergory,$keyword, $key);
        
        echo json_encode($searchData);
        return;
            
    }
    
?>
<?php endif; ?>

<?php if(isset($_POST['placeId'])):
?>

<?php 
    //global $count;
    //$count = strlen("sfbosafboaujblbnpIsfias");
    $placeId =$_POST['placeId'];
    $placeUrl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=".$placeId."&key=AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
    $photoReview = file_get_contents($placeUrl);
    $photoReviewJson = json_decode($photoReview, true);

    //$reviews = $photoReviewJson['result']['reviews'];
    //$photos = $photoReviewJson['result']['photos'];
    $review_pack = array();
    if(isset($photoReviewJson['result']['reviews']))
    {
        $reviews = $photoReviewJson['result']['reviews'];
        //$review_pack = array();
        for($i = 0; $i< 5 && $i< count($reviews); $i++)
        {
            $item = array();
            $item['author_name'] = $reviews[$i]['author_name'];
            $item['profile_photo']=$reviews[$i]['profile_photo_url'];
            $item['text'] = $reviews[$i]['text'];
            $review_pack[$i] = $item;

            //print_r($image);
        }
    }
    else
    {
        $reviews = array();
    }

    $photo_pack = array();
    if(isset($photoReviewJson['result']['photos']))
    {
        $photos = $photoReviewJson['result']['photos'];
        //$photo_pack = array();
        for($i = 0; $i< 5 && $i< count($reviews); $i++)
        {
            $photoRefer = $photos[$i]['photo_reference'];
            $photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth="."600"."&maxheight="."600"."&photoreference=".$photoRefer."&key=AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
            $image = file_get_contents($photoUrl);
            file_put_contents('image'.$i.'.png', $image);

            $photo_Filename = "image".$i.".png";
            $photo_pack[$i] = $photo_Filename; 

            //print_r($image);

        }
    }
    else
    {
        $photos = array();
    }
    
    /*$review_pack = array();
    for($i = 0; $i< 5 && $i< count($reviews); $i++)
    {
        $item = array();
        $item['author_name'] = $reviews[$i]['author_name'];
        $item['profile_photo']=$reviews[$i]['profile_photo_url'];
        $item['text'] = $reviews[$i]['text'];
        $review_pack[$i] = $item;
        
        //print_r($image);
    }*/

    /*$photo_pack = array();
    for($i = 0; $i< 5 && $i< count($reviews); $i++)
    {
        $photoRefer = $photos[$i]['photo_reference'];
        $photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth="."600"."&maxheight="."600"."&photoreference=".$photoRefer."&key=AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
        $image = file_get_contents($photoUrl);
        file_put_contents('image'.$i.'.png', $image);
        
        $photo_Filename = "image".$i.".png";
        $photo_pack[$i] = $photo_Filename; 
        
        //print_r($image);
        
    }*/

    $package = array();
    $package[0] =$review_pack;
    $package[1] = $photo_pack;
    

    echo json_encode($package);
    return;
?>

<?php endif;?>


<!DOCTYPE HTML> 
<html>
<head>
<meta charset="utf-8"/>
<title>Homework 6</title>
 <style type="text/css">
    html,body {
    height:100%;
    width:100%;
}
    .box{
        border-style: solid;
        border-width: 3px;
        border-color: silver;
        width:600px;
        height:230px;
        margin:50px auto;
        background-color: whitesmoke;
    
    }
    .content{
        width:550px;
        margin: 10px auto;
    }
    .content h1{
        margin: 10px auto;
        text-align: center;
        border-bottom: solid 2px silver;
        font-weight: 300;
        font-size: 30px;
        font-family: "Times New Roman", Times, serif;
    
    }
    .button-collection{
        padding-left:50px;
    }
    
    .divinline{
        display: inline-block;
    }
    
    #container{
        margin:20px auto;
        text-align: center;
        width:800px;
    }
    
    table{
        border-collapse: collapse;
        margin:auto auto;
    }
    table, th, td {
        border: 2px solid silver;
    }
    
    a:link, a:visited, a:hover, a:active{
        text-decoration: none;
        color:black;
    }
    
    #noRecord{
        border-style: solid;
        border-width: 2px;
        border-color: silver;
        width:600px;
        height:30px;
        margin:0px auto;
        background-color: whitesmoke;
    }
    .photoReview_title
     {
         margin:0px auto;
         text-align: center;
         
         font-size: 25px;
     }
     
     #arrow_up
     {
         text-align: center;
         width: 35px;
         height:35px;
     }
    #arrow_down
     {
         text-align: center;
         width: 35px;
         height:35px;
     }
     
     .hide_review
     {
         font-size: 18px;
     }
     
     .review_table
     {
         margin:0px auto;
         text-align: center;
         width: 600px;
         
        border-style: solid;
        border-width: 2px;
        border-color: silver;
         
     }
     
     .review_row
     {
         text-align: center;
         
     }
     
     .review_text_row
     {
         
     }
     
     #author_photo
     {
         width: 40px;
         height: 40px;
     }
     
     #author_name
     {
         font-size: 20px;
     }
     
     #test{
        margin:20px auto;
        text-align: center;
         width:600px;
    }
     
    .photo_row
     {
         text-align: center;
         width:600px;
         height:500px;
         
     }
     
     #map, #floating-panel
     {
         position:absolute;
     }
     #map
     {
         z-index:1;
         width: 300px;
         height:300px;
     }
     
     #floating-panel{
         z-index:2;
         display: block;
         width:80px;
         height:80px;
     }
     #floating-panel option
     {
         background-color: silver;
         font-size: 22px;
     }
         
</style>  
</head>
<body>     
<script>
    var table = document.getElementById("container");
    //review_status = "off";
    
    function HTMLEncode(str){
      var i = str.length,
          aRet = [];

      while (i--) {
        var iC = str[i].charCodeAt();
        if (iC < 65 || iC > 127 || (iC>90 && iC<97)) {
          aRet[i] = '&#'+iC+';';
        } else {
          aRet[i] = str[i];
        }
       }
      return aRet.join('');    
    }
    
    //var offsetX, offsetY;
    function show_table(jsonString)
    {
        MapStatus = [];
        obj = JSON.parse(jsonString);
        //console.log(jsonString);///??????????????????????????????????
        var icon, name, address,placeId;
        var output = "";
        
        if(obj.length == 0)
        {
            output +="<div id='noRecord'><p style='text-align:center; margin:0 auto; padding-top:5px;'>No records have been found</p></div>";
            document.getElementById("container").style.display = "block";
            document.getElementById("container").innerHTML = output;
        }
        else
        {
            mainTable = document.createElement('div');
            output += "<table style = 'width:90%'><tr><th width='80px'><b>Catergory</b></th><th><b>Name</b></th><th><b>Address</b></th></tr>";
            for(var i =0; i< 20; i++)
            {
                target_lat = obj[i]['geometry']['location']['lat'];
                target_lng = obj[i]['geometry']['location']['lng'];
                ori_lat = lati;
                ori_lng = lgn;
                MapStatus[i] = "close";
                //console.log(MapStatus[i]);
                
                output +="<tr>";
                icon = obj[i]['icon'];
                name = obj[i]['name'];
                address = obj[i]['vicinity'];
                placeId = obj[i]['place_id'];
                //console.log(typeof(placeId));
                //console.log(placeId);
                tmp =  placeId;
                Encode_name = HTMLEncode(name);
                output += "<td><img src='" + icon + "' alt='icon' height='45px'></td>";
                output += "<td><a href='javascript:photo_review(&quot;"+tmp+"&quot;" + "," + " &quot;"+Encode_name+"&quot;);'>" + Encode_name +"</a></td>";
                output += "<td><a href='javascript:getMap("+target_lat+","+target_lng+","+i+")' id='showInitMap' alt='close'>" + address+ "</a></td>";
                output +="</tr>";
            }
            output +="</table>";

            document.getElementById("container").style.display = "block";
            //document.getElementById("container").innerHTML = output;
            mainTable.innerHTML = output;
            document.getElementById('container').appendChild(mainTable);
            
            //document.addEventListener("click", printMousePos);
        }
        
    }
    
    function printMousePos(event){
        offsetX = event.clientX;
        offsetY = event.clientY;
        console.log(offsetX);
        console.log(offsetY);
    }
    
    
    
    function calculateAndDisplayRoute(directionsService, directionsDisplay) {
        var selectedMode = document.getElementById('mode').value;
            console.log(selectedMode);
          directionsService.route({
            origin: {lat: mylat, lng: mylng},  // Haight.
            destination: {lat: ori_lat, lng: ori_lng},  // Ocean Beach.
            // Note that Javascript allows us to access the constant
            // using square brackets and a string value as its
            // "property."
            travelMode: google.maps.TravelMode[selectedMode]
          }, function(response, status) {
            if (status == 'OK') {
              directionsDisplay.setDirections(response);
            } else {
              window.alert('Directions request failed due to ' + status);
            }
          });

    }
    
    
    function initialize() {
        
        var directionsDisplay = new google.maps.DirectionsRenderer;
        var directionsService = new google.maps.DirectionsService;
        
        var latlng = new google.maps.LatLng(mylat, mylng);
        console.log(string);
        
        var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 10,
            center: latlng
        });
        
        var marker = new google.maps.Marker({
          position: latlng,
          map: map
        });
        
        directionsDisplay.setMap(map);
        calculateAndDisplayRoute(directionsService, directionsDisplay);
        document.getElementById('mode').addEventListener('change', function() {
        calculateAndDisplayRoute(directionsService, directionsDisplay);
        });
        
        
        
    }
    
    string = "";
    
    function getMap(x, y, i)
    {
        //document.addEventListener("click", printMousePos);
        if(MapStatus[i] == 'close')
        {
            //MapDiv = document.createElement('div');
            //MapDiv.setAttribute("id", "map");
            
            mylat = x;
            mylng = y;
            
            var myKey = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
            script = document.createElement('script');
            script.type = 'text/javascript';
            string = "hey it works"; 
            script.src = "https://maps.googleapis.com/maps/api/js?key=" + myKey +"&callback=initialize";
            document.body.appendChild(script);
            
            MapStatus[i] = "open";
            console.log(MapStatus[i]);
               
        }
        else
        {
            MapStatus[i] = "close";
            document.getElementById('map').innerHTML = "";
            document.body.removeChild(script);
            console.log(MapStatus[i]);
            
        }
        
    }
    

    
    function show_photo_review_table(dataString, dataName)
    {
        document.getElementById("container").innerHTML = "";
        document.getElementById("test").innerHTML = "";
        //console.log(dataString);
        p_r_Obj = JSON.parse(dataString); 
        var reviews_error, photo_error;
        //review_status = "on";
        output = "";
        review_status = "off";
        
        reviews = p_r_Obj[0];
        photos = p_r_Obj[1];
        
        reviewDiv = document.createElement("div");
        output += "<h3 class='photoReview_title'>" + dataName + "</h3>";
        output += "<p class='hide_review'>click to hide reviews</p>";
        output += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png' id='arrow_up' alt = 'up'><br>";
        reviewDiv.innerHTML = output;
        document.getElementById('container').appendChild(reviewDiv);
        //output += "<img src='/arrow_up.png' id='arrow_down' alt = 'up'>";
        
        photoDiv = document.createElement('div');
        photo_output = "";
        photo_output += "<p class='hide_review'>click to hide reviews</p>";
        photo_output += "<img src='http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png' id='arrow_down' alt = 'up'><br>";
        photoDiv.innerHTML = photo_output;
        document.getElementById("test").appendChild(photoDiv);
        
        
    
        reviewTable = document.createElement("div");
        document.getElementById('arrow_up').addEventListener('click', function(){
            
            
            flag = document.getElementById("arrow_up").alt;
            Routput = "";
            //var reviewTable;
            //console.log(flag);
    
            if(flag == 'up')
            {
                if(document.getElementById("arrow_down").alt == 'down')
                {
                    document.getElementById('arrow_down').src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png';
                    document.getElementById('test').lastChild.text = '';
                    document.getElementById('test').removeChild(document.getElementById('test').lastChild);
                    document.getElementById("arrow_down").alt = 'up';

                    
                }
                document.getElementById('arrow_up').src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png';
                //reviewTable = document.createElement("div");
                
                Routput += "<table class='review_table'>";
                if(reviews.length == 0)
                {
                    Routput += "<tr><td><b>No Reviews Found</b></td></tr>"
                }
                else{
                    for(var i = 0; i < reviews.length; i++ )
                    {
                        var author_photo = reviews[i]['profile_photo'];
                        var author_name = reviews[i]['author_name'];
                        var text = reviews[i]['text'];
                        Routput += "<tr class='review_row'><td>" + "<img src='" + author_photo + "' id ='author_photo'>" + "<span id = 'author_name'>" + author_name +"</span></td></tr>"; 
                        if(text.length == 0)
                            Routput += "<tr class='review_text_row'><td><p>"+text+"</p></td></tr>";
                        else
                            Routput += "<tr class='review_text_row'><td>" + text + "</td></tr>";
                    }
                }
                Routput +="</table>";
                //document.getElementById("container").innerHTML += Routput;
                reviewTable.innerHTML = Routput;
                //console.log(Routput);
                document.getElementById('container').appendChild(reviewTable);
                document.getElementById("arrow_up").alt = 'down';
                //console.log(document.getElementById("arrow_up").alt);
                //return false;

            }
            else
            {
                //console.log(flag);
                document.getElementById('arrow_up').src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png';
                document.getElementById('container').lastChild.text = '';
                document.getElementById('container').removeChild(document.getElementById('container').lastChild);
                document.getElementById("arrow_up").alt = 'up';
                //return false;
            }
                
            
            //console.log(flag);
        });
        
        photoTable = document.createElement("div");
        
        document.getElementById('arrow_down').addEventListener('click', function(){
            
            
            mark = document.getElementById("arrow_down").alt;
            Poutput = "";
            
            if(mark == 'up')
            {
                if(document.getElementById("arrow_up").alt == 'down')
                {
                    document.getElementById('arrow_up').src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png';
                    document.getElementById('container').lastChild.text = '';
                    document.getElementById('container').removeChild(document.getElementById('container').lastChild);
                    document.getElementById("arrow_up").alt = 'up';
                    
                }
                document.getElementById('arrow_down').src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png';
                //reviewTable = document.createElement("div");
                Poutput += "<table class='review_table'>";
                if(photos.length == 0)
                {
                    Poutput += "<tr><td><b>No Photos Found</b></td><tr>";
                }
                else{
                    for(var i = 0; i < photos.length; i++ )
                    {
                        var photo_image = photos[i];
                        //console.log(photo_image);
                        Poutput += "<tr class='photo_row'><td><a href='/" + photo_image + "'>" + "<img src='/" + photo_image + "' id ='photo_image'>"+"</a></td></tr>"; 
                        console.log(Poutput);
                    }
                }
                Poutput +="</table>";
                //document.getElementById("container").innerHTML += Routput;
                photoTable.innerHTML = Poutput;
                //console.log(Routput);
                document.getElementById('test').appendChild(photoTable);
                document.getElementById("arrow_down").alt = 'down';
                console.log(document.getElementById("arrow_down").alt);
                //return false;

            }
            else
            {
                //console.log(mark);
                document.getElementById('arrow_down').src = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_up.png';
                //console.log(reviewTable.innerHTML);
                document.getElementById('test').lastChild.text = '';
                document.getElementById('test').removeChild(document.getElementById('test').lastChild);
                document.getElementById("arrow_down").alt = 'up';
                //return false;
            }
                
        });
        
        
    }
    
    
    //photo_review Ajax
    function photo_review(Id, dataName)
    {
        ajax = new XMLHttpRequest();
        var SendTo = "/final.php";
            
        var place_id = "placeId=" +Id;
            //var MyVaraible = lgn + "," + lati;
            //var VariablePlaceholder = "location=";
            //var UrlToSend = PageToSendTo + VariablePlaceholder + MyVaraible;
            ajax.onreadystatechange=function()
            {
                if (ajax.readyState==4&&ajax.status==200)
                {
                    photo_reviewData = ajax.responseText;
                    //document.getElementById("test").innerHTML=photo_reviewData;
                    show_photo_review_table(photo_reviewData, dataName);
                    //console.log("--->");
                }
            }

            ajax.open("POST", SendTo, true);
            ajax.setRequestHeader("Content-type","application/x-www-form-urlencoded");
            ajax.send(place_id);
            
    }
    
</script>
    
        
<script>
    function formValidation(oEvent){
        oEvent = oEvent || window.Event;
        var txtField = oEvent.target || oEvent.srcElement;
        
        //var t1ck = true;
        if(document.getElementById("keyword").value.length == 0)
        {
            //t1ck = false;
            document.getElementById("keyword").required = true;      
        }
        
        if(document.getElementById("distance").value.length == 0)
        {
           // document.getElementById("distance").value = document.getElementById("distance").placeholder;
            
        }
        
        if(document.getElementById("defaultHere").checked)
        {
            document.getElementById("other").disabled = true;
            document.getElementById("other").value = "";
            t1ck = false;
            locationJSON(document.getElementById("myForm"));
            t1ck = true;
        }
        
        if(document.getElementById("locationOther").checked)
        {
            document.getElementById("other").disabled = false;
            document.getElementById("other").required = true;
            //t1ck = false;
            //locationJSON(document.getElementById("myForm"));
            //t1ck = true;
            //document.getElementById("search").disabled = false;
                
        }
        
            
    }
    
    function locationJSON(what)
    { 
        var URL = "http://ip-api.com/json"; 
        //console.log(URL);
        function loadJSON(url) 
        {

            xmlhttp=new XMLHttpRequest();
            xmlhttp.open("GET",url,false);

            xmlhttp.send();

            //console.log(typeof(xmlhttp.responseText));
            jsonObj= JSON.parse(xmlhttp.responseText);
            //console.log(jsonObj);
            return jsonObj; 
        }
        
    
        jsonObj = loadJSON(URL);
        
        //jsonObj.onload=geoLocation(jsonObj);
        lgn = jsonObj.lon;
        lati = jsonObj.lat;
    }
    
    
    window.onload=function(){
        var btnSearch = document.getElementById("search");

        var tkeyword = document.getElementById("keyword");
        var tdistance = document.getElementById("distance");
        var tcatergory = document.getElementById("catergory");
        var defaultHere = document.getElementById("defaultHere");
        var locationOther = document.getElementById("locationOther");
        var tother = document.getElementById("other");
        
        var t1ck = false;
        //disable search before loading current postion
        document.getElementById("search").disabled = true;
        document.getElementById("other").disabled = true;
        
        if(defaultHere.checked == true)
        {
            document.getElementById("other").disabled = true;
            locationJSON(document.getElementById("myForm"));
            document.getElementById("search").disabled= false;
        }
            
        tkeyword.onkeyup = formValidation;
        tdistance.onkeyup = formValidation;//?????????
        tcatergory.onclick = formValidation;//?????不需要，直接传值
        locationOther.onclick = formValidation;
        defaultHere.onclick = formValidation;
    }
    
    function myReset(){
        document.getElementById("container").innerHTML = "";
        document.getElementById("container").display = "none";
        
        document.getElementById("test").innerHTML ="";
        document.getElementById("test").display = "none";
        
        document.getElementById('map').innerHTML ="";
        document.getElementById('map').displace = "none";
        
        document.getElementById('keyword').value = "";
        document.getElementById('catergory').value = "default";
        document.getElementById("distance").value = "";
        //document.getElementById('location').value = "";
        document.getElementById("defaultHere").checked = true;
        
        document.getElementById('other').value ="";
        document.getElementById('other').disabled = true;
    }

    
    
    function sendInfo()
    {

        document.getElementById("container").innerHTML = "";
        document.getElementById("test").innerHTML = "";
        document.getElementById("map").innerHTML = "";
        
        if(document.getElementById('keyword').value.length == 0)
        {
            document.getElementById('keyword').required = true;
            return;
        }
        
        var keyword = document.getElementById("keyword").value.trim();
        var location = document.getElementById("defaultHere").value.trim();
        var distance = document.getElementById("distance").value.trim();
        var catergory = document.getElementById("catergory").value.trim();
        //console.log(distance);
        
        if(document.getElementById("locationOther").checked == true)
        {
            if(document.getElementById("other").value.length == 0)
            {
                document.getElementById("other").required = true;
                return;
                
            }
            else
                location = document.getElementById("other").value.trim();
        }
        
        var http = new XMLHttpRequest();
        var url = "/final.php";
        var params = "keyword=" + keyword + "&distance=" +distance +"&catergory=" +catergory+"&distance="+distance +"&location="+location;
        var params2 = "keyword=" + keyword + "&distance=" +distance +"&catergory=" +catergory+"&distance="+distance +"&location="+location +"&lat="+lati +"&lng=" +lgn;
        http.open("POST", url, true);

        //Send the proper header information along with the request
        http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        http.onreadystatechange = function() {//Call a function when the state changes.
            if(http.readyState == 4 && http.status == 200) {
                data = http.responseText;
                //document.getElementById("test").innerHTML=data;
                console.log(data);
                show_table(data);
                //alert(http.responseText);
            }
        }
        
        if(document.getElementById("locationOther").checked == true)
            http.send(params);
        else
            http.send(params2);
    }
    
</script>
    
<div id="floating-panel">
<select id="mode" size="3">
  <option value="WALKING" checked>Walk there</option>
  <option value="BICYCLING">Ride there</option>
  <option value="DRIVING">Drive there</option>
</select>
</div>
    
<!-- HTML-->
<div class="box">
    <div class="content">
        <h1 id="title"><i>Travel and Entertainment Search</i></h1>
        <form id="myForm" method="post"> 
            <b>Keyword</b> <input type="text" name="keyword" id="keyword" required>
            <br><br>
            <b>Category</b> <!--<input type="text" name="catergory" >-->
            <select name="catergory" id="catergory">
                <option value="default" selected>default </option>
                <option value="cafe">cafe </option>
                <option value="bakery"> bakery</option>
                <option value="restaurant">restaurant </option>
                <option value="beauty-salon">beauty salon</option>
                <option value="casino">casino </option>
                <option value="mobie">movie theater </option>
                <option value="lodging">lodging </option>
                <option value="airport">airport </option>
                <option value="train">train station</option>
                <option value="subway">subway station</option>
                <option value="bus">bus station</option>
            </select>
            
           <br><br>
            
            <div class="divinline">
               <b>Distance(miles)</b> <input type="text" name="distance" id="distance" placeholder="10" pattern="[0-9]+">
               <b>from</b> <br><br>
            </div>
            
            <div class="divinline">
                <input type="radio" name="location" id="defaultHere" checked value="current"><b>Here</b><br>
                <input type="radio" name="location" id="locationOther" >
                <input type="text" name="location" id="other" placeholder="location">
            </div>
            <br><br>
            <div class="button-collection">
                <button name="search" type="button" id="search"  onclick="sendInfo();">search</button>
                <button name="clear" type="button" onclick="javascript:myReset();">Clear</button>
            </div>
        </form>
    </div>
</div>

       
<div id="container">
    <!--加了下面那一段PHP之后，这一段就失效了？？？？-->
     
</div>
    
<div id="test"></div>
    
<div id="map">
 
</div>
    

    

    


</body>
</html>





