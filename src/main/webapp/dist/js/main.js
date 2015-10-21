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
        console.log("listing student view for " + id);
        this.showView('.content', new DashBoardLearnerView());
        //$('.content').append(new DashBoardLearnerView().render().el);
        var oldthis = this;
        google.load('visualization', '1', {
            packages: ['calendar', 'bar'], callback: function () {
                oldthis.loadCalendarActivities();
                oldthis.loadPerformance();
            }
        });
    },
    showView: function(selector, view) {
        if (this.currentView)
            this.currentView.close();
        $(selector).html(view.render().el);
        this.currentView = view;
        return view;
    },
    teacherView: function (id) {
        console.log("listing student view for " + id);
        this.showView('.content', new DashBoardTeacherView());
        //$('.content').append(new DashBoardTeacherView().render().el);
        var oldthis = this;
        google.load('visualization', '1', {
            packages: ['calendar', 'bar'], callback: function () {
                oldthis.loadCalendarActivitiesCourse();
            }
        });


    },
    adminView: function (id) {
        console.log("listing student view for " + id);
        this.showView('.content', new DashBoardAdminView());
        //$('.content').append(new DashBoardAdminView().render().el);
        var oldthis = this;
        google.load('visualization', '1', {
            packages: ['calendar', 'bar', 'corechart'], callback: function () {

                oldthis.loadDropOutMonitor();
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
    loadCalendarActivities: function () {
        //var jsonData = $.ajax({
        //    url: "/data-proxy/query/result-fake/calendar/user",
        //    dataType: "json",
        //    async: false
        //}).responseText;
        //
        //var data = new google.visualization.DataTable(jsonData);
        //
        //var chart = new Backbone.GoogleChart({
        //    chartType: 'Calendar',
        //    dataTable: data,
        //    backgroundColor: { fill:'transparent' },
        //    options: {'title': 'agenda'}
        //});
        //
        //$('.learner-vis1').append(chart.render().el);

        this.CalendarUser = new CalendarUser();
        this.CalendarUser.fetch({
            beforeSend: setHeader,
            success: function (response, jsonData) {
                console.log(jsonData);
                var data = new google.visualization.DataTable(jsonData);
                this.showView('.learner-vis1', new CalendarUserView({ model: data }));
            }
        });
    },
    loadCalendarActivitiesCourse: function () {
        var jsonData = $.ajax({
            url: "/data-proxy/query/result-fake/calendar/course/humance",
            dataType: "json",
            async: false
        }).responseText;

        var data = new google.visualization.DataTable(jsonData);

        var chart = new Backbone.GoogleChart({
            chartType: 'Calendar',
            dataTable: data,
            backgroundColor: { fill:'transparent' },
            options: {'title': 'agenda'}
        });

        $('.teacher-vis1').append(chart.render().el);

    },
    loadPerformance: function () {
        var jsonData = $.ajax({
            url: "/data-proxy/query/result-fake/averageLearnerActivities/humance",
            dataType: "json",
            async: false
        }).responseText;

        var data = new google.visualization.DataTable(jsonData);

        var chart = new Backbone.GoogleChart({
            chartType: 'BarChart',
            dataTable: data,
            options: {
                chart: {
                    title: 'Group vs. individual performance',
                },
                backgroundColor: { fill:'transparent' },
                bars: 'horizontal' // Required for Material Bar Charts.
            }
        });

        $('.learner-vis2').append(chart.render().el);

    },
    loadDropOutMonitor: function () {
        var jsonData = $.ajax({
            url: "/data-proxy/query/result-fake/dropoutMonitor",
            dataType: "json",
            async: false
        }).responseText;

        var data = new google.visualization.DataTable(jsonData);

        var chart = new Backbone.GoogleChart({
            chartType: 'BubbleChart',
            dataTable: data,
            options: {
                title: 'DropOutMonitor: Correlation between number of course launches, number of activities and number of users for all Moocs',
                hAxis: {title: 'number of activities', logScale:true},
                vAxis: {title: 'number of launches', logScale:true},
                backgroundColor: { fill:'transparent' },
                height:400,
                bubble: {textStyle: {fontSize: 11}}
            }
        });

        $('.admin-vis1').append(chart.render().el);

    }
});


tpl.loadTemplates(['main', 'user', 'user_sidebar', 'dashboard-learner', 'dashboard-teacher', 'dashboard-admin'], function () {
    app = new AppRouter();
    Backbone.history.start();
});
