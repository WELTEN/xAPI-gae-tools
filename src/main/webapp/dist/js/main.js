Backbone.View.prototype.close = function () {
    //console.log('Closing view ', this);
    if (this.beforeClose) {
        this.beforeClose();
    }
    this.remove();
    this.unbind();
};

var AppRouter = Backbone.Router.extend({
    initialize: function () {
        this.Graphs = new GraphsCollection();
        this.loadCourses();
        if ($.cookie("arlearn.AccessToken") == undefined) {
            window.open("/login.jsp","_self")
        }
    },
    routes: {
        "": "login",
        "oauth": "oauth", // If we managed to redirect the login callback to the route "oauth" then we can generate everything there
        "logout": "logout",
        "mooc/loginData": "loginData",
        "mooc/:id/student": "studentView",
        "mooc/:id/teacher": "teacherView",
        "mooc/:id/admin": "adminView",
        "sandbox/:id": "sandbox"
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
    },
    sandbox: function(id){

        this.d3Chart("AverageResourceConsumption", "Your progress",
            "/data-proxy/query-fake/averageLearnerActivities/oai:eu.ecolearning.hub0:4",
            "/data-proxy/query/result-fake/averageLearnerActivities/oai:eu.ecolearning.hub0:4/d3",
            BarChartView);


        this.googleChart("Interactivity", "Overall activity",
            "/data-proxy/query/interactivitySort/"+id,
            "/data-proxy/query/result/interactivitySort/"+id+"/gdata",
            ColumnChartView);
        //this.googleChart("Progess Gauge", "Your progress gauge",
        //    "/data-proxy/query/progress/"+id+"/user",
        //    "/data-proxy/query/result/progress/"+id+"/gdata",
        //    Progress, GaugeView);
    },

    d3Chart: function(graphId, widgetTitle, queryUrl, resultUrl, viewObject){
        var timeout = 1;
        var _p = this.Graphs.get(graphId)
        if(!_p) {
            var view = new viewObject({ title: widgetTitle });
            app.showView('.content > .row', view );


            $.ajax({
                url: queryUrl,
                async: true,
                headers: {
                    Authorization: getCookie('arlearn.AccessToken')
                },
                success: function (data) {
                }
            });

            function drawChart() {
                $.ajax({
                    url: resultUrl,
                    dataType: "json",
                    headers: {
                        Authorization : getCookie('arlearn.AccessToken')
                    },
                    async: true,
                    success: function(jsonData) {
                        if (Object.getOwnPropertyNames(jsonData).length == 0 ) {
                            setTimeout(function func() {
                                timeout = timeout *2;
                                drawChart()
                            }, timeout * 1000);
                        } else {
                            view.update(jsonData);
                        }
                    }
                });
            }
            drawChart();


        }

    },

    googleChart: function(graphId, widgetTitle, queryUrl, resultUrl, viewObject){
        var timeout = 1;
        var _p = this.Graphs.get(graphId)
        if(!_p) {
            if (queryUrl) {
                $.ajax({
                    url: queryUrl,
                    async: true,
                    headers: {
                        Authorization: getCookie('arlearn.AccessToken')
                    },
                    success: function (data) {
                    }
                });
            }
            function drawChart() {
                //var jsonData = ""
                //jsonData = $.ajax({
                //    url: resultUrl,
                //    dataType: "json",
                //    headers: {
                //        Authorization : getCookie('arlearn.AccessToken')
                //    },
                //    async: false
                //}).responseText;


                $.ajax({
                    url: resultUrl,
                    dataType: "json",
                    headers: {
                        Authorization : getCookie('arlearn.AccessToken')
                    },
                    async: true,
                    success: function(jsonData) {
                        if (Object.getOwnPropertyNames(jsonData).length == 0 ) {//jsonData == "{}" || (jsonData+"").indexOf('true') != -1
                            setTimeout(function func() {
                                timeout = timeout *2;
                                drawChart()
                            }, timeout * 1000);
                        } else {
                            var perf_view = new viewObject({ model: jsonData, title: widgetTitle })
                            if (perf_view)
                                perf_view.close();
                            $('.content > .row').append(perf_view.render().el);
                            app.addRepresentationObject(graphId, jsonData);
                        }
                    }
                });
            }
            drawChart();
        }else{
            var perf_view = new viewObject({ model: _p.get("content"), title: widgetTitle })
            if (perf_view)
                perf_view.close();
            $('.content > .row').append(perf_view.render().el);

        }
    },

    loginData: function() {
        $('.content > .row').html("");
        //this.googleChart("loginData", "Logins of all ECO users", "/data-proxy/query/calendar/logins", "/data-proxy/query/result-fake/calendar/logins/gdata", CalendarLoginsAll, CalendarView);

        this.googleChart("loginData", "Logins of all ECO users", "/data-proxy/query/calendar/logins", "/data-proxy/query/result/calendar/logins/gdata", CalendarView);

        this.googleChart("loginDataMe", "Your login data", "/data-proxy/query/calendar/logins/user", "/data-proxy/query/result/calendar/logins/user/gdata", CalendarView);

        //var resizeLogin = this.loginData;
        //$(window).resize(function(){
        //    $('.content > .row').html("");
        //    resizeLogin();
        //});
    },
    studentView: function (id) {

        $('.content > .row').html("");

        this.googleChart("Progess", "Your progress",
            "/data-proxy/query/progress/"+id+"/user",
            "/data-proxy/query/result/progress/"+id+"/gdata",
            ProgressView);
        this.googleChart("Progess Gauge", "Your progress gauge",
            "/data-proxy/query/progress/"+id+"/user",
            "/data-proxy/query/result/progress/"+id+"/gdata",
            GaugeView);
        this.d3Chart("ResourceConsumptionUser", "Resources consumed by you in this course",
            '/data-proxy/query/resourceTypes/course/'+id+'/d3',
            '/data-proxy/query/result/resourceTypes/course/'+id+'/d3',
            BarChartView);

        this.googleChart("student_calendar_activities:"+id, "All your activities in ECO (regardless of the course)",
            null,
            "/data-proxy/query/result/calendar/user/gdata", CalendarView);

        //var view = new BarChartView({ title: "Resources consumed by you in this course" });
        //app.showView('.content > .row', view );
        //$.ajax({
        //    url: '/data-proxy/query/result/resourceTypes/course/'+id+'/d3',
        //    headers: { "Authorization": 'GoogleLogin auth='+$.cookie("arlearn.AccessToken") },
        //    async: true,
        //    success: function(data){
        //        view.update(data);
        //    }
        //});


        //var _sca = this.Graphs.get("student_calendar_activities:"+id);
        //if(!_sca) {
        //    this.CalendarActivity = new CalendarActivity();
        //    this.CalendarActivity.fetch({
        //        beforeSend: setHeader,
        //        success: function (response, jsonData) {
        //            var studentCalActivities = new CalendarView({ model: jsonData, title: "All your activities in ECO (regardless of the course)" });
        //            if (studentCalActivities ) studentCalActivities.close();
        //            $('.content > .row').append(studentCalActivities.render().el);
        //            app.addRepresentationObject("student_calendar_activities:"+id, jsonData);
        //        }
        //    });
        //}else{
        //    var studentCalActivities = new CalendarView({ model: _sca.get("content"), title: "All your activities in ECO (regardless of the course)" });
        //    if (studentCalActivities ) studentCalActivities.close();
        //    $('.content > .row').append(studentCalActivities.render().el);
        //    app.showView('.content > .row', studentCalActivities);
        //}
        //

        this.googleChart("student_average_learner_activities:"+id, "your performance compared to the average performance",
            '/data-proxy/query/averageLearnerActivities/'+id,
            "/data-proxy/query/result/averageLearnerActivities/" + id, PerfomanceView);



        //var _p = this.Graphs.get("performance:"+id)
        //if(!_p) {
        //    var performanceObject= new Perfomance();
        //    $.ajax({
        //        url: '/data-proxy/query/averageLearnerActivities/'+id,
        //        async: true,
        //        success: function(data){}
        //    });
        //    function drawChart() {
        //
        //        var jsonData = ""
        //        jsonData = $.ajax({
        //            url: "/data-proxy/query/result/averageLearnerActivities/" + id,
        //            dataType: "json",
        //            headers: {
        //                Authorization : getCookie('arlearn.AccessToken')
        //            },
        //            async: false
        //        }).responseText;
        //        if (jsonData == "{}") {
        //            setTimeout(function func() {
        //                timeout = timeout *2;
        //                drawChart()
        //            }, timeout * 1000);
        //        } else {
        //
        //            performanceObject.courseId = id;
        //            performanceObject.fetch({
        //                beforeSend: setHeader,
        //                success: function (response, jsonData) {
        //                    var perf_view = new PerfomanceView({ model: jsonData, title: "your performance compared to the average performance" })
        //                    if (perf_view)
        //                        perf_view.close();
        //                    $('.content > .row').append(perf_view.render().el);
        //                    app.addRepresentationObject("performance:"+id, jsonData);
        //                }
        //            });
        //        }
        //    }
        //
        //    drawChart();
        //
        //
        //}else{
        //    var perf_view = new PerfomanceView({ model: _p.get("content"), title: "Performance graph" })
        //    if (perf_view)
        //        perf_view.close();
        //    $('.content > .row').append(perf_view.render().el);
        //
        //}


    },


    teacherView: function (id) {

        $('.content > .row').html("");
        this.d3Chart("AverageResourceConsumption"+id, "Resources consumed in this course by all students",
            '/data-proxy/query/resourceTypes/meso/course/'+id+'/d3',
            '/data-proxy/query/result/resourceTypes/meso/course/'+id+'/d3',
            BarChartView);

        this.googleChart("course_calendar_activities:"+id, "Course calendar activities", null,
            "/data-proxy/query/result/calendar/course/"+id, CalendarView);

        this.googleChart("Interactivity"+id, "All students sorted according to activity level",
            "/data-proxy/query/interactivitySort/"+id,
            "/data-proxy/query/result/interactivitySort/"+id+"/gdata",
            ColumnChartView);

    },

    adminView: function (id) {
        var _dom = this.Graphs.get("drop_out_monitor")
        if(!_dom) {
            this.DropOutMonitor = new DropOutMonitor();
            this.DropOutMonitor.fetch({
                beforeSend: setHeader,
                success: function (response, jsonData) {
                    app.showView('.content > .row', new BubbleView({ model: jsonData, title: "DropOut monitor" }));
                    app.addRepresentationObject("drop_out_monitor", jsonData);
                }
            });
        }else{
            app.showView('.content > .row', new BubbleView({model: _dom.get("content"), title: "DropOut monitor" }));
        }
        console.log(this.Graphs);

    },
    showView: function(selector, view) {
        if (this.currentView)
            this.currentView.close();
        $(selector).html(view.render().el);
        this.currentView = view;
        return view;
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
    loadCourses: function(){
        var courses = new CoursesCollection();
        courses.fetch({
            beforeSend: setHeader,
            success: function(a, b) {
                $("ul.list-courses").html("");
                _.each(b.courses, function(course){
                    $('ul.list-courses').append(new CourseItemView({ model: course }).render().el);
                });

            }
        });
    },
    addRepresentationObject: function (id, data){
        var representationObject = new RepresentationObject();
        representationObject.set("content", data);
        representationObject.set("id", id);
        app.Graphs.add(representationObject);
    }
});


tpl.loadTemplates(['main', 'user', 'user_sidebar', 'dashboard-learner', 'dashboard-teacher', 'dashboard-admin',
    'widget', 'course-item'], function () {
    app = new AppRouter();
    Backbone.history.start();
});
