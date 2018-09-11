(function (angular) {

    //注册一个模块，2.这个模块依赖哪些模块
var detailsModule = angular.module("myApp.detailsModule", ['ngRoute', 'myApp.resultsModule']);

detailsModule.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/showdetails',{
        templateUrl:'detailsView.html',
        controller:'detailsController'
        
    });
}]);
    
detailsModule.service('detailsService', function(resultsService) {
    this.getValue = function() {
        return resultsService.getValue();
    };

    this.setValue = function() {
        resultsService.setValue('New value');
    };
});
    
    //注册一个主要的控制器
detailsModule.controller("detailsController", ['$scope','$routeParams','$http','$q', 'detailsService','$window','$rootScope','$location',function($scope, $routeParams, $http, $q, detailsService, $window,$rootScope, $location) {
    /*------------Animation--------------------*/
        $scope.pageClass = 'details-view';
    
    $rootScope.slide = $rootScope.slide || 'slide_from_left_to_right';
  
    //控制器分两步，1.设计暴露数据，2.设计暴露行为
    //var Obj = $scope.$parent.data;
    //var index= $routeParams.index;
    //console.log(index);
    //$scope.detailData;
    $scope.clickInfo = true;
    $scope.clickMap = false;
    $scope.clickPhotos = false;
    $scope.clickReview = false;
    
   // console.log($routeParams.placeId );
    
    //2.设计暴露行为
    
    //console.log($scope.detailData);
    var data = detailsService.getValue();
    console.log(data);
    $scope.title = data.name;
    //console.log($scope.title);
    
    //convert priceCount to $
    if('price_level' in data)
        $scope.priceCount = data.price_level;
    else
        $scope.priceCount = 0;
    $scope.getPrice = function(num){
        return new Array(num);
    };
    
    if('international_phone_number' in data)
        $scope.phoneNumberState = true;
    else
        $scope.phoneNumberState = false;
    
    if('url' in data)
        $scope.googlePageState = true;
    else
        $scope.googlePageState = false;
    
    if('website' in data)
        $scope.websiteState = true;
    else
        $scope.websiteState = false;
    
    //convert starCount to ※
    $scope.Math = window.Math;
    $scope.ratingExist = false;
    if("rating" in data)
    {
        $scope.ratingExist = true;
        $scope.starCount = Math.floor(data.rating);
        $scope.starRem = data.rating - $scope.starCount;
        $scope.starPercentage = $scope.starRem * 100;
        //console.log("work");
    }
    else{
        $scope.ratingExist = false;
        $scope.starCount = 0;
        $scope.starRem = 0;
    }
    
    //console.log(data.rating);
    
    $scope.getStar = function(num){
        return new Array(num);
    };
    
    //getHour
    var today = moment();
    var tmpArray = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
    var getIndex = function(monday){
        return tmpArray.indexOf(monday);
    };
    
    if(('opening_hours' in data) && ('open_now' in data.opening_hours))
    {
        $scope.openOrclose = data.opening_hours.open_now;
        $scope.weekArray = data.opening_hours.weekday_text;
        $scope.weeks = [];
        $scope.todayWeek = today.format("dddd");
        for(var i = 0; i < tmpArray.length; i++)
        {
            var dayString = $scope.weekArray[i];
            var dayOpenTime = dayString.substring(tmpArray[i].length + 1);
            var item = {"day":tmpArray[i], "time":dayOpenTime};
            
            $scope.weeks.push(item);
        }
        $scope.hourHandle = true;
    }
    else
    {
        $scope.openOrclose = false;
        $scope.hourHandle = false;
    }
    
    
    $scope.getHour = function(hour){
        
        if(hour === true)
        {
            var timeState = "Open Now: ";
            var todayWeek = today.format("dddd");
            $scope.weekArray = data.opening_hours.weekday_text;
            var dayIndex = getIndex(todayWeek);
            
            var dayString = $scope.weekArray[dayIndex]; 
            var dayOpenTime = dayString.substring(todayWeek.length + 1);
            
            return timeState + dayOpenTime;
            //var time = 
            
        }
        else{
            return "Close";  
        }
        
    };
    
    //Daily Open Hours
    $scope.getWeek = function(){
        //$scope.weekArray = data.opening_hours.weekday_text;
        return $scope.weeks;
    };
    
   
    //get Photos
    if('photos' in data)
    {
        $scope.photosArray = [];
        //var photo = data.photos[0].getUrl({'maxWidth': data.photos[0].width, 'maxHeight': data.photos[0].height});
        
        for(var m = 0; m < data.photos.length; m++)
        {
            var photoUrl = data.photos[m].getUrl({'maxWidth': data.photos[m].width, 'maxHeight': data.photos[m].height});
            $scope.photosArray.push(photoUrl);
        }
        //console.log($scope.photosArray);
    }
    else
    {
        $scope.photosArray = [];
    }
    
    $scope.getPhotos = function() {
        return $scope.photosArray;
    };
    
    //get Map
    var targetLat = data.geometry.location.lat();
    var targetLng = data.geometry.location.lng();
    //$scope.targetLocation = 
    
    var parentData = $rootScope.data; //console.log(parentData);
    var originLatLng = parentData.geolocation; 
    var originArray = originLatLng.split(",");
    var originLat = parseFloat(originArray[0]);//console.log(originLat);
    var originLng = parseFloat(originArray[1]);//console.log(originLng);
    $scope.clickGetDirection = false;
    
    $scope.fromLocationDetail = "";
    if($scope.$parent.formData.locationOther !== '')
        $scope.fromLoc = $scope.$parent.formData.locationOther;
    else
        $scope.fromLoc = "Your location";
    $scope.searchStart = "";
    $scope.toLoc = data.name +", "+ data.formatted_address;
    $scope.mode = "DRIVING";
    
    var mapImg = "https://png.icons8.com/color/50/000000/google-maps.png";
    var yellowMan = "http://cs-server.usc.edu:45678/hw/hw8/images/Pegman.png";
    $scope.streetImg = yellowMan;
    
    $scope.checkEmpty = false;
    $scope.$watch('fromLoc',function(){
        if($scope.fromLoc.length === 0 || $scope.fromLoc === "undefined")
        {
            $scope.checkEmpty = true;
            //console.log("empty work");
        }
        else{
                $scope.checkEmpty = false;
                if($scope.fromLoc === "Your location" || $scope.fromLoc === "My location" || $scope.fromLoc ==="my location" || $scope.fromLoc === "your location")
                {
                    $scope.searchStart = {lat: originLat,lng: originLng};
                }
                else
                {
                    $scope.searchStart = $scope.fromLoc;
                }
                //console.log("work");
            }
    } );
    
    
    
    //console.log($scope.toLoc);
    //console.log(originLat);
    
    
     var mapOptions = {
      zoom: 12,
      center: new google.maps.LatLng(originLat,originLng),
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(document.getElementById('map'), mapOptions);
    
    
    var createMarker = function(lat, lng){
        $scope.originMarker = new google.maps.Marker({
            map: map,
            position: new google.maps.LatLng(lat, lng)
        });
    };
    
    //设定初始marker
    createMarker(originLat, originLng);
    //$scope.directionError = false;
    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay = new google.maps.DirectionsRenderer({panel:document.getElementById('panel')});
    
    directionsDisplay.setMap(map);
    
    function calculateAndDisplayRoute(directionsService, directionsDisplay) {
        //$scope.directionError = false;
      directionsService.route({
        origin: $scope.searchStart,
        destination: $scope.toLoc,
        travelMode: $scope.mode,
        provideRouteAlternatives:true
      }, function(response, status) {
        if (status === 'OK') {
          directionsDisplay.setDirections(response);
            //$scope.directionError = false;
        } else {
         window.alert('Directions request failed due to ' + status);
            //$scope.directionError = true;
        }
      });
    }
    
    //get direction
    $scope.getDirection = function(){
        $scope.originMarker.setMap(null);
//        if (directionsDisplay !== null) {
//            directionsDisplay.setMap(null);
//            //directionsDisplay.setDirections(null);
//        }
        
        
        calculateAndDisplayRoute(directionsService, directionsDisplay);
        $scope.clickGetDirection = true;
        
    };
    
    //toggle street view 
    var panorama = map.getStreetView();
    panorama.setPosition({lat:targetLat, lng:targetLng});
    $scope.showStreetView = function(){
        var toggle = panorama.getVisible();
        if(toggle === false)
        {
            panorama.setVisible(true);
            $scope.streetImg = mapImg;
        }
        else
        {
            panorama.setVisible(false);
            $scope.streetImg = yellowMan;
        }
  
    };
    
    console.log($scope.fromLocationDetail);   
   // console.log(originLatLng);
    
    //reviews part
    $scope.reviewType = "google";
    $scope.reviewOrder = "default";
    $scope.googleArray =[];
    $scope.yelpArray =[];
    
    //google reviews
    $scope.googleReviewUndefined = false;
    if('reviews' in data)
    {
        var reviewData = data.reviews;
        $scope.googleReviewUndefined = false;
        for(var k = 0; k < data.reviews.length; k++)
        {
            var author_url = reviewData[k].author_url;
            var profile_photo_url = reviewData[k].profile_photo_url;
            var author_name = reviewData[k].author_name;
            var author_rating = reviewData[k].rating;
            var review_time = reviewData[k].time;
            var review_text = reviewData[k].text;
            
            var reviewItem = {author_url: author_url, profile_photo_url:profile_photo_url,author_name: author_name, author_rating:author_rating, review_time:review_time, review_text:review_text};
            
            $scope.googleArray.push(reviewItem);
            
        }
        
    }
    else
    {
         $scope.googleReviewUndefined = true;
    }
    
    //yelp reviews
    ///yelp/:name/:address1/:address2/:city/:state/:country
    
    //reviewItem = {author_url: author_url, profile_photo_url:profile_photo_url,author_name: author_name, author_rating:author_rating, review_time:review_time, review_text:review_text};
    var addressArray = data.formatted_address.split(",");
    $scope.yelpReviewUndefined = false;
    //console.log(addressArray);
    $http({
          method: 'GET',
          url: 'http://travelsearchnode-env.us-east-2.elasticbeanstalk.com/yelp/' + data.name + "/" + addressArray[0] + "/" + addressArray[2] + "/" + addressArray[1] + "/" + addressArray[2] + "/" + addressArray[3],
          headers: {'Content-Type': 'application/json'},
          //params:p1
        }).then(function successCallback(response) {
                //console.log(yelpData);
                if(response.data === 'undefined')
                {
                    $scope.yelpReviewUndefined = true;
                    
                }
                else
                {
                    $scope.yelpReviewUndefined = false;
                    var yelpData = response.data;
                    
                    for(var a = 0; a < yelpData.length; a++)
                    {
                        var yelp_author_url = yelpData[a].url;
                        var yelp_profile_photo_url = yelpData[a].user.image_url;
                        var yelp_author_name = yelpData[a].user.name;
                        var yelp_author_rating = yelpData[a].rating;
                        var yelp_review_time = yelpData[a].time_created;
                        var yelp_review_text = yelpData[a].text;
                        
                        var yelp_reviewItem = {author_url: yelp_author_url, profile_photo_url:yelp_profile_photo_url,author_name: yelp_author_name, author_rating:yelp_author_rating, review_time:yelp_review_time, review_text:yelp_review_text};
                        
                        $scope.yelpArray.push(yelp_reviewItem);
                        
                    }
                    
                    //console.log("ahahhaha");  
                }
        
                //console.log(response);

          }, function errorCallback(response) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
             $scope.yelpReviewUndefined = true;
            console.log("yelp connect fail");
          });
    
    
    $scope.turnFade = false;
    $scope.reviewArray = $scope.googleArray;
    //监控reviewType, reviewOrder change
    $scope.$watchGroup(['reviewType','reviewOrder'],function(oldVal, newVal){
        if(oldVal[0] !== newVal[0])
            $scope.turnFade = true;
        else
            $scope.turnFade = false;
        
        if($scope.reviewType === "google")
        {
            //return $scope.googleArray;
            $scope.reviewArray = $scope.googleArray;
            $scope.yelpState = ($scope.reviewType === 'yelp' && $scope.yelpReviewUndefined)? true:false;
            $scope.googleState = ($scope.reviewType === 'google' && $scope.googleReviewUndefined)? true:false;
        }
        else
        {
            //return $scope.yelpArray;
            $scope.reviewArray = $scope.yelpArray;
            $scope.yelpState = ($scope.reviewType === 'yelp' && $scope.yelpReviewUndefined)? true:false;
            $scope.googleState = ($scope.reviewType === 'google' && $scope.googleReviewUndefined)? true:false;
        }
        
//        if($scope.reviewOrder === "")
//        {
//            $scope.reviewOrder = $scope.reviewOrder;
//        }
        
    } );
    
    $scope.yelpState = ($scope.reviewType === 'yelp' && $scope.yelpReviewUndefined)? true:false;
    $scope.googleState = ($scope.reviewType === 'google' && $scope.googleReviewUndefined)? true:false;
    
   //console.log($scope.googleArray);
    $scope.getReview = function(reviewType){
        
        if($scope.reviewType === "google")
        {
            return $scope.googleArray;
        }
        else
        {
            return $scope.yelpArray;
        }
        
        //return $scope.reviewsArray;
    };
    
    
    $scope.getTimeStamp = function(timeStamp){
        if($scope.reviewType === 'google')
            return moment(timeStamp*1000).format("YYYY-MM-DD HH:mm:ss");
        else
            return timeStamp;
    };
    
    if("reviews" in data && "rating" in data.reviews)
    {
        //$scope.ratingExist = true;
        $scope.reviewStarCount = Math.floor(data.reviews.rating);
        $scope.reviewStarRem = data.reviews.rating - $scope.reviewStarCount;
        $scope.reviewStarPercentage = $scope.reviewStarRem* 100;
        //console.log("work");
    }
    else{
        //$scope.ratingExist = false;
        $scope.reviewStarCount = 0;
        $scope.reviewStarRem = 0;
    }
    
    //List: goHistoryBack()
    $scope.goHistoryBack = function() {
        $rootScope.slide = '';
        $rootScope.goSlide ='';
        $rootScope.slide = 'slide_from_right_to_left';
        window.history.back();
    };
    
    //Twitter:https://twitter.com/intent/tweet?text=Hello%20world
    $scope.getHref = function(){
        var text = "Check out " + data.name +" located at " + data.formatted_address + ". Website: ";
        var twUrl = data.website;
        
            
        var href = "https://twitter.com/intent/tweet?text=" + text + "&url=" + twUrl + "&hashtags=TravelAndEntertainmentSearch";
        return href;
    };
    
    //add results and place's detail to local storage
    //$scope.tableDetails = {};
    //if(typeof $scope.detailStar === 'undefined')
            //$scope.detailStar = false;
    if($rootScope.clickThisStar[$rootScope.currentPage][$rootScope.currentIndex])
        $scope.detailStar = true;
    else
        $scope.detailStar = false;
    
    $scope.addToFav = function(){
        var placeItemId = data.place_id;
        $rootScope.fromAddFavDetail = true;
        if($scope.detailStar === true)
        {
            $window.localStorage.removeItem(placeItemId);
                
            $rootScope.clickThisStar[$rootScope.currentPage][$rootScope.currentIndex] = false;
            $scope.detailStar = false;
        }
        else
        {
            //重置
            $rootScope.clickFavDetail = [];
            for(var q = 0; q < Math.ceil($window.localStorage.length/20); q++)
            {
                $rootScope.clickFavDetail[q] = [];
                for(var w = 0; w < 20 && (w+ 20*q) < $window.localStorage.length; w++)
                {
                    $rootScope.clickFavDetail[q][w] = false;
                }
            }

                for(var a = 0; a < $window.localStorage.length; a++)
                {
                        var key = $window.localStorage.key(a);
                        var value = JSON.parse($window.localStorage.getItem(key));

                        value.detailStatus = false;
                        $window.localStorage.setItem(key, JSON.stringify(value));
                }


           // var key = "tableData";
            //var placeDetail = {};
            var infoPhotoMapPart = data; //object
            //var photoPart = $scope.photosArray; //array
            var googleReviews = $scope.googleArray;
            var yelpReviews = $scope.yelpArray; //yelpReviews
            var placeItemData = {data:infoPhotoMapPart, googleReviews:googleReviews, yelpReviews:'no', photosArray:$scope.photosArray, timestamp:new Date().getTime(), detailStatus:true};
            //var placeItemIndex = 0;

            $window.localStorage.setItem(placeItemId, JSON.stringify(placeItemData));
            var pageIndex = $rootScope.currentPage;
            $scope.detailStar = true;

            var lastPage = Math.ceil($window.localStorage.length/20);
            //console.log($rootScope.clickFavDetail);
            if(lastPage === 1 && $rootScope.clickFavDetail.length === 0)
                    $rootScope.clickFavDetail[0] = [true];
            else
            {
                if(typeof $rootScope.clickFavDetail[lastPage - 1] === 'undefined')
                    $rootScope.clickFavDetail[lastPage - 1] = [];

                    $rootScope.clickFavDetail[lastPage - 1].push(true);
            }

            $rootScope.favDetailDisabled = false;

            //console.log($scope.photosArray);
            $rootScope.clickThisStar[$rootScope.currentPage][$rootScope.currentIndex] = true;
            //console.log($window.localStorage.getItem(placeItemId));
           // var tableData = $window.localStorage.getItem('tableData');
            //console.log($rootScope.clickThisStar[0][0]);

            
        }
        
    };
    
    
    //最后赋值！！！！
    $scope.detailData = data;
        
    
    

    /*-----------------show Review-------------------*/
    $scope.showReview = function(){
        $scope.clickInfo = false;
        $scope.clickMap = false;
        $scope.clickPhotos = false;
        $scope.clickReview = true;
        
        //console.log("review");
    };
    
    /*----------------------Info-------------------------*/
    $scope.showInfo = function(){
        $scope.clickInfo = true;
        $scope.clickMap = false;
        $scope.clickPhotos = false;
        $scope.clickReview = false;
        
        console.log("info");
    };
    
    /*--------------------Map-------------------------------*/
    $scope.showMap = function() {
        $scope.clickInfo = false;
        $scope.clickMap = true;
        $scope.clickPhotos = false;
        $scope.clickReview = false;
        
        //console.log("map");
    };
    
    
    /*---------------------Photos-------------------------------*/
    $scope.showPhotos = function() {
        $scope.clickInfo = false;
        $scope.clickMap = false;
        $scope.clickPhotos = true;
        $scope.clickReview = false;
        
        //console.log("photos");
    };

  
}]);
 
detailsModule.directive('fromplace', [ function() {
    return {
        require: 'ngModel',
        scope: {
            ngModel: '=',
            details: '=?'
        },
        link: function(scope, element, attrs, model) {
            var options = { types: ['address'] };

            scope.gPlace = new google.maps.places.Autocomplete(element[0], options);

            google.maps.event.addListener(scope.gPlace, 'place_changed', function() {
                var geoComponents = scope.gPlace.getPlace();
                var latitude = geoComponents.geometry.location.lat();
                var longitude = geoComponents.geometry.location.lng();
                var placeId = geoComponents.place_id;
                var addressComponents = geoComponents.address_components;

                addressComponents = addressComponents.filter(function(component){
                    switch (component.types[0]) {
                        case "locality": // city
                            return true;
                        case "administrative_area_level_1": // state
                            return true;
                        case "country": // country
                            return true;
                        default:
                            return false;
                    }
                }).map(function(obj) {
                    return obj.long_name;
                });

                addressComponents.push(latitude, longitude, placeId);

                scope.$apply(function() {
                    scope.details = addressComponents; // array containing each location component
                    //console.log(scope.details);
                    scope.$parent.fromLocationDetail = scope.details;
                    model.$setViewValue(element.val());
                    //console.log();
                });
            });
        }
    };
}]);
    


    
})(angular);