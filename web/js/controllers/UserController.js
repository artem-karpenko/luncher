angular.module('luncher')
    .controller('UserController', ['$scope', 'Restangular', function ($scope, Restangular) {
        $scope.users = [];

        var usersRest = Restangular.all('rest/users');

        usersRest.getList().then(function (users) {
            $scope.users = users;
        });
    }]);