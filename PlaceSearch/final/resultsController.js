(function (angular) {

    //注册一个模块，2.这个模块依赖哪些模块
    var resultsModule = angular.module("myApp.resultsModule", ['ngRoute']);

    resultsModule.config(['$routeProvider', function ($routeProvider) {
        $routeProvider
        .when('/showresults', {
            templateUrl: 'resultsView.html',
            controller: 'resultsController'

        });
        
}]);
    
    resultsModule.service('resultsService', function(){
        this.getValue = function() {
            return this.detailData;
        };
        
        this.setValue = function(newValue) {
            this.detailData = newValue;
        };
    });
    
    
    //注册一个主要的控制器
    resultsModule.controller("resultsController", ['$scope', '$http','$location','resultsService','$q','$rootScope','$window', function ($scope, $http, $location, resultsService, $q, $rootScope, $window) {

        /*-------------Animation---------------------*/
            //$scope.pageClass = 'results-view';
        $rootScope.slide ='';
        $rootScope.goSlide ='';
        
        //控制器分两步，1.设计暴露数据，2.设计暴露行为
        var Obj = $rootScope.data;
        $scope.searchData = $rootScope.data.results;
        
        console.log(Obj.next_page_token);
        //$scope.searchData = data;
        var dataCache = Obj;
        
        $scope.loading = $scope.$parent.loading;
        
        //$scope.pagesData = [];
        //$scope.currentPage = -1;
        //console.log($scope.currentPage);
       // $scope.hasPreview = false;
        //console.log($scope.currentPage);
                
        //Details============================================================
        //$scope.detailDisabled = true; 
        
        $scope.detailData;
        
       
        /*======================new Pagination===========================*/
        /*初始判断*/
        
        if($scope.searchData.length === 0 && $rootScope.currentPage === 0)
        {
            $scope.hasNext = false;
            $scope.hasPreview = false;
        }
        
        if($rootScope.pagesData.length === 0)
        {
            $rootScope.pagesData.push(dataCache);
            if('next_page_token' in Obj)
            {
                $scope.nextPageToken  = Obj.next_page_token;
                $scope.hasNext = true;
                $scope.hasPreview = false;
            }
            else
            {
                $scope.hasNext = false;
                $scope.hasPreview = false;
            }
        }
        
        
        if($rootScope.currentPage === 0)
        {
            $scope.hasPreview = false;
            if('next_page_token' in Obj)
            {
                $scope.nextPageToken  = Obj.next_page_token;
                $scope.hasNext = true;
            }
            else
            {
                $scope.hasNext = false;
            }
        }
        
        if($rootScope.currentPage === 1)
        {
            $scope.hasPreview = true;
            if('next_page_token' in Obj)
            {
                $scope.nextPageToken  = Obj.next_page_token;
                $scope.hasNext = true;
            }
            else
            {
                $scope.hasNext = false;
            }
        }
        
        if($rootScope.currentPage === 2)
        {
            $scope.hasPreview = true;
            $scope.hasNext = false;
        }
        
        
        /*****************/
        for(var a = 0; a < $window.localStorage.length; a++)
        {
            var key = $window.localStorage.key(a);
            for(var b = 0; b < $rootScope.pagesData[$rootScope.currentPage].results.length; b++)
            {
                if($rootScope.pagesData[$rootScope.currentPage].results[b].place_id === key)
                    $rootScope.clickThisStar[$rootScope.currentPage][b] = true;
            }
        }
        
        
        /*****************/
        
        $scope.goNext = function(nextPage){
            
            if(typeof $rootScope.pagesData[nextPage] === 'undefined')
            {
                
                $http({
                      method: 'GET',
                      url: 'http://travelsearchnode-env.us-east-2.elasticbeanstalk.com/pages/' + $scope.nextPageToken,
                      headers: {'Content-Type': 'application/json'},
                      //params:p1
                    }).then(function successCallback(response) {

                        var jsonObj = response.data;
                        //!important: insert geolocation for new data
                        var geolocation = $rootScope.data.geolocation;
                        $rootScope.data= jsonObj;
                        $rootScope.data.geolocation = geolocation;
                        
                        $scope.searchData = response.data.results;
                        $rootScope.data.results = $scope.searchData;///?????
                        
                        
        
                        $rootScope.clickThisDetail[nextPage]=[];
                        for(var k = 0; k < 20; k++)
                        {
                            $rootScope.clickThisDetail[nextPage][k] = false;
                        }
                        
                        $rootScope.clickThisStar[nextPage]=[];
                        for(var y = 0; y < 20; y++)
                        {
                            $rootScope.clickThisStar[nextPage][y] = false;
                        }


                        //把当前页面推入cache中
                        //dataCache = $scope.searchData;
                        $rootScope.pagesData.push(jsonObj);
                        $rootScope.currentPage = nextPage; 
                        console.log($rootScope.pagesData.length);
                        //查看是否有下一页
                        if('next_page_token' in jsonObj)
                        {
                            $scope.nextPageToken = jsonObj.next_page_token;
                            $scope.hasNext = true;
                            console.log("ahahhah");
                        }
                        else
                        {
                            $scope.hasNext = false;
                            $rootScope.finished = true;
                        }
                        $scope.hasPreview = true;

                    
                        for(var a = 0; a < $window.localStorage.length; a++)
                        {
                            var key = $window.localStorage.key(a);
                            for(var b = 0; b < $rootScope.pagesData[$rootScope.currentPage].results.length; b++)
                            {
                                if($rootScope.pagesData[$rootScope.currentPage].results[b].place_id === key)
                                    $rootScope.clickThisStar[$rootScope.currentPage][b] = true;
                            }
                        }
                    
                        //console.log(response);
                        //进度条
                        //$scope.loading = false;

                      }, function errorCallback(response) {
                        // called asynchronously if an error occurs
                        // or server returns response with an error status.
                        console.log("connect fail");
                      });
                
            }
            else
            {
                $scope.searchData = $rootScope.pagesData[nextPage].results;
                $rootScope.data = $rootScope.pagesData[nextPage];
                $rootScope.data.results = $scope.searchData;
                $rootScope.currentPage = nextPage;
                $scope.hasPreview = true;
                if('next_page_token' in $rootScope.pagesData[nextPage])
                    $scope.hasNext = true;
                else
                    $scope.hasNext = false;
                
            }
        };
        
        
        
        
        
        /*================================================================*/
            

        $scope.goBack = function(backPage) {
            console.log(backPage);
            if(backPage === 0)
            {
                $rootScope.currentPage = backPage;
                $scope.hasPreview = false;
                $scope.hasNext = true;
                $scope.searchData = $rootScope.pagesData[backPage].results;
                $rootScope.data = $rootScope.pagesData[backPage];
                $rootScope.data.results = $scope.searchData;
                
            }
            if(backPage === $rootScope.pagesData.length - 1)
            {
                $rootScope.currentPage = backPage;
                $scope.hasNext = false;
                $scope.hasPreview = true;
                $scope.searchData = $rootScope.pagesData[backPage].results;
                $rootScope.data = $rootScope.pagesData[backPage];
                $rootScope.data.results = $scope.searchData;
            }
            
            if(0 < backPage && backPage < $rootScope.pagesData.length - 1 )
            {
                $rootScope.currentPage = backPage;
                $scope.hasPreview = true;
                $scope.hasNext = true;
                $scope.searchData = $rootScope.pagesData[backPage].results;
                $rootScope.data = $rootScope.pagesData[backPage];
                $rootScope.data.results = $scope.searchData;
                
            }
            
            
        };
        
    //go details??????
        
      $scope.go = function(){
          $rootScope.slide = 'slide_from_left_to_right';
          $location.path('/showdetails');
          //console.log("work");
      };
        
        /*----------------------Details--------------------------*/
        $scope.getDetail = function(index){
            $rootScope.slide = 'slide_from_left_to_right';
            
            $rootScope.notClickfav = true;
            
            $rootScope.detailDisabled = false;
            //调节背景黄色
            $rootScope.currentIndex = index;
            
            //重置
            $rootScope.clickThisDetail = [];
            for(var m = 0; m < 3; m++)
            {
                $rootScope.clickThisDetail[m]=[];
                for(var k = 0; k < 20; k++)
                {
                    $rootScope.clickThisDetail[m][k] = false;
                }
            }
            
            $rootScope.clickThisDetail[$rootScope.currentPage][index] = true;
            
            $scope.placeId = $scope.searchData[index].place_id;
            
            var map = new google.maps.Map(document.createElement('div'));
            var placeService = new google.maps.places.PlacesService(map);

            
            var getDetails = function(place) {
              var deferred = $q.defer();
              placeService.getDetails({
                'placeId': place
              }, function(details) {
                deferred.resolve(details);
              });
              return deferred.promise;
            };
            
            getDetails($scope.placeId).then(function(details) {
                //$scope.detailData = details;
                resultsService.setValue(details);
                //console.log(details);
                var path = '/showdetails';
                $location.path(path);
              });
        
    };
        
    function getGoogleReviews(data){
        $scope.googleArray =[];
        
        if('reviews' in data)
        {
            var reviewData = data.reviews;
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
             //$scope.googleReviewUndefined = true;
            $scope.googleArray = [];
        }
        
        return $scope.googleArray;
    }
        
    function getPhotoArray(data){
        
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
        return $scope.photosArray;
    }
        
    //add to favorite
    //var placeItemData = {data:infoPhotoMapPart, googleReviews:googleReviews, yelpReviews:yelpReviews, photosArray:$scope.photosArray, timestamp:new Date().getTime()};
    $scope.addToFav = function(index){
        $scope.placeId = $scope.searchData[index].place_id;
        
        if($rootScope.clickThisStar[$rootScope.currentPage][index] === true)
        {
            $rootScope.clickThisStar[$rootScope.currentPage][index] = false;
            console.log($rootScope.clickThisStar[$rootScope.currentPage][index]);
            $window.localStorage.removeItem($scope.placeId);
                 
        }
        else
        {
            $rootScope.clickThisStar[$rootScope.currentPage][index] = true;
            
            $rootScope.fromAddFavDetail = false;
            //调节背景黄色

            var map = new google.maps.Map(document.createElement('div'));
            var placeService = new google.maps.places.PlacesService(map);


            var getDetails = function(place) {
              var deferred = $q.defer();
              placeService.getDetails({
                'placeId': place
              }, function(details) {
                deferred.resolve(details);
              });
              return deferred.promise;
            };

            getDetails($scope.placeId).then(function(details) {
                //$scope.detailData = details;
                resultsService.setValue(details);

                //重置
    //            $rootScope.clickFavDetail = [];
    //            for(var q = 0; q < Math.ceil($window.localStorage.length/20); q++)
    //            {
    //                $rootScope.clickFavDetail[q] = [];
    //                for(var w = 0; w < 20 && (w+ 20*q) < $window.localStorage.length; w++)
    //                {
    //                    $rootScope.clickFavDetail[q][w] = false;
    //                }
    //            }

    //            for(var a = 0; a < $window.localStorage.length; a++)
    //            {
    //                        var key = $window.localStorage.key(a);
    //                        var value = JSON.parse($window.localStorage.getItem(key));
    //                        
    //                        value.detailStatus = false;
    //                        $window.localStorage.setItem(key, JSON.stringify(value));
    //            }


                $scope.googleArray = getGoogleReviews(details);
                $scope.photosArray = getPhotoArray(details);
                var placeItemData = {data:details, googleReviews:$scope.googleArray, yelpReviews:'no', photosArray: $scope.photosArray, timestamp:new Date().getTime(),fromResult:true, detailStatus:false};

                var placeItemId = details.place_id;
                $window.localStorage.setItem(placeItemId, JSON.stringify(placeItemData));

               // $scope.detailStar = true;!!!!!!!星星变亮


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

                //$rootScope.favDetailDisabled = false;

                //console.log($scope.photosArray);星星变亮
                //$rootScope.clickThisStar[$rootScope.currentPage][index] = true;


              });
        }
    };




}]);
    


})(angular);

