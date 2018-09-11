(function (angular) {

    //注册一个模块，2.这个模块依赖哪些模块
var favoriteDetailModule = angular.module("myApp.favoriteDetailModule", ['ngRoute','myApp.favoritesModule']);

favoriteDetailModule.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/favdetails',{
        templateUrl:'detailsView.html',
        controller:'favDetailController'
        
    });
}]);
    
favoriteDetailModule.service('favDetailService', function(favService) {
    this.getValue = function() {
        return favService.getValue();
    };

    this.setValue = function() {
        favService.setValue('New value');
    };
});
    
    //注册一个主要的控制器
favoriteDetailModule.controller("favDetailController", ['$scope','$window','$location','favDetailService','$rootScope','$http',function($scope, $window, $location,favDetailService,$rootScope,$http) {
    /*--------------Animation---------------*/
        $scope.pageClass = 'favoriteDetail-view';
    //分步控制
    $scope.clickInfo = true;
    $scope.clickMap = false;
    $scope.clickPhotos = false;
    $scope.clickReview = false;
    
    //$scope.detailStar = true;
    
    var detail = favDetailService.getValue();
    
    console.log(detail);
    
    $scope.detailData = detail.result.data;
    
    $scope.title = $scope.detailData.name;
    if('price_level' in $scope.detailData)
        $scope.priceCount = $scope.detailData.price_level;
    else
        $scope.priceCount = 0;
    
    $scope.getPrice = function(num){
        return new Array(num);
    };
    
    
    if('international_phone_number' in $scope.detailData)
        $scope.phoneNumberState = true;
    else
        $scope.phoneNumberState = false;
    
    if('url' in $scope.detailData)
        $scope.googlePageState = true;
    else
        $scope.googlePageState = false;
    
    if('website' in $scope.detailData)
        $scope.websiteState = true;
    else
        $scope.websiteState = false;
    
    //convert starCount to ※
    $scope.Math = window.Math;
    $scope.ratingExist = false;
    if("rating" in $scope.detailData)
    {
        $scope.ratingExist = true;
        $scope.starCount = Math.floor($scope.detailData.rating);
        $scope.starRem = $scope.detailData.rating - $scope.starCount;
        $scope.starPercentage = $scope.starRem * 100;
        //console.log("work");
    }
    else{
        $scope.ratingExist = false;
        $scope.starCount = 0;
        $scope.starRem = 0;
    }
    
    $scope.getStar = function(num){
        return new Array(num);
    };
    
    //getHour
    var today = moment();
    var tmpArray = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
    var getIndex = function(monday){
        return tmpArray.indexOf(monday);
    };
    
    if(('opening_hours' in $scope.detailData) && ('open_now' in $scope.detailData.opening_hours))
    {
        $scope.openOrclose = $scope.detailData.opening_hours.open_now;
        $scope.weekArray = $scope.detailData.opening_hours.weekday_text;
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
            $scope.weekArray = $scope.detailData.opening_hours.weekday_text;
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
   if(detail.result.photosArray.length !== 0)
    {
        $scope.photosArray = [];
        //var photo = data.photos[0].getUrl({'maxWidth': data.photos[0].width, 'maxHeight': data.photos[0].height});
//        
//        for(var m = 0; m < $scope.detailData.photos.length; m++)
//        {
//            var photoUrl = $scope.detailData.photos[m].getUrl({'maxWidth': $scope.detailData.photos[m].width, 'maxHeight': $scope.detailData.photos[m].height});
//            $scope.photosArray.push(photoUrl);
//        }
        $scope.photosArray = detail.result.photosArray;
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
    var targetLat = $scope.detailData.geometry.location.lat;//??????????????
    var targetLng = $scope.detailData.geometry.location.lng;
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
    $scope.toLoc = $scope.detailData.name +", "+ $scope.detailData.formatted_address;
    $scope.mode = "DRIVING";
    
    var mapImg = "https://png.icons8.com/color/50/000000/google-maps.png";
    var yellowMan = "http://www.pvhc.net/img22/moawaqcbhovgehpljceh.jpg";
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
    
    createMarker(originLat, originLng);
    
    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay = new google.maps.DirectionsRenderer({panel:document.getElementById('panel')});
    
    function calculateAndDisplayRoute(directionsService, directionsDisplay) {
      directionsService.route({
        origin: $scope.searchStart,
        destination: $scope.toLoc,
        travelMode: $scope.mode,
        provideRouteAlternatives:true
      }, function(response, status) {
        if (status === 'OK') {
          directionsDisplay.setDirections(response);
        } else {
          //window.alert('Directions request failed due to ' + status);
        }
      });
    }
    
    //get direction
    $scope.getDirection = function(){
        $scope.originMarker.setMap(null);
        if (directionsDisplay !== null) {
            directionsDisplay.setMap(null);
            //directionsDisplay.setDirections(null);
        }
        directionsDisplay.setMap(map);
        
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
    
    
    //reviews part
    $scope.reviewType = "google";
    $scope.reviewOrder = "default";
    $scope.googleArray =[];
    $scope.yelpArray =[];
    
    //google reviews
    $scope.googleReviewUndefined = false;
    if(detail.result.googleReviews.length !== 0)
    {
        //var reviewData = data.reviews;
        $scope.googleReviewUndefined = false;
        $scope.googleArray = detail.result.googleReviews;
//        for(var k = 0; k < data.reviews.length; k++)
//        {
//            var author_url = reviewData[k].author_url;
//            var profile_photo_url = reviewData[k].profile_photo_url;
//            var author_name = reviewData[k].author_name;
//            var author_rating = reviewData[k].rating;
//            var review_time = reviewData[k].time;
//            var review_text = reviewData[k].text;
//            
//            var reviewItem = {author_url: author_url, profile_photo_url:profile_photo_url,author_name: author_name, author_rating:author_rating, review_time:review_time, review_text:review_text};
//            
//            $scope.googleArray.push(reviewItem);
//            
//        }
        
    }
    else
    {
         $scope.googleReviewUndefined = true;
    }
    
    //yelp reviews
    ///yelp/:name/:address1/:address2/:city/:state/:country
    
    //reviewItem = {author_url: author_url, profile_photo_url:profile_photo_url,author_name: author_name, author_rating:author_rating, review_time:review_time, review_text:review_text};
    var addressArray = $scope.detailData.formatted_address.split(",");
    $scope.yelpReviewUndefined = false;
    //console.log(addressArray);
    if(detail.result.yelpReviews === 'no')
    {
        $http({
          method: 'GET',
          url: 'http://travelsearchnode-env.us-east-2.elasticbeanstalk.com/yelp/' + $scope.detailData.name + "/" + addressArray[0] + "/" + addressArray[2] + "/" + addressArray[1] + "/" + addressArray[2] + "/" + addressArray[3],
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
    }
    else
    { //没有意义
        if(detail.result.yelpReviews.length !== 0)
        {
            $scope.yelpArray = detail.result.yelpReviews;
        }
        else
        {
            $scope.yelpReviewUndefined = true;
        }
        
    }
    
    
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
            return moment(timeStamp*1000).format("YYYY-MM-DD h:mm:ss");
        else
            return timeStamp;
    };
    
    if("reviews" in $scope.detailData && "rating" in $scope.detailData.reviews)
    {
        //$scope.ratingExist = true;
        $scope.reviewStarCount = Math.floor($scope.detailData.reviews.rating);
        $scope.reviewStarRem = $scope.detailData.reviews.rating - $scope.reviewStarCount;
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
        $rootScope.goSlide = 'slide-right';
        window.history.back();
    };
    
    //Twitter:https://twitter.com/intent/tweet?text=Hello%20world
    $scope.getHref = function(){
        var text = "Check out " + $scope.detailData.name +" located at " + $scope.detailData.formatted_address + ". Website: ";
        var twUrl = $scope.detailData.website;
        
            
        var href = "https://twitter.com/intent/tweet?text=" + text + "&url=" + twUrl + "&hashtags=TravelAndEntertainmentSearch";
        return href;
    };
    
    
    /*------------------星星点击--------------------*/
    //$scope.detailStar = false;
    //star on: click off; 
//    $scope.addToFav= function(){
//        if($scope.detailStar === false)
//        {
//            $scope.detailStar = true;
//        }
//        else
//        {
//            $scope.detailStar = false;
//            //$window.localStorage.removeItem()
//            var keyPlaceId = detail.result.data.place_id;
//            $window.localStorage.removeItem(keyPlaceId);
//        }
//        
//    };  
    
    //处理detail里的星星亮起
   /* if('fromResult' in detail.result)
         $scope.detailStar = true;
    else
        $scope.detailStar = false;*/
    
    $scope.detailStar = true;
    
//  if($rootScope.clickThisStar[$rootScope.currentPage][$rootScope.currentIndex])
//        $scope.detailStar = true;
//    else
//        $scope.detailStar = false;
    
    //addToFav: when star 'on', delete, when star 'off'
    $scope.addToFav = function(){
       if($scope.detailStar ===  true)
        {
            $scope.detailStar = false;
            var index = $rootScope.lastClickFavDetail;
            var referKey = detail.result.data.place_id;
            
            $scope.tmpFavTrueFalse = $rootScope.clickFavDetail;
        
            $scope.tmpArray = [];
        
            $window.localStorage.removeItem(referKey);
        
        
        for(var j = 0; j < $scope.tmpFavTrueFalse.length; j++)
        {
            for(var k = 0; k < $scope.tmpFavTrueFalse[j].length; k++)
            {
                $scope.tmpArray.push($scope.tmpFavTrueFalse[j][k]);
            }
        }
        var abb = $scope.tmpArray.pop();
        //console.log(abb);
       //T/F after removing item 
        var tmpIndex = $rootScope.favCurrentPage * 20 + index;
        for(var m = tmpIndex; m < $scope.tmpArray.length; m++)
        {
            $scope.tmpArray[m] = $scope.tmpArray[m+1];
        }
        //$scope.tmpArray = $scope.tmpArray.pop();
       // console.log($scope.tmpArray);
        
        var TFArray = [];
        for(var a = 0; a < Math.ceil($scope.tmpArray.length/20); a++)
        {
            var Item = [];
            TFArray[a] = [];
            for(var b = 0; b < 20 && (b + 20*a) < $scope.tmpArray.length; b++)
            {
                Item.push($scope.tmpArray[b + 20*a]);
            }
            TFArray[a] = Item;
        }
        
        $rootScope.clickFavDetail = TFArray;
        $scope.clickThisDetail = $rootScope.clickFavDetail;
            
            
            
        }
    };
    
    //=======================function part=====================//
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

    
    
    
   
    
    //$scope.searchData = $scope.favArray.
    
}]);
    
})(angular);