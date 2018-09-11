/*jslint white:true */
/*global angular */
(function (angular) {

    
var app = angular.module("myApp", ['ngRoute','myApp.resultsModule', 'myApp.favoritesModule', 'myApp.detailsModule','myApp.favoriteDetailModule','ngAnimate']);//注册一个模块，2.这个模块依赖哪些模块

/*app.config(['$routeProvider',function($routeProvider){
    $routeProvider.otherwise({redirectTo:'/index.html'});
}]); ///?????不确定是否需要跳转*/
    
    
    //注册一个主要的控制器
app.controller("myAppCtrl", ['$scope','$location','$http','ipService','$rootScope','$window', function($scope, $location, $http, ipService,$rootScope,$window) {
    /*------------animation----------------*/
    $rootScope.slide = "";
    

    
    //$window.localStorage.clear();///??????????????????
    //console.log($window.localStorage);
    //master 是初始状态
  
    $scope.master = {"keyword": "","catergory":"default","location":0,"distance":"","locationOther":""};
    //进度条
    $scope.loading = false;
    
    //$scope.currentPlaceId;
    $scope.formData  = angular.copy($scope.master);
    $scope.resultsClicked = true;
    $scope.favClicked = false;
    $scope.Hereloc = "";
    $scope.formData.distance = "";
    $scope.formData.locationOther = "";
    //[ "Los Angeles", "California", "United States", 34.020765, -118.2817968, "ChIJsTOpfOHHwoARF9Cv27WRym4" ]
    $scope.otherlocDetails = [];
    $scope.detailData = {};
    
    //$rootScope.detailDisabled = true;//=========================================
    
    if(typeof $rootScope.detailDisabled === 'undefined')
        $rootScope.detailDisabled = true;
    else
        $scope.detailDisabled = $rootScope.detailDisabled;
    //pages
    $rootScope.currentPage = 0;
    $rootScope.pagesData = [];
    $rootScope.finished = false;
    
    //favorite pages
    $rootScope.favCurrentPage = 0;
    $rootScope.favPagesData;
    $rootScope.favDetailDisabled = true;
    $rootScope.notClickfav = true;
    //$scope.data;
    
    //detail row to yellow
    $rootScope.clickThisDetail = [];
    for(var m = 0; m < 3; m++)
    {
        $rootScope.clickThisDetail[m]=[];
        for(var k = 0; k < 20; k++)
        {
            $rootScope.clickThisDetail[m][k] = false;
        }
    }
    //table star to yellow
    $rootScope.clickThisStar = [];
    for(var x = 0; x < 3; x++)
    {
        $rootScope.clickThisStar[x]=[];
        for(var y = 0; y < 20; y++)
        {
            $rootScope.clickThisStar[x][y] = false;
        }
    }
    //console.log($rootScope.clickThisStar[0][0]);
    $scope.Math = window.Math;
    $rootScope.clickFavDetail = [];
    for(var q = 0; q < Math.ceil($window.localStorage.length/20); q++)
    {
        $rootScope.clickFavDetail[q] = [];
        for(var w = 0; w < 20 && (w+ 20*q) < $window.localStorage.length; w++)
        {
            $rootScope.clickFavDetail[q][w] = false;
        }
    }
    
    $scope.$watchCollection('formData',function(){
        if($scope.formData.location === 0 && $scope.formData.locationOther != "undefined")
        {
            $scope.formData.locationOther = "";
        }
    } );
    
    $scope.clearFunc = function(myForm){
        if(myForm){
            myForm.$setPristine();
            myForm.$setUntouched();
        }
        $rootScope.slide = '';
        $rootScope.goSlide = '';
        
        $scope.formData = angular.copy($scope.master);
        $location.path('/');
        $scope.isSubmited = false;
        $scope.resultsClicked = true;
        $scope.favClicked = false;
        $scope.formData.distance = "";
        $scope.otherlocDetails = [];
        $scope.loading = false;
        
        //$scope.data = {};
        $rootScope.detailDisabled = true;
        //重置page 参数
        $rootScope.currentPage = 0;
        $rootScope.pagesData = [];
        $rootScope.finished = false;
    
        //fav pagination
        $rootScope.favCurrentPage = 0;
        $rootScope.favPagesData = [];
    };
    
    $scope.searchSuccess = true;
	// function to submit the form after all validation has occurred
    $scope.isSubmited = false;
    $scope.submitForm = function(path) {
        $rootScope.slide = '';
        //重置参数
        $scope.searchSuccess = true;
        $location.path('/');
        $scope.isSubmited = false;
        $scope.resultsClicked = true;
        $scope.favClicked = false;
        //$scope.formData.distance = "";
        $scope.otherlocDetails = [];
        $scope.loading = false;
        
        $rootScope.detailDisabled = true;
        //重置page 参数
        $rootScope.currentPage = 0;
        $rootScope.pagesData = [];
        $rootScope.finished = false;
    
        //fav pagination
        $rootScope.favCurrentPage = 0;
        $rootScope.favPagesData = [];
        
        $rootScope.clickThisDetail = [];
        for(var m = 0; m < 3; m++)
        {
            $rootScope.clickThisDetail[m]=[];
            for(var k = 0; k < 20; k++)
            {
                $rootScope.clickThisDetail[m][k] = false;
            }
        }
        
        $rootScope.clickThisStar = [];
        for(var x = 0; x < 3; x++)
        {
            $rootScope.clickThisStar[x]=[];
            for(var y = 0; y < 20; y++)
            {
                $rootScope.clickThisStar[x][y] = false;
            }
        }
        
//        $rootScope.clickFavDetail = [];
//        for(var q = 0; q < Math.ceil($window.localStorage.length/20); q++)
//        {
//            $rootScope.clickFavDetail[q] = [];
//            for(var w = 0; w < 20; w++)
//            {
//                $rootScope.clickFavDetail[q][w] = false;
//            }
//        }
        
        for(var a = 0; a < $window.localStorage.length; a++)
        {
                var key = $window.localStorage.key(a);
                var value = JSON.parse($window.localStorage.getItem(key));

                value.detailStatus = false;
                $window.localStorage.setItem(key, JSON.stringify(value));
        }
        
        
        //console.log($scope.formData);
        // check to make sure the form is completely valid
        //$scope.data = data;
        if ($scope.myForm.$valid) {
            //Here: lat and lng
            //console.log($scope.formData);
            $scope.loading = true;
            var ipDataPromise = ipService.getData();
            ipDataPromise.then(function(result) {  
                $scope.Hereloc = result.lat + "," + result.lon;
                //$scope.data;
                
                var formData = $scope.formData;
                var choice = formData.location;
                var keyword = formData.keyword;
                var catergory = formData.catergory;
                var distance = formData.distance;
                var hereLoc = $scope.Hereloc;
                var otherLat, otherLng, otherLoc = "";

                var otherhasLoc = $scope.otherlocDetails; //不为空，用坐标
                var locationOther = formData.locationOther; //当选择other，没有选下拉菜单，要用Google place API
                //console.log(otherhasLoc);
                //console.log($scope.Hereloc);//此处很重要
                var p1;
                if (choice === 0) {
                    //hereLoc
                    var pp1 = {keyword: keyword, catergory:catergory, distance:distance, location:choice, geolocation:hereLoc};
                    p1 = pp1;
                    console.log(choice);
                }

                //other loc
                if(choice === 1)
                { //选下拉
                    if (otherhasLoc.length !== 0) {
                        otherLat = otherhasLoc[3];
                        otherLng = otherhasLoc[4];
                        otherLoc = otherLat + "," + otherLng;

                        var pp2 = {keyword: keyword, catergory:catergory, distance:distance, location:choice, geolocation:otherLoc, locationOther:locationOther};
                        p1 = pp2;
                    } 
                    else //没选下拉， use Google place API get location 在服务器端完成
                    {
                        var pp3 = {keyword: keyword, catergory:catergory, distance:distance, location:choice, geolocation:otherLoc, locationOther:locationOther};
                        p1 = pp3;
                    }

                }
                //console.log(p1);
                //send PHP
                $http({
                  method: 'GET',
                  url: 'http://travelsearchnode-env.us-east-2.elasticbeanstalk.com/showresults',
                  headers: {'Content-Type': 'application/json'},
                  params:p1
                }).then(function successCallback(response) {
                    // this callback will be called asynchronously
                    // when the response is available
                    $rootScope.data = response.data;
                    console.log($rootScope.data);
                    //进度条
                    $scope.loading = false;
                    //显示table
                    $location.path(path);
                  }, function errorCallback(response) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                    //$scope.searchSuccess = false;
                    console.log("connect fail");
                  });
                
                
                //$location.path(path);
                //console.log($scope.formData);
                //进度条出现在这儿
                //$scope.loading = false;
                
                //显示返回的信息
                //$location.path(path);
            }, function(error){
                $scope.searchSuccess = false;
            });
            
            //$location.path(path);
            //alert('our form is amazing');   
        }
        $scope.isSubmited = true;
    }; 
    

    //需要更改！！！！！
    $scope.clickResult = function(path) {
        if($scope.isSubmited)
        {
            $rootScope.slide = '';
            $rootScope.goSlide ='';
            
            $location.path(path);
            $scope.resultsClicked = true;
            $scope.favClicked = false;
            
        }
    };
    
    $scope.clickFav = function(path) {
    if($scope.isSubmited)
    {
        $rootScope.slide = '';
        $rootScope.goSlide ='';
        
        $location.path(path);
        $scope.favClicked = true;
        $scope.resultsClicked = false;
    }
    };
    
}]);
    
    
app.directive('googleplace', [ function() {
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
                    scope.$parent.otherlocDetails = scope.details;
                    model.$setViewValue(element.val());
                });
            });
        }
    };
}]);
    
app.factory('ipService', function($http) {

    var getData = function() {

        // Angular $http() and then() both return promises themselves 
        return $http({method:"GET", url:"http://ip-api.com/json"}).then(function(result){
            return result.data;
        });
    };

    return { getData: getData };
});
    


    
})(angular);