'use strict';

angular.module('luncher').controller('UserController', ['$scope', 'Restangular', function ($scope, Restangular) {
    $scope.users = [];

    var usersRest = Restangular.all('rest/users');

    getAllUsers();

    $scope.addUser = function (newUser) {
        $scope.users.post(newUser).then(function () {
            getAllUsers();
            $scope.newUser = {};
        });
    };

    $scope.deleteUser = function (user) {
        user.customDELETE("deleteByEmail/" + user.email).then(function () {
            getAllUsers();
        });
    };

    function getAllUsers() {
//        usersRest.getList().then(function (users) {
            $scope.users = usersRest.getList().$object;
//        });
    }
}]);