document.body.style.background = 'url(\'lib/background.jpg\') center no-repeat #FFFC58';

var app = angular.module('bashdownApp', [ 'ngMaterial' ]);

app.controller('BashController', [ '$mdDialog', '$interval', '$timeout', 'countdownService', 'bashService', 'timeService', function($mdDialog, $interval, $timeout, countdownService, bashService, timeService) {
    var bash = this;

    bashService.findActiveBash();
    bash.active = bashService.getActiveBash();

    bash.countdownTimer = countdownService.countdown.countdownValue;
    $interval(function() {
        bash.countdownTimer = countdownService.countdown.countdownValue;
    }, 500);

    bash.processDateTime = function(dateTime) {
        var date = new Date(dateTime);
        countdownService.setCountdownForDate(date);

        return timeService.getDateTime(date);
    }

    bash.createBash = function(ev) {
        $mdDialog.show({
            controller: 'CreateBashController as createBash',
            templateUrl: 'views/create-bash.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true,
            fullscreen: true
        })
        .then(function(data) {

        }, function() {
            $mdDialog.cancel();
        });
    }

    bash.currentIndex = 0;

    bash.isCurrentBashAtIndex = function(index) {
        return bash.currentIndex === index;
    }

    bash.rotate = function() {
        bash.currentIndex = (bash.currentIndex < bash.active.length - 1) ? ++bash.currentIndex : 0;
        $timeout(bash.rotate, 5000);
    }

    bash.rotate();
}]);

app.controller('CreateBashController', [ '$mdToast', '$mdDialog', 'bashService', function($mdToast, $mdDialog, bashService) {

    var createBash = this;

    createBash.activeBash = bashService.getActiveBash();

    createBash.title;
    createBash.description;
    createBash.dateTime;
    createBash.image;
    createBash.imageTitle;
    createBash.imageDescription;

    createBash.close = function() {
        $mdDialog.cancel();
    }

    createBash.selectedBash = null;

    createBash.onSelectBash = function(bash) {
        createBash.selectedBash = bash;
    }

    createBash.onDeselectBash = function(bash) {
        createBash.selectedBash = null;
    }

    createBash.updateBash = function(bash) {
        var isBashValid = bashService.validateBash(bash);

        if (isBashValid) {
            bash.dateTime = new Date(bash.dateTime);
            bashService.updateBash(bash);
        } else {
            $mdToast.show($mdToast.simple().textContent("Please correct the invalid form fields").position('top right').hideDelay(3000));
        }
    }

    createBash.deleteBash = function(bash) {
        bashService.deleteBash(bash.id);
    }

    createBash.createNewBash = function() {
        var bash = {
            title : createBash.title,
            description : createBash.description,
            dateTime : createBash.dateTime,
            image : createBash.image,
            imageTitle : createBash.imageTitle,
            imageDescription : createBash.imageDescription
        }

        var isBashValid = bashService.validateBash(bash);

        if (isBashValid) {
            bash.dateTime = new Date(bash.dateTime);
            bashService.createBash(bash);
            $mdDialog.hide();
        } else {
            $mdToast.show($mdToast.simple().textContent("Please correct the invalid form fields").position('top right').hideDelay(3000));
        }
    }
}]);

app.factory('countdownService', function() {

    var countdown = { countdownValue : [ "0:00:00" ] };

    var setCountdownForDate = function(date) {
        var deltaSeconds = parseInt(Math.abs(new Date().getTime() - date.getTime()) / 1000);
        var seconds = parseInt(deltaSeconds % 60);
        seconds = seconds < 10 ? "0" + seconds : seconds;
        var minutes = parseInt((deltaSeconds / 60) % 60);
        minutes = minutes < 10 ? "0" + minutes : minutes;
        var hours = parseInt(deltaSeconds / 3600);
        countdown.countdownValue.pop();
        countdown.countdownValue.push("" + hours + ":" + minutes + ":" + seconds);
    }

    return {
        setCountdownForDate : setCountdownForDate,
        countdown : countdown
    };
});

app.factory('timeService', function() {
    var getDateTime = function(date) {
        var months = "January February March April May June July August September October November December".split(" ");
        var dateInt = date.getDate();
        var month = months[date.getMonth()];
        var year = date.getYear() + 1900;

        var hour = date.getHours() % 12;
        var a = date.getHours() >= 12 ? "pm" : "am";
        hour = hour == 0 ? 12 : hour;

        var mins = date.getMinutes();
        mins = mins < 10 ? "0" + mins : mins;

        return "" + dateInt + " " + month + " " + year + " | " + hour + ":" + mins + " " + a;
    }

    return { getDateTime : getDateTime };
});

app.factory('bashService', [ '$http', '$mdToast', function($http, $mdToast) {
    var bash = { active : [] };

    var getActiveBash = function() {
        return bash.active;
    }

    var findActiveBash = function() {
        $http.get('/api/bash/find/active')
        .then(function(response) {
            for (var i = 0; i < response.data.length; i++) {
                getActiveBash().push(response.data[i]);
            }
            $mdToast.show($mdToast.simple().textContent("Loaded :)").position('top right').hideDelay(3000));
        }, function(response) {
            $mdToast.show($mdToast.simple().textContent("Load failed :( Error: " + response.status).position('top right').hideDelay(3000));
        });
        $mdToast.show($mdToast.simple().textContent("Loading...").position('top right').hideDelay(3000));
    }

    var createBash = function(bash) {
        $http.post('/api/bash/create', bash)
        .then(function(response) {
            getActiveBash().push(response.data);
            $mdToast.show($mdToast.simple().textContent("Save successful :)").position('top right').hideDelay(3000));
        }, function(response) {
            var errorList = "Save failed :(";
            if (response.data.errors) {
                errorList += " ";
                var errors = response.data.errors;
                for (var i = 0; i < errors.length; i++) {
                    errorList += errors[i] + "; ";
                }
            } else {
                errorList += " Error: " + response.code;
            }
            $mdToast.show($mdToast.simple().textContent(errorList).position('top right').hideDelay(3000));
        });
        $mdToast.show($mdToast.simple().textContent("Saving...").position('top right').hideDelay(3000));
    }

    var updateBash = function(bash) {
        $http.post('/api/bash/id/' + bash.id + '/update', bash)
        .then(function(response) {
            $mdToast.show($mdToast.simple().textContent("Update successful :)").position('top right').hideDelay(3000));
        }, function(response) {
            var errorList = "Update failed :(";
            if (response.data.errors) {
                errorList += " ";
                var errors = response.data.errors;
                for (var i = 0; i < errors.length; i++) {
                    errorList += errors[i] + "; ";
                }
            } else {
                errorList += " Error: " + response.code;
            }
            $mdToast.show($mdToast.simple().textContent(errorList).position('top right').hideDelay(3000));
        });
        $mdToast.show($mdToast.simple().textContent("Updating...").position('top right').hideDelay(3000));
    }

    var deletedBash;

    var deleteBash = function(id) {
        var i = -1;
        var deletedBash;
        $mdToast.show($mdToast.simple().textContent("Deleting...").action("UNDO").position('top right').hideDelay(3000))
        .then(function(response) {
            if (response == "ok") {
                if (deletedBash != null) {
                    getActiveBash().splice(i, 0, deletedBash[0]);
                }
            } else {
                $http.delete('/api/bash/id/' + id)
                .then(function(response) {
                }, function(response) {
                    if (deletedBash != null) {
                        getActiveBash().splice(i, 0, deletedBash[0]);
                        $mdToast.show($mdToast.simple().textContent("Delete failed :(").position('top right').hideDelay(3000));
                    }
                });
            }
        });
        for (j = 0; j < getActiveBash().length; j++) {
            if (getActiveBash()[j].id === id) {
                i = j;
                break;
            }
        }
        if (i > -1) {
            deletedBash = getActiveBash().splice(i, 1);
        }
    }

    var validateBash = function(bash) {
        var isDateTimeValid = bash.dateTime != null;

        var date = new Date(bash.dateTime);
        isDateTimeValid = isDateTimeValid && date != null;

        var isTitleValid = bash.title != null && bash.title.length >= 5 && bash.title.length <= 32 ? true : false;

        var isDescriptionValid = bash.description == null ? true : bash.description.length <= 256 ? true : false;

        if (isDateTimeValid && isTitleValid && isDescriptionValid) {
            var data = {
                title : bash.title,
                description : bash.description,
                dateTime : new Date(bash.dateTime).toISOString(),
                image : bash.image,
                imageTitle : bash.imageTitle,
                imageDescription : bash.imageDescription
            }
            if (bash.id != null) {
                data.id = bash.id;
            }
            if (bash.active != null) {
                data.active = bash.active;
            }
            return true;
        } else {
            return false;
        }
    }

    return {
        getActiveBash : getActiveBash,
        findActiveBash : findActiveBash,
        deletedBash : deletedBash,
        validateBash : validateBash,
        createBash : createBash,
        updateBash : updateBash,
        deleteBash : deleteBash
    }
}]);
