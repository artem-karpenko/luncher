'use strict';

angular.module('luncher').controller('OrderController', function ($scope, $routeParams, Restangular) {
    $scope.orderData = {
        userEmail: $routeParams.userEmail,
        nextWeekOrders: [],
        availableOrderDescriptions: [
            "Комплекс №1",
            "Комплекс №1a",
            "Комплекс №2",
            "Комплекс №3",
            "Комплекс №4"
        ]
    };

    var Order = function(date, description) {
        this.date = date;
        this.description = description;
    };

    var userRest = Restangular.one('rest/users', $scope.orderData.userEmail);
    var orderRest = userRest.all("orders");
    $scope.orderData.user = userRest.get().$object;

//    getAllOrders();
    getNextWeekOrders();

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

    $scope.saveNextWeekOrders = function () {
        orderRest.all("all").post($scope.orderData.nextWeekOrders);
    };

    function getNextWeekOrders() {
        var bounds = getNextWeekBounds();
        $scope.orderData.nextWeekOrders = orderRest
            .one("from", bounds.from.getTime())
            .one("to", bounds.to.getTime()).getList().$object;

        for (var from = new Date(bounds.from.getTime()); from.getTime() < bounds.to.getTime();
             from = new Date(from.getTime()), from.setDate(from.getDate() + 1)) {
            if (!$scope.orderData.nextWeekOrders[from]) {
                $scope.orderData.nextWeekOrders.push(new Order(from.getTime(), "Комплекс №2"));
            }
        }
    }

    function getNextWeekBounds() {
        var nextMonday = new Date();
        do {
            nextMonday.setDate(nextMonday.getDate() + 1);
        } while (nextMonday.getDay() != 1);

        var nextFriday = new Date(nextMonday.getFullYear(), nextMonday.getMonth(),
            nextMonday.getDate() + 5);

        return {
            from: nextMonday,
            to: nextFriday
        };
    }

    function getAllOrders() {
        $scope.orders = orderRest.getList().$object;
    }
});