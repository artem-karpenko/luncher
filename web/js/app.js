'use strict';

angular.module('luncher', ['ngRoute', 'restangular'])
.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    $routeProvider.when('/users', {templateUrl:'partials/users.html', controller: 'UserController'});
    $routeProvider.when('/users/:userEmail/orders',
        {templateUrl:'partials/orders.html', controller: 'OrderController'});
    $routeProvider.otherwise({redirectTo:'/users'});

//    $locationProvider.html5Mode(true);
}]);