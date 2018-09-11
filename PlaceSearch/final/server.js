var express = require("express");
var bodyParser = require("body-parser");
var cors = require('cors');
var queryString = require('query-string');
var request = require("request");
var yelp = require('yelp-fusion');


var app = express();


//路由是“/”时
/*app.get("/", function(req, res){
    res.send("hahahahhah");
});

//路由是"/showresults"时
app.get("/showresults", function(req, res){
    res.send("Results 结果");
});

app.get(/^\/student\/([\d]{10})$/, function(req, res){
    res.send("student info: " + req.params[0]);
});

app.get("/teacher/:info", function(req, res){
    res.send("teacher infor:" + req.params["info"]);
});*/

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());

app.all('*', function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    res.header("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
    next();
});

var apiKey = "e-SOqSix47MTX_9SR2ajbo6uv6QfoMcIsTkvEj_2vA441CyexXD4SSaj4X8-1U-5jdBZjZ9B_6XZ-thFwxyfqXK5m25i3xl_UaAS7zYV2hNAYZQkximPW1TcQ_-6WnYx";
var client = yelp.client(apiKey);

var searchData;


app.get('/showresults', function(req, res) {
    console.log('Processing request...');
    var keyword, catergory, distance, location,geolocation, locationOther;
    var lat, lng;
    var key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";

    var gotData = req.url.substr(12);
    var dataObj = queryString.parse(gotData);

    keyword = dataObj.keyword;
    catergory = dataObj.catergory;
    distance = dataObj.distance;
    location = dataObj.location;
    locationOther = dataObj.locationOther;
    geolocation = dataObj.geolocation;

    if(distance === "")
        distance = 10*1609;
    else
        distance = distance*1609;

    //console.log(location);
    if(location !== '0')
    {
        if(geolocation !== ""){
            keyword = keyword.replace(/\s+/g, "+");
            var searchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ geolocation + "&radius=" + distance + "&type=" + catergory + "&keyword=" + keyword + "&key=" + key;

            request.get(searchUrl, function(error, response, body) {
                var searchContent = JSON.parse(body);
                searchContent.geolocation = geolocation;
                searchData = searchContent;

                console.log("Bank Tower works....");
                res.send(searchData);
            });


            //res.send("hahahah");
        }
        if(geolocation === ""){
            var p ="https://maps.googleapis.com/maps/api/geocode/json?address=" + locationOther + "&key=AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";

            var url = p;
            request.get(url, function(error, response, body) {
                //response.setHeader("Content-Type", "application/json");
                //response.write(200, {"Content-type":"application/json; charset=utf-8"});

                var json = JSON.parse(body);

                if(json.results.length !== 0){
                    lat = json.results[0].geometry.location.lat;
                    lng = json.results[0].geometry.location.lng;

                    geolocation = lat + "," + lng;
                    //getSearch(geolocation, distance, catergory, keyword, key);
                    keyword = keyword.replace(/\s+/g, "+");
                    var searchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ geolocation + "&radius=" + distance + "&type=" + catergory + "&keyword=" + keyword + "&key=" + key;

                    request.get(searchUrl, function(error, response, body) {
                        var searchContent = JSON.parse(body);
                        searchContent.geolocation = geolocation;
                        searchData = searchContent;

                        console.log("usc works....");
                        res.send(searchData);
                    });

                    //console.log("good");
                    //res.send("你已经收到数据了" + geolocation);
                    //return;
                  }
                  else
                  {
                      searchData = {};
                      searchData.results = [];
                      res.send(searchData);
                  }

                //console.log(json.results[0]);
            });

        }
    }//选择Here

    if(location === '0')
    {
        keyword = keyword.replace(/\s+/g, "+");
        var searchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ geolocation + "&radius=" + distance + "&type=" + catergory + "&keyword=" + keyword + "&key=" + key;

        request.get(searchUrl, function(error, response, body) {
            var searchContent = JSON.parse(body);
            searchContent.geolocation = geolocation;
            searchData = searchContent;

            console.log("Here works....");
            res.send(searchData);
        });

    }


   //console.log(locationOther);
   // res.send(geolocation);
   //res.send("来自服务器的Results结果");
});



app.get('/pages/:page', function(req, res) {
   console.log('Processing request...');
   //console.log(req.url);

   var nextPageToken = req.params.page;
   var key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
   var pageUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + nextPageToken + "&key=" + key;
   request.get(pageUrl, function(error, response, body) {
        var pageContent = JSON.parse(body);

        //console.log(pageContent);
        console.log("Page works....");
        res.send(pageContent);
   });

   //res.send("来自服务器Page结果");
});

app.get('/yelp/:name/:address1/:address2/:city/:state/:country', function(req, res) {
   console.log('Processing request...');
   //console.log(req.url);

    //console.log(req.params.address2);
    var yelpData = req.params;
    var stateStr = yelpData.address2.split(/ /);
    //console.log(stateStr); //[ '', 'CA', '90014' ]
    //console.log(stateStr[1]);

    client.businessMatch('best', {
          name: yelpData.name,
          address1: yelpData.address1,
          address2: yelpData.address2,
          city: yelpData.city,
          state: stateStr[1],
          country: "US"
        }).then(function(response){
              //console.log(response.jsonBody.businesses[0]);
              //console.log("work");

              if(response.jsonBody.businesses[0])
              {
                  client.reviews(response.jsonBody.businesses[0].id).then(function(response) {

                      res.send(response.jsonBody.reviews);
                      console.log("review works");
                      //console.log(response.jsonBody);
                    }).catch(function(e) {
                      console.log(e);
                    });
              }
              else
             {
                 res.send("undefined");
                 console.log("it is undefined");
             }
              //res.send(response.jsonBody.businesses[0]);

        }).catch(function(e) {
          console.log(e);
        //console.log("not work");
        });

   //res.send("来自服务器的Yelp的结果");
});


app.get('/detail/:placeId', function(req, res) {
   console.log('Processing request...');
   //console.log(req.url);


   res.send("来自服务器的Detail结果");
});


app.get('/showfavorites', function(req, res) {
   console.log('Processing request...');
   console.log(req.url);

//   var key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
//   var pageUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + nextPageToken + "&key=" + key;
//   request.get(pageUrl, function(error, response, body) {
//        var pageContent = JSON.parse(body);
//
//        //console.log(pageContent);
//        console.log("Page works....");
//        res.send(pageContent);
//   });


   res.send("来自服务器的Favorite结果");
});

app.listen(8081);
