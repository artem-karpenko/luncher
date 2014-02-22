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

    var mailRest = Restangular.all('rest/sendMail')
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

    $scope.sendThisWeekEmail = function () {
        sendEmail(getThisWeekBounds())
    };

    $scope.sendNextWeekEmail = function () {
        sendEmail(getNextWeekBounds())
    };

    function sendEmail(bounds) {
        mailRest
            .one("from", bounds.from.getTime())
            .one("to", bounds.to.getTime())
            .post()
    }

    /**
     * Get orders within the given date bounds
     * @param bounds
     */
    function getOrders(bounds) {
        var orders;

        if (bounds) {
            orders = orderRest
                .one("from", bounds.from.getTime())
                .one("to", bounds.to.getTime()).getList().$object;

            for (var from = new Date(bounds.from.getTime()); from.getTime() <= bounds.to.getTime();
                 from = new Date(from.getTime()), from.setDate(from.getDate() + 1)) {
                if (!orders[from]) {
                    orders.push(new Order(from.getTime(), null));
                }
            }
        } else {
            orders = [];
        }

        return orders;
    }

    function getThisWeekBounds() {
        var today = getToday();
        var nextFriday = getToday();

        if (0 < today.getDay() && today.getDay() < 6) {
            nextFriday.setDate(nextFriday.getDate() + (5 - today.getDay()));
        } else {
            return null;
        }

        return {
            from: today,
            to: nextFriday
        };
    }

    function getNextWeekBounds() {
        var nextMonday = getToday();
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

    function getToday() {
        var today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        today.setMilliseconds(0);
        return today;
    }
});