Backbone.View.prototype.close = function () {
    console.log('Closing view ' + this);
    if (this.beforeClose) {
        this.beforeClose();
    }
    this.remove();
    this.unbind();
};

var AppRouter = Backbone.Router.extend({

    initialize: function() {
    },
    routes: {
        "login"		: "login",
        ""          : "main",
        "oauth"     : "oauth", // If we managed to redirect the login callback to the route "oauth" then we can generate everything there
        "logout"    : "logout",
        "teachers"  : "teachers",
        "learners"  : "learners",
        "admin"     : "admin"
    },
    main: function(){
        if (!this.CalendarCourse) {
            this.CalendarCourse = new CalendarCourse();
            this.CalendarCourse.courseId = "humance";
            this.CalendarCourse.fetch({
                beforeSend: setHeader,
                success: function(response, jsonData) {
                    //console.log(response,jsonData);

                    //if (jsonData == "{}") {
                    //    setTimeout(function func() {
                    //        timeout = timeout *2;
                    //        drawChart()
                    //    }, timeout * 1000);
                    //} else {
                        new CalendarCourseView({ model: jsonData }).render().el;
                    //}
                }
            });
        }
    },
    logout: function() {
        $.cookie("arlearn.AccessToken", null, { path: '/' });
        $.cookie("arlearn.OauthType", null, { path: '/' });
        app.navigate('');
        window.location.replace("/");
    },
    login: function() {
        this.common();
    },
    oauth: function(){
        console.log("here we can store the accessToken following the code commented below");

        //if(location.href.indexOf("accessToken=") > -1){
        //    var aux = location.href.split("accessToken=")[1];
        //    accessToken = aux.split("&")[0];
        //    //console.log(location.href, aux, accessToken);
        //
        //    var date = new Date();
        //    date.setTime(date.getTime() + (1 * 24 * 60 * 60 * 1000));
        //    var expires = "; expires=" + date.toGMTString();
        //
        //    $.cookie("arlearn.AccessToken", accessToken, {expires: date, path: "/"});
        //    $.cookie("arlearn.OauthType", 2, {expires: date, path: "/"});
        //
        //    document.location = "/";
        //
        //}
    },
    common: function(callback) {

        console.debug("[common]", "Checking user...");

        if (this.CurrentUser) {

            console.log("hit on the local model");
        } else {

            this.CurrentUser = new CurrentUser();
            this.CurrentUser.fetch({
                beforeSend: setHeader,
                success: function(response, xhr) {

                    $('ul.navbar-nav > li:eq(0)').after( new UserView({ model: xhr }).render().el );
                    $( new UserSidebarView({ model: xhr }).render().el).insertBefore("form.sidebar-form");

                }
            });
        }
    }
});

tpl.loadTemplates(['main', 'user', 'user_sidebar'], function() {
    app = new AppRouter();
    Backbone.history.start();
});
