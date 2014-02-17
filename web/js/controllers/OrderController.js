'use strict';

angular.module('luncher').controller('OrderController', function ($scope, $routeParams, Restangular) {
    $scope.orderData = {
        userEmail: $routeParams.userEmail,
        thisWeekOrders: [],
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

    $scope.orderData.thisWeekOrders = getOrders(getThisWeekBounds());
    $scope.orderData.nextWeekOrders = getOrders(getNextWeekBounds());

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

    $scope.saveOrders = function (orders) {
        orderRest.all("all").post(orders);
    };

    /**
     * Get orders within the given date bounds
     * @param bounds
     */
    function getOrders(bounds) {
        var orders = orderRest
            .one("from", bounds.from.getTime())
            .one("to", bounds.to.getTime()).getList().$object;

        for (var from = new Date(bounds.from.getTime()); from.getTime() < bounds.to.getTime();
             from = new Date(from.getTime()), from.setDate(from.getDate() + 1)) {
            if (!orders[from]) {
                orders.push(new Order(from.getTime(), ""));
            }
        }
        return orders;
    }

    function getThisWeekBounds() {
        var today = new Date();
        var nextFriday = new Date();

        if (0 < today.getDay() && today.getDay() < 6) {
            nextFriday.setDate(nextFriday.getDate() + (6 - today.getDay()));
        }

        return {
            from: today,
            to: nextFriday
        };
    }

    function getNextWeekBounds() {
        var nextMonday = new Date();
        nextMonday.setDate(nextMonday.getDate() + ((7 - nextMonday.getDay()) % 7) + 1);

        var nextFriday = new Date(nextMonday.getFullYear(), nextMonday.getMonth(),
            nextMonday.getDate() + 4);

        return {
            from: nextMonday,
            to: nextFriday
        };
    }

    function getAllOrders() {
        $scope.orders = orderRest.getList().$object;
    }
});