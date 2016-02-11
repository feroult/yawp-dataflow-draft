module.exports = (function () {

    const BACKEND_API = process.env.FLOWT_API;
    const MAX_CREATE_QUEUE = 30;

    var async = require('async');
    var yawp = require('./yawp');

    yawp.config(function (c) {
        c.baseUrl(BACKEND_API ? BACKEND_API : 'http://localhost:8080/api');
    });

    function run() {
        if (process.argv.length < 6) {
            console.error('use: add-student [total students] [total grades] [totalRequests] [parallel requests]');
            return;
        }
        var totalStudents = parseInt(process.argv[2], 10);
        var totalGrades = parseInt(process.argv[3], 10);
        var totalRequests = parseInt(process.argv[4], 10);
        var parallelRequests = parseInt(process.argv[5], 10);

        addStudents(totalStudents, totalGrades, totalRequests, parallelRequests);
    }

    function getRandomInt(min, max) {
        return Math.floor(Math.random() * (max + 1 - min)) + min;
    }

    function addStudents(totalStudents, totalGrades, totalRequests, parallelRequests) {
        function addStudent(i, callback) {

            var studentId = getRandomInt(1, totalStudents);
            var gradeId = getRandomInt(1, totalGrades);

            var student = {
                id: '/students/' + studentId,
                gradeId: '/grades/' + gradeId
            };

            console.log('student ->', student);

            yawp('/students').create(student).done(function () {
                callback();
            }).fail(function(err) {
                console.log('fail?! ', err);
                callback();
            });
        }

        async.timesLimit(totalRequests, parallelRequests, addStudent, function () {
            console.log('finish');
        });
    }

    return {
        run: run
    };

})();


var addStudent = require('./add-student.js');

if (require.main === module) {
    addStudent.run();
}
