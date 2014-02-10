'use strict';

angular.module('luncher').controller('OrderController', function ($scope, $routeParams, Restangular) {
    $scope.userEmail = $routeParams.userEmail;

    var userRest = Restangular.one('rest/users/', $scope.userEmail);
    var orderRest = userRest.all("orders");
    $scope.user = userRest.get().$object;

    getAllOrders();

    $scope.addOrder = function (newOrder) {
        $scope.orders.post(newOrder).then(function() {
            getAllOrders();
            $scope.newOrder = {};
        });
    };

    $scope.deleteOrder = function (order) {
        userRest.one("orders", order.date).remove().then(function () {
            getAllOrders();
        });
    };

    function getAllOrders() {
        $scope.orders = orderRest.getList().$object;
    }
});