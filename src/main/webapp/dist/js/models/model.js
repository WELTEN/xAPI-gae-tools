/////////
// Models
/////////
window.User = Backbone.Model.extend({
    initialize: function(a){
        //console.log("User initialize");
    }
});

window.RepresentationObject = Backbone.Model.extend({
    initialize: function(a){
        //console.log("RepresentationObject initialize");
    },
    defaults:{
        "id": "",
        "content": ""
    }
});

window.CalendarActivity = Backbone.Model.extend({
    initialize: function(studentId){
        this.studentId = studentId;
    },
    url: function(){
        return "/data-proxy/query/result/calendar/user/"+this.studentId+"/gdata";
    }
    ,
    defaults:{
        "studentId": ""
    }
});

window.CalendarActivityCourse = Backbone.Model.extend({
    initialize: function(courseId){
        this.courseId = courseId;
    },
    url: function(){
        return "/data-proxy/query/result/calendar/course/"+this.courseId;
    },
    defaults:{
        "courseId": ""
    }
});

window.Perfomance = Backbone.Model.extend({
    url: function(){
        return "/data-proxy/query/result-fake/averageLearnerActivities/humance";
    }
});

window.DropOutMonitor = Backbone.Model.extend({
    url: function(){
        return "/data-proxy/query/result-fake/dropoutMonitor";
    }
});

window.Course = Backbone.Model.extend({});

//////////////
// Collections
//////////////
window.CurrentUser = Backbone.Collection.extend({
    model: User,
    url: "/rest/account/accountDetails"
});

window.UserRunCollection = Backbone.Collection.extend({
    model: User,
    url: function(){
        return "/rest/users/runId/"+this.runId;
    },
    parse: function(response){
        return response.users;
    }
});

window.GraphsCollection = Backbone.Collection.extend({
    model: RepresentationObject
});

window.CoursesCollection = Backbone.Collection.extend({
    model: Course,
    url: '/data-proxy/courses',
    //url: 'dist/data/courses.json',
    parse: function (response) {
        // Return people object which is the array from response
        return response.courses;
    }
});

//////////////////////////////////////////
// Defined header to make the service call
//////////////////////////////////////////
var setHeader = function (xhr) {
    // Check cookie and read that value

    //console.log(location.href);

    //var s = document.cookie;

    var accessToken = "";
    var type = 2;

    // Problem:
    // 1) someone put an access token manually it will restore the current cookie
    // 2)

    if(location.href.indexOf("accessToken=") > -1){
        var aux = location.href.split("accessToken=")[1];
        accessToken = aux.split("&")[0];
        //console.log(location.href, aux, accessToken);
        aux = location.href.split("type=")[1];
        type = aux.split("&")[0];

        var date = new Date();
        date.setTime(date.getTime() + (1 * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();

        $.cookie("arlearn.AccessToken", accessToken, {expires: date, path: "/"});
        $.cookie("arlearn.OauthType", type, {expires: date, path: "/"});

        document.location = "/";
    }

    if($.cookie("arlearn.AccessToken")){
        accessToken = $.cookie("arlearn.AccessToken");
        //console.log(location.href, accessToken);
    }

    xhr.setRequestHeader('Authorization', 'GoogleLogin auth='+accessToken);
    xhr.setRequestHeader('Accept', 'application/json');

    //console.log(accessToken,xhr);
}