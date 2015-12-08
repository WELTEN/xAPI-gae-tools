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
    },
    routes: {
        "": "login",
        "oauth": "oauth", // If we managed to redirect the login callback to the route "oauth" then we can generate everything there
        "logout": "logout",
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
        var _sca = this.Graphs.get("student_calendar_activities:"+id);
        if(!_sca) {
            this.CalendarActivity = new CalendarActivity();
            //this.CalendarActivity.initialize(studentId);
            this.CalendarActivity.fetch({
                beforeSend: setHeader,
                success: function (response, jsonData) {
                    app.showView('.content > .row', new CalendarView({ model: jsonData, title: "Student calendar activities" }));
                    app.addRepresentationObject("student_calendar_activities:"+id, jsonData);
                }
            });
        }else{
            app.showView('.content > .row', new CalendarView({ model: _sca.get("content"), title: "Student calendar activities" }));
        }

        var _rc = this.Graphs.get("resources_consumed:"+id);
        if(!_rc) {
            this.ResourcesConsumed = new ResourcesConsumed();
            this.ResourcesConsumed.courseId = id;
            this.ResourcesConsumed.fetch({
                beforeSend: setHeader,
                success: function (response, jsonData) {

                    var rc_view = new ResourcesConsumedView({ model: jsonData, title: "Resources Consumed"  })

                    if (rc_view)
                        rc_view.close();
                    $('.content > .row').append(rc_view.render().el);
                    app.addRepresentationObject("resources_consumed:"+id, jsonData);
                }
            });
        }else{

            var perf_view = new ResourcesConsumedView({ model: _rc.get("content"), title: "Resources consumed graph" })
            if (perf_view)
                perf_view.close();
            $('.content > .row').append(perf_view.render().el);
        }


        var _p = this.Graphs.get("performance:"+id)
        if(!_p) {
            var performanceObject= new Perfomance();
            $.ajax({
                url: '/data-proxy/query/averageLearnerActivities/'+id,
                async: true,
                success: function(data){}
            });
            function drawChart() {

                var jsonData = ""
                jsonData = $.ajax({
                    url: "/data-proxy/query/result/averageLearnerActivities/" + id,
                    dataType: "json",
                    headers: {
                        Authorization : getCookie('accessToken')
                    },
                    async: false
                }).responseText;
                if (jsonData == "{}") {
                    setTimeout(function func() {
                        timeout = timeout *2;
                        drawChart()
                    }, timeout * 1000);
                } else {

                    performanceObject.courseId = id;
                    performanceObject.fetch({
                        beforeSend: setHeader,
                        success: function (response, jsonData) {
                            var perf_view = new PerfomanceView({ model: jsonData, title: "Performance graph" })
                            if (perf_view)
                                perf_view.close();
                            $('.content > .row').append(perf_view.render().el);
                            app.addRepresentationObject("performance:"+id, jsonData);
                        }
                    });
                }
            }

            drawChart();


        }else{
            var perf_view = new PerfomanceView({ model: _p.get("content"), title: "Performance graph" })
            if (perf_view)
                perf_view.close();
            $('.content > .row').append(perf_view.render().el);

        }
        console.log(this.Graphs);
    },
    teacherView: function (id) {
        var _cca = this.Graphs.get("course_calendar_activities:"+id)
        if(!_cca) {
            this.CalendarActivityCourse = new CalendarActivityCourse();
            this.CalendarActivityCourse.courseId = id;
            this.CalendarActivityCourse.fetch({
                beforeSend: setHeader,
                success: function (response, jsonData) {
                    app.showView('.content > .row', new CalendarView({ model: jsonData, title: "Course calendar activities "+id }));
                    app.addRepresentationObject("course_calendar_activities:"+id, jsonData);
                }
            });
        }else{
            app.showView('.content > .row', new CalendarView({model: _cca.get("content"), title: "Course calendar activities "+id }));
        }

        var _rc = this.Graphs.get("resources_consumed:"+id);
        if(!_rc) {
            this.CourseResourceTypesConsumed = new CourseResourceTypesConsumed();
            this.CourseResourceTypesConsumed.courseId = id;
            this.CourseResourceTypesConsumed.fetch({
                beforeSend: setHeader,
                success: function (response, jsonData) {

                    var rc_view = new ResourcesConsumedView({ model: jsonData, title: "Resources Consumed"  })

                    if (rc_view)
                        rc_view.close();
                    $('.content > .row').append(rc_view.render().el);
                    app.addRepresentationObject("resources_consumed:"+id, jsonData);
                }
            });
        }else{

            var perf_view = new ResourcesConsumedView({ model: _rc.get("content"), title: "Resources consumed graph" })
            if (perf_view)
                perf_view.close();
            $('.content > .row').append(perf_view.render().el);
        }

        console.log(this.Graphs);

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
                    console.log(course)
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
