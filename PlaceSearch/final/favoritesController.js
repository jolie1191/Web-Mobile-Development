(function (angular) {

    //注册一个模块，2.这个模块依赖哪些模块
var favoritesModule = angular.module("myApp.favoritesModule", ['ngRoute']);

favoritesModule.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/showfavorites',{
        templateUrl:'favoritesView.html',
        controller:'favoritesController'
        
    });
}]);
    
    favoritesModule.service('favService', function(){
        this.getValue = function() {
            return this.detailData;
        };
        
        this.setValue = function(newValue) {
            this.detailData = newValue;
        };
    });
    
    //注册一个主要的控制器
favoritesModule.controller("favoritesController", ['$scope','$window','$location','favService','$rootScope',function($scope, $window, $location, favService,$rootScope) {
    /*-----------Animation-------------*/
        $rootScope.goSlide = '';
        $scope.slide ='';
    
    $scope.Math = window.Math;
    console.log("favorite work");
    
    $scope.favData = $window.localStorage;
    $scope.currentPage = 0;
    //$rootScope.notClickfav = true;

    //console.log($window.localStorage);
    //Step 1:parse
    //parse local storage elements to favArray
    $scope.favArray = [];
    for(var i = 0; i <$scope.favData.length; i++)
    {
        var key = $scope.favData.key(i);
        var value = JSON.parse($scope.favData.getItem(key));
    
        var item = {result:value};
        $scope.favArray.push(item);
        
    }
    
    //sort favArray according to timestamp
    function sortLocalStorage(favArray){
        favArray.sort(function(a, b){return a.result.timestamp - b.result.timestamp;});  
    }
    
    
    
    //pages 预处理-----------------分页-------------//

    /*********************************************/
    $scope.pages = [];
    
    //Helper: 分页器
    function dividePage(favArray) {
        $scope.clickThisDetail = [];
        $scope.totalPage = Math.ceil($window.localStorage.length/20);
        //$scope.currentPage = $rootScope.favCurrentPage;//!!!!!!!!

        for(var m = 0; m < $scope.totalPage; m++)
        {
            var pageItem = [];
            //$rootScope.favPagesData[m] = [];
            $scope.pages[m] = [];
            $scope.clickThisDetail[m] = [];
            for(var n = 0; n < 20 && (n + 20*m) < favArray.length; n++)
            {
                pageItem.push(favArray[n + 20*m]);
                $scope.clickThisDetail[m][n] = favArray[n + 20*m].result.detailStatus;
            }
            $scope.pages[m] = pageItem;
        }
    }
    
    //Step 2: sort
    sortLocalStorage($scope.favArray);
    //step 3: divide page
    dividePage($scope.favArray);
    //变黄
    //$scope.clickThisDetail = $rootScope.clickFavDetail;
    
//    if(typeof $rootScope.favDetail !== 'undefined' && $rootScope.fromAddFavDetail === false )
//        $scope.clickThisDetail = $rootScope.favDetail;
    
    if(typeof $rootScope.favDetailDisabled != 'undefined')
        $scope.detailDisabled = $rootScope.favDetailDisabled;
    
    $scope.searchData = $scope.pages[0];
    if($scope.totalPage > 1)
    {
        $scope.hasNext = true;
        $scope.hasPreview = false;
    }
    
    console.log($scope.favArray);
    
    /*********************************************/
    //console.log($rootScope.favPagesData);
    //Helper: 分页器
   /* function dividePage(favArray) {
        $scope.totalPage = Math.ceil($window.localStorage.length/20);
        $scope.currentPage = $rootScope.favCurrentPage;//!!!!!!!!

        for(var m = 0; m < $scope.totalPage; m++)
        {
            var pageItem = [];
            $rootScope.favPagesData[m] = [];
            for(var n = 0; n < 20 && (n + 20*m) < favArray.length; n++)
            {
                pageItem.push(favArray[n + 20*m]);
            }
            $rootScope.favPagesData[m] = pageItem;
        }
    }*/
    //Step 2: sort
 /*   sortLocalStorage($scope.favArray);
    //step 3: divide page
    dividePage($scope.favArray);
    $scope.clickThisDetail = $rootScope.clickFavDetail;
    
    //！important 赋值
    //Step 4: 赋值
   $scope.searchData = $rootScope.favPagesData[$scope.currentPage];*/
    
    //Helper: parse funtion raw favirte data
    function parseFavData(favData) {
        $scope.favArray = [];
        for(var i = 0; i <favData.length; i++)
        {
            var key = favData.key(i);
            var value = JSON.parse(favData.getItem(key));
            var item = {result:value};
            $scope.favArray.push(item);
        }   
    }
    
    //Delete item
/*    $scope.deleteItem = function(index){
        $scope.tmpFavTrueFalse = $rootScope.clickFavDetail;
        
        $scope.tmpArray = [];
        
        var referKey = $scope.favArray[index].result.data.place_id;
        $window.localStorage.removeItem(referKey);
        
        //reset $scope.favArray
        $scope.favData = $window.localStorage;
        //parse raw favData
        parseFavData($scope.favData);
        //sort
        sortLocalStorage($scope.favArray);
        //re-divide page
        dividePage($scope.favArray);
        //重新赋值
        $scope.searchData = $rootScope.favPagesData[$scope.currentPage];
        
        //original true/false
        //$scope.tmpArray = [];
        
        for(var j = 0; j < $scope.tmpFavTrueFalse.length; j++)
        {
            for(var k = 0; k < $scope.tmpFavTrueFalse[j].length; k++)
            {
                $scope.tmpArray.push($scope.tmpFavTrueFalse[j][k]);
            }
        }
        
        //console.log(abb);
       //T/F after removing item 
        var tmpIndex = $scope.currentPage * 20 + index;
        for(var m = tmpIndex; m < $scope.tmpArray.length; m++)
        {
            $scope.tmpArray[m] = $scope.tmpArray[m+1];
        }
        var abb = $scope.tmpArray.pop();
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
        
        if($rootScope.favPagesData[0].length === 0 && $rootScope.favPagesData.length ===0)
        {
            $scope.hasNext = false;
            $scope.hasPreview = false;
        }
        
        console.log($scope.favArray);
    }; 
    */
    
    
    /*----------------------------------------------------*/
    $scope.deleteItem = function(index){
        //console.log($scope.pages.length);
        
        var referKey = $scope.favArray[index + 20*$scope.currentPage].result.data.place_id;
        $window.localStorage.removeItem(referKey);
        
        //reset
        $scope.pages = [];
        $scope.favData = $window.localStorage;
        parseFavData($scope.favData);
        sortLocalStorage($scope.favArray);
        dividePage($scope.favArray);
        
        if((index + 20*$scope.currentPage)%20 === 0 && typeof $scope.pages[$scope.currentPage] === 'undefined')
        {
            $scope.currentPage = Math.floor((index + 20*$scope.currentPage)/20) - 1;
            //$scope.searchData = $scope.pages[$scope.currentPage];
        }
        else
        {
            $scope.currentPage = Math.floor((index + 20*$scope.currentPage)/20);
        }
        //$scope.currentPage = Math.floor((index + 20*$scope.currentPage)/20);
        $scope.searchData = $scope.pages[$scope.currentPage];
        
//        if(typeof $rootScope.favDetail !== 'undefined' && $rootScope.fromAddFavDetail === false )
//            $scope.clickThisDetail = $rootScope.favDetail;
        for(var a = 0; a < $rootScope.pagesData.length; a++)
        {
            for(var b = 0; b < $rootScope.pagesData[a].results.length; b++)
            {
                //console.log($rootScope.pagesData[a].results[b]);
                if($rootScope.pagesData[a].results[b].place_id === referKey)
                {
                    $rootScope.clickThisStar[a][b] = false;
                }
            }
        }

        
        if($scope.currentPage === 0)
        {
            $scope.hasPreview = false;
        }
        if($scope.currentPage === $scope.pages.length - 1)
            $scope.hasNext = false;
    
        console.log($scope.pages.length);
    };
    /*----------------------pagination---------------------*/
    console.log($scope.currentPage);
    $scope.currentPage = 0;
    $scope.goNext = function(nextPage){
        $scope.currentPage = nextPage;
        console.log($scope.currentPage );
        $scope.searchData = $scope.pages[nextPage];
        
        if(nextPage === $scope.totalPage - 1)
        {
            $scope.hasNext = false;
            $scope.hasPreview = true;
        }
        else
        {
            $scope.hasNext = true;
            $scope.hasPreview = true;
        }
        
    };
    
    $scope.goBack = function(backPage){

        $scope.currentPage = backPage;
        //console.log($scope.currentPage);
        $scope.searchData = $scope.pages[backPage];
        
        if(backPage === 0)
        {
            $scope.hasNext = true;
            $scope.hasPreview = false;
        }
        else
        {
            $scope.hasNext = true;
            $scope.hasPreview = true;
        }
    };
    
    
    $scope.getFavDetail = function(index){
        $rootScope.goSlide = 'slide-left';
        $rootScope.fromAddFavDetail = false;
        $rootScope.notClickfav = false;
        favService.setValue($scope.pages[$scope.currentPage][index]);
       
        console.log($scope.pages[$scope.currentPage][index]);
        
        
        for(var a = 0; a < $window.localStorage.length; a++)
        {
            var key = $window.localStorage.key(a);
            var value = JSON.parse($window.localStorage.getItem(key));

            value.detailStatus = false;
            $window.localStorage.setItem(key, JSON.stringify(value));
        }
        
        var referKey = $scope.pages[$scope.currentPage][index].result.data.place_id;
        var referValue = JSON.parse($window.localStorage.getItem(referKey));
        referValue.detailStatus = true;
        $window.localStorage.setItem(referKey, JSON.stringify(referValue));
        
            
        $rootScope.favDetailDisabled = false;
        $scope.detailDisabled = false;
        $location.path('/favdetails');
        
    };
    
    
    $scope.goDetails = function(){
        $rootScope.goSlide ='slide-right';
        if($rootScope.notClickfav === true)
            $location.path('/showdetails');
        else
            $location.path('/favdetails');
    };
    
    
    //goNext()
  /*  if($rootScope.favCurrentPage === 0 && $rootScope.favCurrentPage === $rootScope.favPagesData.length - 1)
    {
        $scope.hasNext = false;
        $scope.hasPreview = false;
    }
    else if($rootScope.favCurrentPage === 0 && $rootScope.favCurrentPage < $rootScope.favPagesData.length - 1)
    {
        $scope.hasNext = true;
        $scope.hasPreview = false;
    }
    else if($rootScope.favCurrentPage === 0 && $rootScope.favPagesData.length === 0)
    {
        $scope.hasNext = false;
        $scope.hasPreview = false;
    }
    else if($rootScope.favCurrentPage !== 0 && $rootScope.favCurrentPage === $rootScope.favPagesData.length - 1)
    {
        $scope.hasNext = false;
        $scope.hasPreview = true;
    }
    else
    {
        $scope.hasPreview = true;
        $scope.hasNext = true;
    }
    
    $scope.goNext = function(nextPage){
       // $rootScope.favCurrentPage 
        //console.log(nextPage);
        $scope.totalPage = Math.ceil($window.localStorage.length/20);
        //$scope.currentPage = $rootScope.favCurrentPage;
//        $rootScope.clickFavDetail[nextPage]=[];
//        for(var w = 0; w < 20; w++)
//        {
//            $rootScope.clickFavDetail[nextPage][w] = false;
//        }
        
        //$scope.clickThisDetail = $rootScope.clickFavDetail;
        
        $rootScope.favCurrentPage = nextPage;
        
        $scope.currentPage = $rootScope.favCurrentPage;
        $scope.searchData = $rootScope.favPagesData[$scope.currentPage];
        
        console.log($rootScope.favCurrentPage);
        if(nextPage === $scope.totalPage - 1)
        {
            $scope.hasNext = false;
            $scope.hasPreview = true;
            console.log("work");
        }
        else
        {
            $scope.hasNext = true;
            $scope.hasPreview = true;
        }
        
    };
    
    $scope.goBack = function(backPage){
        $scope.totalPage = Math.ceil($window.localStorage.length/20);
        
        $rootScope.favCurrentPage = backPage;
        $scope.currentPage = $rootScope.favCurrentPage;
        $scope.searchData = $rootScope.favPagesData[$scope.currentPage];
        
        if(backPage === 0 && backPage === $scope.totalPage - 1)
        {
            $scope.hasNext = false;
            $scope.hasPreview = false;
        }
        else if(backPage === 0 && backPage < $scope.totalPage - 1)
        {
            $scope.hasNext = true;
            $scope.hasPreview = false;
        }
        else
        {
            $scope.hasNext = true;
            $scope.hasPreview = true;
        }
    };
    
    //get favorite detail============未完成！
   // $scope.detailDisabled = $rootScope.favDetailDisabled;
    $scope.detailDisabled = $rootScope.detailDisabled;
    $scope.getFavDetail = function(index){
        $rootScope.goSlide = 'slide-left';
        
        $rootScope.notClickfav = false;
        
        $rootScope.lastClickFavDetail = index;
        
        favService.setValue($rootScope.favPagesData[$scope.currentPage][index]);
        
        
        $rootScope.clickFavDetail = [];
        for(var q = 0; q < Math.ceil($window.localStorage.length/20); q++)
        {
            $rootScope.clickFavDetail[q] = [];
            for(var w = 0; w < 20 && (w+ 20*q) < $window.localStorage.length; w++)
            {
                $rootScope.clickFavDetail[q][w] = false;
            }
        }
        
        $rootScope.clickFavDetail[$scope.currentPage][index] = true;
        $scope.clickThisDetail[$scope.currentPage][index] = true;
        //$scope.detailDisabled = false;
        $rootScope.favDetailDisabled = false;
        $scope.detailDisabled = false;
        $rootScope.detailDisabled = false;
        
        $location.path('/favdetails');
    };
    

    $scope.goDetails = function(){
        $rootScope.goSlide ='slide-right';
        if($rootScope.notClickfav === true)
            $location.path('/showdetails');
        else
            $location.path('/favdetails');
    };
    
    */
    
    
    //$scope.searchData = $scope.favArray.
    
}]);
    
})(angular);