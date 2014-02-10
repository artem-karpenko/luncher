'use strict';

angular.module('luncher').controller('OrderController', function ($scope, $routeParams, Restangular) {
    $scope.userEmail = $routeParams.userEmail;

    var userRest = Restangular.one('rest/users/', $scope.userEmail);
    $scope.user = userRest.get().$object;
});