jQuery(document).ready(function() {
    jQuery("time.timeago").timeago();
});

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
        // if ($.cookie("arlearn.AccessToken") == undefined) {
        //     window.open("/login.jsp","_self")
        // }
    },
    routes: {
        "": "activityView",
        "myActivities": "activityView",
        "oauth": "oauth", // If we managed to redirect the login callback to the route "oauth" then we can generate everything there
        "logout": "logout",
        "mooc/loginData": "loginData",
        "mooc/eco": "dropoutView",
        "mooc/multiculturalism": "multiculturalism",
        "mooc/:id/student": "studentView",
        "mooc/:id/student/resource": "studentResourceView",
        "mooc/:id/student/performance": "studentPerformanceView",
        "mooc/:id/student/path": "studentDropOutView",
        "mooc/:id/teacher": "teacherView",
        //"rob/:id/course": "robCourseView",
        //"rob/course": "robCourseAllView",
        "mooc/:id/teacher/forum": "forumCourseView",
        "mooc/:id/teacher/resource": "teacherResourceConsumptionView",
        "mooc/:id/teacher/studentInteractivity": "teacherStudentInteractivityView",
        "mooc/:id/teacher/studentActivity": "teacherStudentActivityView",
        "mooc/:id/teacher/social": "teacherSocialView",
        "mooc/:id/admin": "adminView",
        "sandbox/:id": "sandbox",
        "code/:id": "code"
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
    code: function(id) {


        $.ajax({
                    url: "/data-proxy/xAPI/statements/json/"+id,
                    async: true,
                    headers: {
                        Authorization: getCookie('arlearn.AccessToken')
                    },
            dataType : 'json',
                    success: function (data) {
                        //alert(data)
                        $("div.row").html('<div class="col-md-12"><div class="box box-primary"><div class="box-header"><h3 class="box-title"><i class="fa fa-code"></i> xAPI source</h3></div><div class="box-body"><pre style="font-weight: 600;">'+JSON.stringify(data,undefined,2)+'</pre></div></div></div>');
                    }
                });
    },
    activityView: function(){
        //$("#dashboardtitle").html(i18n.t('myActivities')+"<small>Personal</small>");

        $("#dashboardtitle").html("<span data-i18n='myActivities'><small>Personal</small></span>");
        $("#explanation").html(i18n.t('activityFeedExplanation'));
        $('.content > .row').html("");
        $(document).i18n();

        $.ajax({
            url: "/data-proxy/query/timeline",
            async: true,
            headers: {
                Authorization: getCookie('arlearn.AccessToken')
            },
            success: function (data) {
            }
        });


        var timeLineView = new TimeLineView();
        app.showView('.content > .row', timeLineView );

        var timeout = 1;
        function drawTimeline() {
            $.ajax({
                url: '/data-proxy/query/result/timeline',
                dataType: "json",
                headers: {
                    Authorization : getCookie('arlearn.AccessToken')
                },
                async: true,
                success: function(jsonData) {
                    if (Object.getOwnPropertyNames(jsonData).length == 0 ) {
                        setTimeout(function func() {
                            timeout = timeout *2;
                            drawTimeline()
                        }, timeout * 1000);
                    } else {
                        var items = new TimeLineCollection();
                        items.fetch({
                            beforeSend: setHeader,
                            success: function(a, b) {
                                $("ul.user-timeline").html("");
                                _.each(b.results, function(timelineItem){
                                    $('ul.user-timeline').append(new TimeLineItemView({ model: timelineItem }).render().el);
                                });
                                jQuery("time.timeago").timeago();
                                $(document).i18n();
                            }
                        });
                    }

                }
            });
        }
        drawTimeline();




    },
    sandbox: function(id){
        //$('.content > .row').html("");
        //$(".content-header").html("<h2 class='page-header'>Dropout view</h2><p ><small><b>The dropout</b> view is a widget</small></p>")


        //this.googleChart("loginData", "Logins of all ECO users",
        //    "/data-proxy/query/calendar/logins", "/data-proxy/query/result/calendar/logins/gdata", CalendarView);
        //
        //this.googleChart("loginDataMe", "Your login data", "/data-proxy/query/calendar/logins/user",
        //    "/data-proxy/query/result/calendar/logins/user/gdata", CalendarView);

        //this.googleChart("loginData",
        //    "Logins of all ECO users", "/data-proxy/query/calendar/logins",
        //    "/data-proxy/query/result/calendar/logins/gdata", AnnotationChartView);


        //this.d3Chart("courseSocial"+id, "Graphs representing who follows who in this course",
        //    null,
        //    '/json.json',
        //    SandboxView3Followers);


        this.googleChart("allCourseActivities", "DropoutView",
            null,
            '/json.json', AnnotationChartView);

        //this.googleChart("loginDataMe",
        //    "Your login data", "/data-proxy/query/calendar/logins/user",
        //    "/data-proxy/query/result/calendar/logins/user/gdata", AnnotationChartView);


        //var _dom = this.Graphs.get("drop_out_monitor")
        //if(!_dom) {
        //    this.DropOutMonitor = new DropOutMonitor();
        //    this.DropOutMonitor.fetch({
        //        beforeSend: setHeader,
        //        success: function (response, jsonData) {
        //            app.showView('.content > .row', new BubbleView({ model: jsonData, title: "DropOut monitor" }));
        //            app.addRepresentationObject("drop_out_monitor", jsonData);
        //        }
        //    });
        //}else{
        //    app.showView('.content > .row', new BubbleView({model: _dom.get("content"), title: "DropOut monitor" }));
        //}
        //console.log(this.Graphs);


    },






    d3Chart: function(graphId, widgetTitle, queryUrl, resultUrl, viewObject, id){
        var timeout = 1;
        var _p = this.Graphs.get(graphId)
        if(!_p) {
            var view = new viewObject({ title: widgetTitle, csv: resultUrl+'/csv' });
            app.showView('.content > .row', view );


            if (queryUrl) $.ajax({
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
                        $(document).i18n();
                    },
                    error: function() {
                        //console.log('now ...')
                        //$('#path_'+(id.replaceAll('.', '').replaceAll(':', '_'))).hide();
                        //view.visnotavailable();
                        $("#explanation").html("");
                        $('.content > .row').html("Visualisation not available for this course");
                    }
                });
            }
            drawChart();
            return view;


        } else {
            $(document).i18n();
            return _p;

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
                            var perf_view = new viewObject({ model: jsonData, title: widgetTitle, csv: resultUrl+"/csv" })
                            if (perf_view)
                                perf_view.close();
                            $('.content > .row').append(perf_view.render().el);
                            app.addRepresentationObject(graphId, jsonData);
                        }
                        $(document).i18n();
                    }
                });
            }
            drawChart();
        }else{
            var perf_view = new viewObject({ model: _p.get("content"), title: widgetTitle, csv: resultUrl+"/csv" })
            if (perf_view)
                perf_view.close();
            $('.content > .row').append(perf_view.render().el);
            $(document).i18n();

        }
    },

    loginData: function() {

        $("#dashboardtitle").html("<span data-i18n='ecoLoginTitle'>Login Data</span><small>Micro and Macro</small>");
        $("#explanation").html("<span data-i18n='ecoLoginExplanation'>Login Data</span><small>");

        $('.content > .row').html("");


        this.googleChart("loginData",
            "<span data-i18n='ecoLoginChartTitle'>Login Data</span>", "/data-proxy/query/calendar/logins",
            "/data-proxy/query/result/calendar/logins/gdata", AnnotationChartView);


        this.googleChart("loginDataMe",
            "Your login data", "/data-proxy/query/calendar/logins/user",
            "/data-proxy/query/result/calendar/logins/user/gdata", AnnotationChartView);
        $(document).i18n();
    },

    // dropoutView:function(){
    //
    //     $("#dashboardtitle").html("<span data-i18n='ecoMonitorTitle'>Login Data</span> <small>Macro</small>");
    //     $("#explanation").html("<span data-i18n='ecoMonitorExplanation'>Login Data</span>");
    //
    //     $('.content > .row').html("");
    //
    //     this.googleChart("allCourseActivities", "<span data-i18n='ecoMonitorTitle'>Login Data</span>",
    //         '/data-proxy/query/dropoutMonitor',
    //         '/data-proxy/query/result/dropoutMonitor', BubbleView);
    //
    //     this.googleChart("allCourseActivitiesLang", "<span data-i18n='ecoMonitorTitle'>Login Data</span>",
    //         '/data-proxy/query/dropoutMonitor',
    //         '/data-proxy/query/result/dropoutMonitorLang', BubbleViewLang);
    //
    //     $(document).i18n();
    //
    //
    // },

    dropoutView:function(){

        $("#dashboardtitle").html("<span data-i18n='ecoMonitorTitle'>Login Data</span> <small>Macro</small>");
        $("#explanation").html("<span data-i18n='ecoMonitorExplanation'>Login Data</span>");

        $('.content > .row').html("");

        this.googleChart("allCourseActivitiesLang", "<span data-i18n='ecoMonitorTitle'>Login Data</span>",
            '/data-proxy/query/dropoutMonitor',
            '/data-proxy/query/result/dropoutMonitor', BubbleView);


        $(document).i18n();


    },

    multiculturalism:function(){
        $("#dashboardtitle").html("<span data-i18n='multiculturalism'>Login Data</span> <small>Macro</small>");
        $("#explanation").html("<span data-i18n='multiculturalismExpl'>Login Data</span>");

        $('.content > .row').html("");


        this.googleChart("allCourseActivitiesLang", "<span data-i18n='ecoMonitorTitle'>Language distribution for all ECO MOOCs</span>",
            '/data-proxy/query/dropoutMonitor',
            '/data-proxy/query/result/dropoutMonitorLang', BubbleViewLang);


        this.d3Chart("LanguagesUsed", "Amount of activities per language",
            '/data-proxy/query/langDistribution',
            '/data-proxy/query/result/langDistribution',
            BarChartView);

        $(document).i18n();


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

    studentView: function (id) {
        $("#dashboardtitle").html("<span data-i18n='studentDefaultTitle'>title</span> <small>Micro</small>");
        $("#explanation").html("<span data-i18n='studentDefaultExplanation'>explanation</span>");

        $('.content > .row').html("");


        this.googleChart("student_calendar_activities:"+id, i18n.t('studentDefaultChartTitle'),
            null,
            "/data-proxy/query/result/calendar/user/gdata", CalendarView);
        $(document).i18n();
    },

    studentResourceView: function(id) {
        $("#dashboardtitle").html("<span data-i18n='studentResourcesTitle'>title</span> <small>Micro</small>");
        $("#explanation").html("<span data-i18n='studentResourcesExplanation'>explanation</span>");

        $('.content > .row').html("");

        this.d3Chart("ResourceConsumptionUser", i18n.t('studentResourcesChartTitle'),
            '/data-proxy/query/resourceTypes/course/'+id+'/d3',
            '/data-proxy/query/result/resourceTypes/course/'+id+'/d3',
            BarChartView);
        $(document).i18n();
    },


    studentPerformanceView: function(id) {
        $("#dashboardtitle").html("<span data-i18n='studentPerformanceTitle'>title</span> <small>Micro</small>");
        $("#explanation").html("<span data-i18n='studentPerformanceExplanation'>explanation</span>");

        $('.content > .row').html("");
        this.googleChart("Progess", i18n.t('studentPerformance2ChartTitle'),
            "/data-proxy/query/progress/"+id+"/user",
            "/data-proxy/query/result/progress/"+id+"/gdata",
            ProgressView);
        this.googleChart("student_average_learner_activities:"+id, i18n.t('studentPerformanceChartTitle'),
            '/data-proxy/query/averageLearnerActivities/'+id,
            "/data-proxy/query/result/averageLearnerActivities/" + id, PerfomanceView);
        $(document).i18n();
    },

    studentDropOutView:function(id) {
        var resizeTimer;
        $("#dashboardtitle").html("MOOC path monitor");
        $('.content > .row').html("");
        $("#explanation").html("");
        $("#explanation").append('<div class="row"><div id="legendIcons" class="col-lg-8"><i class="fa fa-film" aria-hidden="true">&nbsp;Access a video</i>&nbsp;&nbsp;&nbsp; <i class="fa fa-tasks" aria-hidden="true">&nbsp;Access a task</i>&nbsp;&nbsp;&nbsp; <i class="fa fa-paper-plane-o" aria-hidden="true">&nbsp;Submit a task</i>&nbsp;&nbsp;&nbsp; <i class="fa fa-pencil-square" aria-hidden="true">&nbsp;Access an assessement</i>&nbsp;&nbsp;&nbsp; <i class="fa fa-paper-plane" aria-hidden="true">&nbsp;Submit an assessement</i>&nbsp;&nbsp;&nbsp; <i class="fa fa-users" aria-hidden="true">&nbsp;Comment a forum</i>&nbsp;&nbsp;&nbsp;<i class="fa fa-file-text-o" aria-hidden="true">&nbsp;Access a article, document, ...</i>&nbsp;&nbsp;&nbsp; </div> <div class="col-lg-4"> <div id="legendSizeContainer"> <div><img src="dist/img/blackDot.png"/>&nbsp Model line</div> <div id="legendStudentLine" style=" float: left; margin-top:6px; width:40px; height:7px; background-color:white"></div><div>&nbsp My line</div> <div class="legendSize" style="float:left"><i class="fa fa-film" id="legendColorActive" style="color: green; float:left;" aria-hidden="true"></i>&nbsp; Accessed &nbsp; &nbsp; &nbsp; </div><div class="legendSize"><i class="fa fa-film" style="color:red; float:left"  id="legendColorNotActive" aria-hidden="true"></i>&nbsp; Not accessed</div> </div> </div> </div>');
        $("#explanation").append('<br><button id="loadStudentButtons" style="cursor:pointer"><i class="fa fa-line-chart" aria-hidden="true"></i> Load all peer students in this MOOC<br/></button>');
        $("#explanation").append('<div>Zoom in: <span id="zoomIn" style="cursor: zoom-in" class="glyphicon glyphicon-plus"></span> &nbsp; &nbsp; Zoom out:<span id="zoomOut" style="cursor: zoom-out" class="glyphicon glyphicon-minus"></span> </div>');
        //$("#explanation").append('<div>Load data other students:<span id="loadOtherStudents" style="cursor: zoom-out" class="glyphicon glyphicon-minus"></span> </div>');

        $("#explanation").append('<br>Filter peer students:<input type="text" id="amount" readonly style="border:0; background-color:transparent; color:#f6931f; font-weight:bold;"> <div id="slider-range"></div>');
        localStorage.setItem("scaleFactor","1");

        var view = this.d3Chart("studentDropOutView"+id, "Student paths",
            null,
            '/activityPath/'+id.replace(":", "_")+'.json',
            DropoutStudentView);

        $.ajax({
            url: '/data-proxy/query/result/studentPaths/'+id+'/me',
            async: true,
            headers: {
                Authorization: getCookie('arlearn.AccessToken')
            },
            success: function (data) {
                if (Object.getOwnPropertyNames(data).length != 0 ) {
                    view.moocPath.setStudentActivities(data.rows);
                    view.updateMyData();
                }
            }
        });

        //$("#loadOtherStudents").bind("click",function(){
        //    $("#loadOtherStudents").hide();
        //
        //    }
        //);

        $("#zoomIn").bind("click",function(){
                var v = parseInt(localStorage.getItem("scaleFactor"));
                if(parseInt(localStorage.getItem("scaleFactor"))>1){
                    localStorage.setItem("scaleFactor",parseInt(v/2));
                    view.reScaleYAxis();
                };
            }
        );

        $("#zoomOut").bind("click",function(){
                var v = parseInt(localStorage.getItem("scaleFactor"));
                localStorage.setItem("scaleFactor",v*2);
                view.reScaleYAxis();
            }
        );



        $( function() {$( "#slider-range" ).slider({
            range: true,
            min: 0,
            max: 100,
            values: [ 0, 100 ],
            slide: function( event, ui ) {
                clearTimeout(this.resizeTimer);
                this.resizeTimer = setTimeout(function() {
                    $( "#amount" ).val( ui.values[ 0 ] + " - " + ui.values[ 1 ] + " %");
                    view.hideLines(ui.values[0],ui.values[1]);},300);
            }
        });
            $( "#amount" ).val($( "#slider-range" ).slider( "values", 0 ) + " - " + $( "#slider-range" ).slider( "values", 1 ) );});

        $("#loadStudentButtons").bind("click",function(){
            view.moocPath.studentTimeLinesActivities = [];
            var timeout = 1;
            for (var i=1;i<800;i++) {
                $.ajax({
                    url: '/data-proxy/query/result/studentPaths/'+id+'/'+i,
                    async: true,
                    headers: {
                        //Authorization: getCookie('arlearn.AccessToken')
                    },
                    success: function (data) {
                        view.moocPath.setStudentTimeLinesActivities(data.rows);
                        view.addOtherStudent(view.moocPath.getNewStudentPathGraph(view.moocPath.studentTimeLinesActivities[view.moocPath.studentTimeLinesActivities.length-1]),view.moocPath.studentTimeLinesActivities.length-1);


                    }
                });
            };


            $("#loadStudentButtons").unbind("click");
        });



    },




    teacherView: function (id) {


        $("#dashboardtitle").html("MOOC Data <small>Meso</small>");
        $("#explanation").html("This page shows all activities in the course organized in a calendar view");

        $('.content > .row').html("");


        this.googleChart("course_calendar_activities:"+id, "Course calendar activities",
            "/data-proxy/query/calendar/course/"+id,
            "/data-proxy/query/result/calendar/course/"+id, CalendarView);

        this.googleChart("course_calendar_activities_timeline:"+id, "Course activities organized on a timeline",
            null,
            "/data-proxy/query/result/calendar/course/"+id, AnnotationChartView);



    },

    forumCourseView: function (id) {
        $("h1.dashboardtitle").html("Forum<small>Group</small>")

        $('.content > .row').html("");


        this.googleChart("course_threadopening:"+id, "Thread openening messages in course",
            "data-proxy/query/calendar/course/threadopening/"+id,
            "/data-proxy/query/result/calendar/course/threadopening/", AnnotationChartView);
        this.googleChart("course_calendar_activities_all2:"+id, "Course calendar activities",
            null,
            "/data-proxy/query/result/calendar/course/all", AnnotationChartView);



    },
    teacherResourceConsumptionView: function (id) {
        $("#dashboardtitle").html("Resource consumption<small>Meso</small>");
        $("#explanation").html("This page shows per resource in the course, how often the resourcetype was consumed. This graph gives an overview of what kind of content is more consumed in this course.");
        $('.content > .row').html("");
        this.d3Chart("AverageResourceConsumption"+id, "Resources consumed in this course by all students",
            '/data-proxy/query/resourceTypes/meso/course/'+id+'/d3',
            '/data-proxy/query/result/resourceTypes/meso/course/'+id+'/d3',
            BarChartView);
    },
    // teacherStudentInteractivityView: function (id) {
    //     $("#dashboardtitle").html(i18n.t('studentActivitySortedTitle')+" <small>Meso</small>");
    //     $("#explanation").html(i18n.t('studentActivitySortedExplanation'));
    //     $('.content > .row').html("");
    //     this.googleChart("Interactivity"+id, i18n.t('studentActivitySortedChartTitle'),
    //         "/data-proxy/query/interactivitySort/"+id,
    //         "/data-proxy/query/result/interactivitySort/"+id+"/gdata",
    //         ColumnChartView);
    // },
    //
    // teacherStudentActivityView_old : function(id) {
    //     $("#dashboardtitle").html("Student activity <small>Meso</small>");
    //     $("#explanation").html("This graph below sorts all students according to there level of activity. Active students are displayed more the right, passive students are displayed at the left. This page therefor gives a rough overview of what ratio of the students is active");
    //     $('.content > .row').html("");
    //     this.d3Chart("AverageResourceConsumption"+id, "Resources consumed in this course by all students",
    //         null,
    //         '/test/trend/teacherStudentActivity.json',
    //         TeacherStudentActivityView);
    // },

    teacherStudentActivityView : function(id) {
        $("#dashboardtitle").html(i18n.t('studentActivitySortedTitle')+"<small>Meso</small>");
        $("#explanation").html(i18n.t('studentActivitySortedExplanation'));
        $("#explanation").append('<div class="row" id="IenC_ControlPanel"><div class="col-sm-6"><h4>'+i18n.t('studentActivitySortedExplanationb')+'&nbsp;<a href="#"><span data-toggle="tooltipClustering" title="With this tool you can group your data in different ways.(Ex.Median, KClustering(AI)" class="glyphicon glyphicon-info-sign" aria-hidden="false"></span></a></h4><label><input type="radio" value="0" name="clusteringRadioButton">Median</label><label><input type="radio" value="1" name="clusteringRadioButton">K-Clustering (AI)</label><div id="algoritmInformation" class="well well-sm"><div id="clusterInformationTitle"></div><div id="clusterInformationContent"></div><div id="clusterInformationLink"></div></div></div></div>');
        $('.content > .row').html("");
        $("#explanation").append('<div id="IenCInfo">I-Level:</div>');
        localStorage.setItem("clusterType","0");


        var view = this.d3Chart("teacherStudentActivityView"+id, i18n.t('studentActivitySortedChartTitle'),
            "/data-proxy/query/interactivitySort/"+id,
            "/data-proxy/query/result/interactivitySort/"+id+"/gdata",
            TeacherStudentActivityView);

        $.ajax({
            url: "/data-proxy/query/interactivitySort/"+id+"/me",
            async: true,
            headers: {
                Authorization: getCookie('arlearn.AccessToken')
            },
            success: function (data) {
            }
        });

        var myLine;
        function retrieveMyActivity() {
            $.ajax({
                url: "/data-proxy/query/result/interactivitySort/"+id+"/me/gdata",
                dataType: "json",
                headers: {
                    Authorization : getCookie('arlearn.AccessToken')
                },
                async: true,
                success: function(jsonData) {
                    if (Object.getOwnPropertyNames(jsonData).length == 0 ) {
                        setTimeout(function func() {
                            timeout = timeout *2;
                            retrieveMyActivity()
                        }, timeout * 1000);
                    } else {
                        myLine = parseInt(jsonData.rows[0].c[1].v);
                        view.makeUserInteractivityLevelLine(myLine);
                    }
                },
                error: function() {
                    //console.log('now ...')
                    //$('#path_'+(id.replaceAll('.', '').replaceAll(':', '_'))).hide();
                    //view.visnotavailable();
                    $("#explanation").html("");
                    $('.content > .row').html("Visualisation not available for this course");
                }
            });
        }
        retrieveMyActivity();


        //view.makeUserInteractivityLevelLine(200);
        $("input[name=clusteringRadioButton]:radio").change(function(){
            localStorage.setItem("clusterType",$("input[name=clusteringRadioButton]:checked").val());
            view.initialize("gdfgfd");
            view.update();
            if (myLine) view.makeUserInteractivityLevelLine(myLine);
        });
    },

    teacherSocialView: function (id) {


        $("#dashboardtitle").html(i18n.t('teacherSocialTitle')+"<small>Meso</small>");
        $("#explanation").html(i18n.t('teacherSocialExplanation'));
        $('.content > .row').html("");

        this.d3Chart("courseSocial"+id, i18n.t('teacherSocialChartTitle'),
            '/data-proxy/query/social/'+id+'/follows',
            '/data-proxy/query/result/social/'+id+'/follows',
            SandboxView3Followers);
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
                var urlPath = window.location.href.substr(window.location.origin.length+1);

                $("ul.list-courses").html("");
                _.each(b.courses, function(course){

                    var renderEl = new CourseItemView({ model: course }).render().el;
                    if (urlPath.indexOf(course.oaiPmhIdentifier) != -1) {
                        renderEl.setAttribute("class", "active");
                    }
                    $('ul.list-courses').append(renderEl);
                });

            },
            error: function (a, b){
                window.open("http://la.ecolearning.eu/login.jsp","_self")
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
    'widget', 'course-item', 'timeline', 'timeline-item'], function () {
    app = new AppRouter();
    Backbone.history.start();
});
