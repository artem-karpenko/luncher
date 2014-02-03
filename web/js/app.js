'use strict';

angular.module('luncher', ['ngRoute', 'restangular'])
.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/users', {templateUrl:'partials/users.html', controller: 'UserController'});
    $routeProvider.otherwise({redirectTo:'/users'});
}]);