Backbone.View.prototype.close = function () {
    console.log('Closing view ', this);
    if (this.beforeClose) {
        this.beforeClose();
    }
    this.remove();
    this.unbind();
};

var AppRouter = Backbone.Router.extend({
    initialize: function () {
        this.Graphs = new GraphsCollection();
    },
    routes: {
        "": "login",
        "oauth": "oauth", // If we managed to redirect the login callback to the route "oauth" then we can generate everything there
        "logout": "logout",
        "mooc/:id/student": "studentView",
        "mooc/:id/teacher": "teacherView",
        "mooc/:id/admin": "adminView"
    },
    logout: function () {
        $.cookie("arlearn.AccessToken", null, {path: '/'});
        $.cookie("arlearn.OauthType", null, {path: '/'});
        app.navigate('');
        window.location.replace("/");
    },
    login: function () {
        this.common();
    },
    oauth: function () {
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
    studentView: function (id) {
        if(!this.Graphs.get("student_calendar_activities")) {
            console.log("Not in the collection");
            this.CalendarActivity = new CalendarActivity();
            this.CalendarActivity.fetch({
                beforeSend: setHeader,
                success: function (response, jsonData) {
                    //console.log(jsonData);
                    app.showView('.content > .row', new CalendarView({ model: jsonData }));

                    app.addRepresentationObject("student_calendar_activities", jsonData);
                }
            });
        }else{
            console.log("Already in the collection");
            app.showView('.content > .row', new CalendarView({ model: this.Graphs.get("student_calendar_activities") }));
        }

        console.log(app.Graphs);

        if(this.Graphs.get("aada")) {
        this.Perfomance = new Perfomance();
        this.Perfomance.fetch({
            beforeSend: setHeader,
            success: function (response, jsonData) {
                var perf_view = new PerfomanceView({ model: jsonData })
                if (perf_view)
                    perf_view.close();
                $('.content > .row').append(perf_view.render().el);

                app.addRepresentationObject("addaa", jsonData);
            }
        });
        }else{
            console.log("Already in the collection");
        }
    },
    showView: function(selector, view) {
        if (this.currentView)
            this.currentView.close();
        $(selector).html(view.render().el);
        this.currentView = view;
        return view;
    },
    teacherView: function (id) {
        this.CalendarActivityCourse = new CalendarActivityCourse();
        this.CalendarActivityCourse.fetch({
            beforeSend: setHeader,
            success: function (response, jsonData) {
                //console.log(jsonData);
                app.showView('.content > .row', new CalendarView({ model: jsonData }));

                app.addRepresentationObject("asdasd", jsonData);
            }
        });
    },
    adminView: function (id) {
        this.DropOutMonitor = new DropOutMonitor();
        this.DropOutMonitor.fetch({
            beforeSend: setHeader,
            success: function (response, jsonData) {
                //console.log(jsonData);
                app.showView('.content > .row', new BubbleView({ model: jsonData }));

                app.addRepresentationObject("dsfsd", jsonData);
            }
        });

    },
    common: function (callback) {
        if (!this.CurrentUser) {
            this.CurrentUser = new CurrentUser();
            this.CurrentUser.fetch({
                beforeSend: setHeader,
                success: function (response, xhr) {
                    $('ul.navbar-nav > li:eq(0)').after(new UserView({model: xhr}).render().el);
                    $(new UserSidebarView({model: xhr}).render().el).insertBefore("form.sidebar-form");
                }
            });
        }
    },
    addRepresentationObject: function (id, data){
        var representationObject = new RepresentationObject();
        representationObject.set("content", data);
        representationObject.set("id", id);
        app.Graphs.add(representationObject);

        console.log(app.Graphs);
    }
});


tpl.loadTemplates(['main', 'user', 'user_sidebar', 'dashboard-learner', 'dashboard-teacher', 'dashboard-admin',  'widget'], function () {
    app = new AppRouter();
    Backbone.history.start();
});
