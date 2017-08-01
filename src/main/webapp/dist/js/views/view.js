////////
// Users
////////
window.UserView = Backbone.View.extend({
    tagName:  "li",
    className: "dropdown user user-menu",
    initialize:function () {
        this.template = _.template(tpl.get('user'));
    },

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

window.UserSidebarView = Backbone.View.extend({
    tagName:  "div",
    className: "user-panel",
    initialize:function () {
        this.template = _.template(tpl.get('user_sidebar'));
    },

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

window.ProgressView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-6 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);

        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'BarChart',
            dataTable: this.data,
            backgroundColor: { fill:'#76a7fa' },
            options: {
                title: 'You progress in this MOOC',
                chartArea: {width: '50%'},
                hAxis: {
                    title: 'Percentage',
                    minValue: 0,
                    maxValue: 100
                }
            }
        });
        this.$('.box-body').append(chart.render().el);
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['corechart', 'bar']
        });
        return this;
    }
});

window.ActivityAreaChartView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);

        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'AreaChart',
            dataTable: this.data,

            options: {
                title: 'MOOC activity',
                chartArea: {width: '100%'},
                hAxis: {
                    title: 'Time'
                },
                chart: {
                    interpolateNulls: true
                }
            }
        });
        this.$('.box-body').append(chart.render().el);
        var closure = this;
        $(window).resize(function(){
            closure.drawVisualization();
        });
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['corechart']
        });
        return this;
    }
});

window.AnnotationChartView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'AnnotationChart',
            dataTable: this.data,

            options: {
                title: 'MOOC activity',
                displayAnnotations: false,
                hAxis: {
                    title: 'Time'
                },
                chartArea: {
                    left: 0,
                    top: 0,
                    width: 100,
                    height: 150
                }
            }

        });
        this.$('.box-body').append(chart.render().el);
        this.$('.box-body').attr("style","padding-right:30px;");
        var closure = this;
        $(window).resize(function(){
            closure.drawVisualization();
        });
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['annotationchart']
        });
        return this;
    }
});

window.ColumnChartView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'ColumnChart',
            dataTable: this.data,
            options: {
                //title: this.title,
                chartArea: {width: '90%'},
                'backgroundColor': 'transparent',


                //,
                //hAxis: {
                //    title: 'Percentage',
                //    minValue: 0,
                //    maxValue: 100
                //}
            }
        });
        this.$('.box-body').append(chart.render().el);
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['corechart', 'bar']
        });
        return this;
    }
});

window.GaugeView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-6 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'Gauge',
            dataTable: this.data,
            backgroundColor: { fill:'#76a7fa' },
            options: {
                width: 400, height: 120,
                redFrom: 90, redTo: 100,
                yellowFrom:75, yellowTo: 90,
                minorTicks: 5
            }
        });
        this.$('.box-body').append(chart.render().el);
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['gauge']
        });
        return this;
    }
});

window.CalendarView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv= options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'Calendar',
            dataTable: this.data,
            backgroundColor: { fill:'#76a7fa' },
            options: {
                width: '100%',
                height:340,

                calendar: {
                    monthLabel: {
                        fontName: 'Times-Roman',
                        fontSize: 12,
                        color: '#fff',
                        bold: true,
                        italic: true
                    },
                    monthOutlineColor: {
                        stroke: '#981b48',
                        strokeOpacity: 0.8,
                        strokeWidth: 2
                    },
                    unusedMonthOutlineColor: {
                        stroke: '#bc5679',
                        strokeOpacity: 0.8,
                        strokeWidth: 1
                    },
                    dayOfWeekLabel: {
                        fontName: 'Times-Roman',
                        fontSize: 12,
                        color: '#fff',
                        bold: true,
                        italic: true
                    },
                    yearLabel: {
                        fontName: 'Source Sans Pro',
                        fontSize: 32,
                        color: '#fff',
                        bold: true,
                        italic: true
                    },
                    cellColor: {
                        stroke: '#76a7fa',
                        strokeOpacity: 0.5,
                        strokeWidth: 1
                    },
                    underYearSpace: 10,
                    underMonthSpace: 16
                }
            }
        });
        this.$('.box-body').append(chart.render().el);

    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['calendar']
        });
        return this;
    }
});

window.BubbleView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-10 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'BubbleChart',
            dataTable: this.model,
            options: {
//                title: 'DropOutMonitor: Correlation between number of course launches, number of activities and number of users for all Moocs',
                titlePosition :'none',
                hAxis: {title: 'number of activities', logScale:true},
                vAxis: {title: 'number of launches', logScale:true},
                backgroundColor: { fill:'transparent' },
                height:400,
                bubble: {textStyle: {fontSize: 11}}
            }
        });
        this.$('.box-body').append(chart.render().el);
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['calendar']
        });
        return this;
    }
});

window.BubbleViewLang = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-10 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'BubbleChart',
            dataTable: this.model,
            options: {
//                title: 'DropOutMonitor: Correlation between number of course launches, number of activities and number of users for all Moocs',
                titlePosition :'none',
                hAxis: {title: 'number of registered users', logScale:true},
                vAxis: {title: 'number of launches', logScale:true},
                backgroundColor: { fill:'transparent' },
                height:400,
                bubble: {textStyle: {fontSize: 11}}
            }
        });
        this.$('.box-body').append(chart.render().el);
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['calendar']
        });
        return this;
    }
});

window.ResourcesConsumedView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-10 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'BarChart',
            dataTable: this.data,
            options: {
                chart: {
                    title: 'Resources consumed'
                },
                bar: {groupWidth: "95%"},
                legend: { position: 'top', maxLines: 3 },
                backgroundColor: { fill:'transparent' },
                bars: 'horizontal' // Required for Material Bar Charts.
            }
        });
        this.$('.box-body').append(chart.render().el);
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['bar']
        });
        return this;
    }
});

window.PerfomanceView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-6 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        var chart = new Backbone.GoogleChart({
            chartType: 'BarChart',
            dataTable: this.data,
            options: {
                chart: {
                    title: 'Group vs. individual performance'
                },
                backgroundColor: { fill:'transparent' },
                bars: 'horizontal' // Required for Material Bar Charts.
            }
        });
        this.$('.box-body').append(chart.render().el);
    },
    render: function() {
        google.load('visualization', '1', {
            'callback': _.bind(this.drawVisualization, this),
            'packages': ['bar']
        });
        return this;
    }
});

window.DashBoardLearnerView = Backbone.View.extend({
    tagName:  "div",
    className: "dashboard-learner",
    initialize:function () {
        this.template = _.template(tpl.get('dashboard-learner'));
    },

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

window.DashBoardTeacherView = Backbone.View.extend({
    tagName:  "div",
    className: "dashboard-learner",
    initialize:function () {
        this.template = _.template(tpl.get('dashboard-teacher'));
    },

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

window.DashBoardAdminView = Backbone.View.extend({
    tagName:  "div",
    className: "dashboard-learner",
    initialize:function () {
        this.template = _.template(tpl.get('dashboard-admin'));
    },

    render:function () {
        $(this.el).html(this.template(this.model));
        return this;
    }
});

window.CourseItemView = Backbone.View.extend({
    tagName:  "li",
    initialize:function () {
        this.template = _.template(tpl.get('course-item'));
    },
    render:function () {
        $(this.el).html(this.template(this.model));

        return this;
    }
});

window.TimeLineView = Backbone.View.extend({
    tagName:  "ul",
    className: "timeline user-timeline",

    initialize:function () {
        this.template = _.template(tpl.get('timeline'));
    },

    render:function () {
        $(this.el).html(this.template());
        return this;
    },

    update: function(options){

    }
});

window.TimeLineItemView = Backbone.View.extend({
    tagName:  "li",
    initialize:function () {
        this.template = _.template(tpl.get('timeline-item'));
    },
    render:function () {
        var model =  { model: this.model, verb: "this.model.toJSON().verbId" };
        $(this.el).html(this.template(model));
        $(document).i18n();
        return this;
    }
});

window.BarChartView = Backbone.View.extend({
    tagName:  "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    xScale: null,
    yScale: null,
    margin: {},
    barSvg: null,
    height: 0,
    initialize: function(options){

        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;

        $(this.el).html(this.template({ title: this.title, csv: this.csv }));
        this.container = this.$('.box-body')[0];

        $(this.container).html("");

        this.margin = {top: 50, bottom: 50, left:150, right: 40};
        if($( window ).width() < 760){
            var width = $( window ).width()- 50 - this.margin.left - this.margin.right;
        }else{
            var width = $( window ).width() - 300 - this.margin.left - this.margin.right;
        }
        this.height = 400 - this.margin.top - this.margin.bottom;

        this.xScale = d3.scale.linear().range([0, width]);
        this.yScale = d3.scale.ordinal().rangeRoundBands([0, this.height], 1.8,0);


        var numTicks = 5;
        var xAxis = d3.svg.axis().scale(this.xScale)
            .orient("top")
            .tickSize((-this.height))
            .ticks(numTicks);

        var svg = d3.select(this.container).append("svg")
            .attr("width", width+this.margin.left+this.margin.right)
            .attr("height", this.height+this.margin.top+this.margin.bottom+50)
            .attr("class", "base-svg")
            ;


        this.barSvg = svg.append("g")
            .attr("transform", "translate("+this.margin.left+","+this.margin.top+")")
            .attr("class", "bar-svg");

        var x = this.barSvg.append("g").attr("class", "x-axis");


    },
    update: function(options){

        //var data = JSON.parse(options);
        data = options;
        var xMax = d3.max(data, function(d) { return d.rate; } );
        var xMin = 0;

        this.xScale.domain([xMin, xMax]);
        this.yScale.domain(data.map(function(d) { return d.label; }));

        var yScale = this.yScale;
        var xScale = this.xScale;

        d3.select(".base-svg").append("text")
            .attr("x", this.margin.left)
            .attr("y", (this.margin.top)/2)
            .attr("text-anchor", "start")
            .text(this.title)
            .attr("class", "title");

        var groups = this.barSvg.append("g").attr("class", "labels")
            .selectAll("text")
            .data(data)
            .enter()
            .append("g");

        groups.append("text")
            .attr("x", "0")
            .attr("y", function(d) { return yScale(d.label); })
            .text(function(d) { return d.label; })
            .attr("text-anchor", "end")
            .attr("dy", ".9em")
            .attr("dx", "-.32em")
            .attr("id", function(d,i) { return "label"+i; });

        var bars = groups
            .attr("class", "bars")
            .append("rect")
            .attr("width", function(d) { return xScale(d.rate); })
            .attr("height", this.height/20)
            .attr("x", xScale(xMin))
            .attr("y", function(d) { return yScale(d.label); })
            .attr("id", function(d,i) { return "bar"+i; });

        groups.append("text")
            .attr("x", function(d) { return xScale(d.rate); })
            .attr("y", function(d) { return yScale(d.label); })
            .text(function(d) { return d.rate; })
            .attr("text-anchor", "end")
            .attr("dy", "1.2em")
            .attr("dx", "-.32em")
            .attr("id", "precise-value");

        bars
            .on("mouseover", function() {
                var currentGroup = d3.select(this.parentNode);
                currentGroup.select("rect").style("fill", "brown");
                currentGroup.select("text").style("font-weight", "bold");
            })
            .on("mouseout", function() {
                var currentGroup = d3.select(this.parentNode);
                currentGroup.select("rect").style("fill", "steelblue");
                currentGroup.select("text").style("font-weight", "normal");
            });
    },
    render: function(){

        return this;
    }
});



window.DropoutStudentView = Backbone.View.extend({
    tagName:  "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    xScale: null,
    yScale: null,
    margin: {},
    barSvg: null,
    height: 0,
    xatt : null,
    yatt:null,
    xAxis:null,
    yAxis:null,
    moocPath:null,
    amountOfLines:null,
    width:null,



    initialize: function(options){

        if(this.moocPath === null) {
            this.moocPath = new MOOCpath();
        }
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));
        this.container = this.$('.box-body')[0];

        $(this.container).html("");


        this.margin = {top: 25, right: 20, bottom: 40, left: 40};
        width = $('.content').width() - this.margin.left - this.margin.right;
        this.height = 300 - this.margin.top - this.margin.bottom;


        var x = d3.scale.ordinal()
            .rangeBands([0, width],1);

        var y = d3.scale.linear()
            .range([this.height, 0]);

        this.xatt = x;
        this.yatt = y;
        this.xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

        this.yAxis = d3.svg.axis()
            .scale(y)
            .orient("left")
            .ticks(20, ".");

        svg = d3.select(this.container).append("svg")
            .attr("width", width + this.margin.left + this.margin.right)
            .attr("height", this.height + this.margin.top + this.margin.bottom)
            .append("g")
            .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

        svg = d3.select(this.container).append("svg")
            .attr("viewBox", "0 0 "+(width + this.margin.left + this.margin.right)+" "+(this.height + this.margin.top + this.margin.bottom))
            .classed("svg-container", true)
            .classed("svg-content-responsive", true)
            .append("g")
            .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

        line = d3.svg.line()
            .x(function(d) { return x(d.serial); })
            .y(function(d) { return y(d.relativeTime); })
            .interpolate('linear');

        lineAll = d3.svg.line()
            .x(function(d) { return x(d.serial); })
            .y(function(d) { return y(d.relativeTimeCont); })
            .interpolate('linear');

        lineStudent = d3.svg.line()
            .x(function(d) { return x(d.serial); })
            .y(function(d) { return y(d.studentActivityCont); })
            .interpolate('linear');




    },


    update: function(options){
        if(this.moocPath === null){
            this.moocPath = new MOOCpath();
            this.moocPath.modelActivities = options.activityPath;
        }
        if (this.moocPath.modelActivities == null) {
            this.moocPath.modelActivities = options.activityPath;
        }

        var x = this.xatt;
        var y = this.yatt;
        x.domain(this.moocPath.modelActivities.map(function(d) { return d.serial; }));
        y.domain([0, this.moocPath.getHightYAxis()* parseInt(localStorage.getItem("scaleFactor"))]);
//INIT GRAPH
        svg.append("g")
            .attr("class", "xAxis")
            .attr("id","StudentPathXaxis")
            .attr("transform", "translate(0," + this.height + ")")
            .call(this.xAxis)
            .append("text")
            .attr("x",$('.content').width()-50)
            .attr("y",0)
            .style("text-anchor","end")
            .text("ACTIVITIES");

        svg.append("g")
            .attr("class", "yAxis")
            .attr("id","StudentPathYaxis")
            .call(this.yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 2)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("WEEKS (AFTER FIRST ACCESS ACTIVITY)")
            .style("font-size","0.8em");


//END


//DRAW model line with icons and colors
        svg.append("path")
            .attr("class", "lineModel")
            .attr("d", line(this.moocPath.modelActivities))
            .attr('stroke', 'black')
            .attr('stroke-width', 5)
            .attr('fill', 'none');

        svg.selectAll(".activity")
            .data(this.moocPath.modelActivities)
            .enter().append("circle")
            .attr("class", "activity")
            .attr("r", 3)
            .attr("cx", function(d) { return x(d.serial); })
            .attr("cy", function(d) { return y(d.relativeTime); })
            .style("fill", "white");


//END

    },


    updateMyData: function(){

        var x = this.xatt;
        var y = this.yatt;


//DRAW STUDENT LINE
        svg.append("path")
            .attr("class", "lineStudent")
            .attr("d", lineStudent(this.moocPath.getGraphStudentsActivitiesRelative()))
            .attr('fill', 'none')
            .attr('stroke', 'white')
            .attr('stroke-width', 5);//wijziging
        //wijziging opacity verwijderd


        svg.selectAll(".activity2")
            .data(this.moocPath.getGraphStudentsActivitiesRelative())
            .enter().append("circle")
            .attr("class", "activity2")
            .attr("r", 5)
            .attr("cx", function(d) { return x(d.serial);})
            .attr("cy", function(d) { return y(d.studentActivityCont); })
            .style("fill", function (d) {return getColorIcon(d.studentActivity);});

        svg.selectAll(".activityFig")//wijziging deze programmablock heb ik van de model weggehaald en hier geplaatst
            .data(this.moocPath.getGraphStudentsActivitiesRelative())
            .enter().append("text")
            .attr("x",function(d) { return x(d.serial)-7;})
            .attr("y",this.height+32)
            .attr("font-family","FontAwesome")
            .attr('font-size', function(d) { return '1em';} )
            .text(function(d) { return getCorrectFontAwesomeIcon(d.objectDefinition, d.verbId); })
            .style("fill", function (d) {return getColorIcon(d.studentActivity);});

//END

    },
    addOtherStudent: function(data,i) {
        var id = i;
        var x = this.xatt;
        var y = this.yatt;
        var graphData = data;
        var color = graphData.color;
        svg.append("path")
            .attr("class", "line"+id)
            .attr("d", lineAll(graphData))
            .attr('stroke', color)
            .attr('stroke-width', 1)
            .attr('fill', 'none')
            .attr("opacity",0.7); //wijziging opacity toegevoegd: zo blijft de lijn van de student altijd goed zichtbaar

        svg.selectAll(".activityAll"+ id)
            .data(graphData)
            .enter().append("circle")
            .attr("class", "activityAll"+ id)
            .attr("r", function(d){if(d.relativeTimeCont=== 0){return 0} else {return 1.5}})
            .attr("cx", function(d) { return x(d.serial); })
            .attr("cy", function(d) { return y(d.relativeTimeCont); })
            .style("fill", function (d) {return getColorIcon(d.relativeTime);});






    },





    hideLines: function(firstSlider, secondSlider){
        var amountOfLines = this.moocPath.studentTimeLinesActivities.length;//wijziging ik weet niet of ik deze al doorgestuurd had naar jou
        if(amountOfLines<=this.moocPath.studentTimeLinesActivities.length){
            for(var i = parseInt(Math.round(amountOfLines * (firstSlider/100))); i <= parseInt(Math.round(amountOfLines * (secondSlider/100))); i++){
                (function(i) {
                    setTimeout(function() {
                        $(".line"+i).show();
                        $(".activityAll"+i).show();
                    },100);
                })(i);

            }
            for(var i = 0; i <= parseInt(Math.round(amountOfLines * (firstSlider/100))); i++){
                (function(i) {
                    setTimeout(function() {
                        $(".line"+i).hide();
                        $(".activityAll"+i).hide();
                    },100);
                })(i);

            }
            for(var i = parseInt(Math.round(amountOfLines * (secondSlider/100))); i <= amountOfLines; i++){
                (function(i) {
                    setTimeout(function() {
                        $(".line"+i).hide();
                        $(".activityAll"+i).hide();
                    },100);
                })(i);

            }
        }

    },



    reScaleYAxis : function(){
        this.initialize("This is ");
        $(".box-title").html("Graphs representing who follows who in this course");
        this.update();
        this.updateMyData();
        for(var i = 0; i<this.moocPath.studentTimeLinesActivities.length;i++){
            this.addOtherStudent(this.moocPath.getNewStudentPathGraph(this.moocPath.studentTimeLinesActivities[i]),i);
        }
        this.hideLines($("#slider-range").slider("values",0),$("#slider-range").slider("values",1));

    },


});




window.SandboxView2 = Backbone.View.extend({
    tagName:  "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){

        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv =  options.csv;
        $(this.el).html(this.template({ title: this.title, csv:this.csv }));

        this.container = this.$('.box-body')[0];

        var margin = {top: 20, right: 20, bottom: 30, left: 50},
            width = 960 - margin.left - margin.right,
            height = 500 - margin.top - margin.bottom;

        var parseDate = d3.time.format("%d-%b-%y").parse;

        var x = this.x = d3.time.scale()
            .range([0, width]);

        var y = this.y = d3.scale.linear()
            .range([height, 0]);

        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");

        var line = this.line = d3.svg.line()
            .x(function(d) { return x(d.date); })
            .y(function(d) { return y(d.close); });

        var svg = this.svg = d3.select(this.container).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis);

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Dagen na startactiviteit");

    },
    render: function(){
        return this;
    },
    update: function(options){

        moocPath.updateStudentPath(studentId, path);
        var data = JSON.parse(options);

        data.forEach(function(d) {
            d.date = d.date;
            //d.date = parseDate(d.date);
            d.close = +d.close;
        });

        this.x.domain(d3.extent(data, function(d) { return d.date; }));
        this.y.domain(d3.extent(data, function(d) { return d.close; }));

        this.svg.append("path")
            .datum(data)
            .attr("class", "line")
            .attr("d", this.line);

    }
});



window.SandboxView3Followers = Backbone.View.extend({
    tagName:  "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){

        //_(this).bindAll('buildBase');

        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
        this.csv = options.csv;

        $(this.el).html(this.template({ title: this.title, csv:this.csv }));

        var w = window,
            d = document,
            e = d.documentElement,
            g = this.$('.box-body')[0],
            x = w.innerWidth || e.clientWidth || g.clientWidth,
            y = w.innerHeight|| e.clientHeight|| g.clientHeight;

        this.container = this.$('.box-body')[0];

        var width = 960,
            height = 500;

        this.force = d3.layout.force()
            .charge(-140)
            .linkDistance(80)
            .size([width, height]);

        this.svg = d3.select(this.container)
            .append("svg")
            .attr("width", width)
            .attr("height", height);

        var self = this;



        function updateWindow(){
            x =  g.clientWidth;
            y =  g.clientHeight;

            self.svg.attr("width", x).attr("height", y);
        }
        window.onresize = updateWindow;


    },
    render: function(){
        return this;
    },
    update: function(data){


        var self = this;

        var graph = this.dataAnalysis(data);

        var nodeMap = {};

        this.maxConnections = 1;
        graph.nodes.forEach(function(d) {
            nodeMap[d.name] = d;
            if (d.connections> self.maxConnections) self.maxConnections = d.connections;
        });

        graph.links.forEach(function(l) {
            l.source = nodeMap[l.source];
            l.target = nodeMap[l.target];
        });

        this.force.nodes(graph.nodes)
            .links(graph.links)
            .start();

        var link = this.svg.selectAll(".link")
            .data(graph.links)
            .enter().append("line")
            .attr("class", "link")
            ;

        var node = this.svg.selectAll(".node")
            .data(graph.nodes);

        var label = this.svg.selectAll(".label")
            .data(graph.nodes);

        var nodeEnter = node.enter().append("svg:circle")
            .attr("class", "node")
            .attr("r", function(d){

                return 3 + d.connections * (30.0/self.maxConnections);
            })
            .call(this.force.drag);

        node.append("title")
            .text(function(d) { return d.name; });

        //label = label.data(graph.nodes);
        //label.enter().append("text")
        //    .call(this.force.drag)
        //    .attr("y", function(d) {
        //        return d.y-40;
        //    })
        //    .attr("x", function(d) {
        //        return d.x+10;
        //    })
        //    .text(function(d){
        //        return d.text
        //    });

        this.force.on("tick", function(e) {
            link.attr("x1", function(d) { return d.source.x; })
                .attr("y1", function(d) { return d.source.y; })
                .attr("x2", function(d) { return d.target.x; })
                .attr("y2", function(d) { return d.target.y; });

            node.attr("cx", function(d) { return d.x; })
                .attr("cy", function(d) { return d.y; });

            //label.attr("x", function(d) { return d.x-20; })
            //    .attr("y", function(d) { return d.y-13; });

            var k = 10 * e.alpha;
            graph.nodes.forEach(function(o, i) {
                o.y += i & 1 ? k : -k;
                o.x += i & 2 ? k : -k;
            });
        }

        );

        self.svg.attr("width", "100%").attr("height", 600);
    },
    dataAnalysis: function(tripleObject){

        var nodes = [];
        var links = [];

        tripleObject.followers.forEach(function(response) {
            var item_nodes = {};
            //item_nodes ["id"] = response.id;
            item_nodes ["name"] = response.id;
            item_nodes ["text"] = response.name;
            item_nodes ["group"] = 1;
            item_nodes ["connections"] = response.follows.length;
            nodes.push(item_nodes);
            var _id = response.id
            response.follows.forEach(function(follow){
                var item_links = {};
                item_links["source"] = follow.id;
                item_links["target"] = _id;
                item_links["value"] = 3;
                links.push(item_links);
            });
        });

        var item2 = {
            "nodes": nodes,
            "links": links
        };

        return item2;
    }
});

// loader settings
var opts = {
    lines: 9, // The number of lines to draw
    length: 9, // The length of each line
    width: 5, // The line thickness
    radius: 14, // The radius of the inner circle
    color: '#EE3124', // #rgb or #rrggbb or array of colors
    speed: 1.9, // Rounds per second
    trail: 40, // Afterglow percentage
    className: 'spinner' // The CSS class to assign to the spinner
};

function init() {

    // trigger loader
    var spinner = new Spinner(opts).spin(target);

    // slow the json load intentionally, so we can see it every load
    setTimeout(function() {

        // load json data and trigger callback
        d3.json(chartConfig.data_url, function(data) {

            // stop spin.js loader
            spinner.stop();

            // instantiate chart within callback
            chart(data);

        });

    }, 1500);
}



function MOOCpath(){
    this.modelActivities ;//= getModelActivityPathFromJSON(); //Model as presented by the teacher
    this.studentActivities = [];
    this.studentTimeLinesActivities = [];  // the activities of all the users


    this.setStudentActivities = function(studentActivities){
        //var studentActivities = [];
        //$.ajax({
        //    type:"GET",
        //    dataType: "json",
        //    url: "LAD-Tools/StudentPath/newjson.json",
        //    //url: "https://2-dot-eco-xapi-production-server.appspot.com/data-proxy/query/result/studentPaths/eu.ecolearning.hub0:11/0",
        //    async:false,
        //    success: function(data){
        //        //studentActivities = data.studentActivities;
        //        studentActivities = data.rows;
        //    }
        //});
        for (var i = 0; i<studentActivities.length; i++){
            var studentActivity = {};

            studentActivity.objectId = studentActivities[i].c[0].v;
            studentActivity.objectDefinition = studentActivities[i].c[1].v;
            studentActivity.verbId = studentActivities[i].c[2].v;
            studentActivity.RelativeTime = studentActivities[i].c[3].v + "";
            this.studentActivities.push(studentActivity);
        }
    };


    this.setStudentTimeLinesActivities = function(newStudent){
        var studentActivities = [];
        var student = {};
        student.id = this.studentTimeLinesActivities.length;
        var studentActivities=[];;
        for(var i = 0; i < newStudent.length; i ++){
            var studentActivity = {};
            studentActivity.objectId = newStudent[i].c[0].v;
            studentActivity.objectDefinition = newStudent[i].c[1].v;
            studentActivity.verbId = newStudent[i].c[2].v;
            studentActivity.RelativeTime = newStudent[i].c[3].v + "";
            studentActivities.push(studentActivity);
        }
        student.timeLines = studentActivities;
        this.studentTimeLinesActivities.push(student);
    };

    this.getNewStudentPathGraph = function(student){
        var studentActivity = [];
        for(var i = 0; i < this.modelActivities.length; i++){
            var sar = {};
            sar.serial = this.modelActivities[i].serial;
            sar.objectDefinition = this.modelActivities[i].objectDefinition;
            sar.verbId = this.modelActivities[i].verbId;
            for(var j = 0; j < student.timeLines.length; j++){
                if(this.modelActivities[i].objectId === student.timeLines[j].objectId){
                    sar.relativeTime = parseInt(student.timeLines[j].RelativeTime);
                    sar.relativeTimeCont = parseInt(student.timeLines[j].RelativeTime);
                    break;
                } else {
                    sar.relativeTime = -1;
                    if(i > 0){
                        sar.relativeTimeCont = 0//studentActivity[i-1].relativeTimeCont;
                    }else{
                        sar.relativeTimeCont = 0;
                    }

                }

            }
            studentActivity.push(sar);
        }
        studentActivity.color = getRandomColor();
        return studentActivity;
    };


    /*
     * transforms the data so that the user can be drawn on the visualisation
     * @returns {Array}
     */
    this.getGraphStudentsActivitiesRelative = function(){
        var studentActivitiesRelative=[];
        for(var i = 0; i < this.modelActivities.length; i++){
            var sar = {};
            sar.serial = this.modelActivities[i].serial;
            sar.objectDefinition = this.modelActivities[i].objectDefinition;
            sar.verbId = this.modelActivities[i].verbId;
            for(var j = 0; j < this.studentActivities.length; j++){
                if(this.modelActivities[i].objectId === this.studentActivities[j].objectId){
                    sar.studentActivity = parseInt(this.studentActivities[j].RelativeTime);
                    sar.studentActivityCont = parseInt(this.studentActivities[j].RelativeTime);
                    break;
                }else {

                    sar.studentActivity = -1;
                    if(i>0){
                        sar.studentActivityCont = studentActivitiesRelative[i-1].studentActivityCont;
                    }
                    else{
                        sar.studentActivityCont = 0;
                    }


                }
            }
            studentActivitiesRelative[i] = sar;
        };
        return studentActivitiesRelative;
    };



    /*
     * Calculates the highest relative time
     * Ex. This can be used to make the Y-Axis in a d3graph
     * @returns {Number}highest value
     */
    this.getHightYAxis = function(){
        var highestValue = 0;
        for(var i = 0; i < this.modelActivities.length; i++){
            if(parseInt(this.modelActivities[i].relativeTime) > highestValue){
                highestValue = parseInt(this.modelActivities[i].relativeTime);
            }
        }
        /* console.log(this.modelActivities.length);
         for(var i = 0; i < this.studentActivities.length; i++){
         if(parseInt(this.studentActivities[i].RelativeTime) > highestValue){
         highestValue = parseInt(this.studentActivities[i].RelativeTime);
         }
         }
         console.log(this.studentActivities.length)
         for(var i = 0; i < this.studentTimeLinesActivities.length; i++){
         for(var j = 0; j < this.studentTimeLinesActivities[i].timeLines.length;j++){
         if(parseInt(this.studentTimeLinesActivities[i].timeLines[j].RelativeTime) > highestValue){
         highestValue = parseInt(this.studentTimeLinesActivities[i].timeLines[j].RelativeTime);
         }
         }
         }
         console.log(this.studentTimeLinesActivities.length); */
        return highestValue;
    };

};



//OTHER FUNCTIONS
/*
 * Generates a random color
 * @returns {String}
 */
function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}



//DATABASE CALLS

/*
 * Read the activityPath from a JSON file.
 */
function getModelActivityPathFromJSON(){
    var activityPath = [];
    $.ajax({
        type:"GET",
        dataType: "json",
        url: "LAD-Tools/StudentPath/activityPath.json",
        async:false,

        success: function(data){
            activityPath = data.activityPath;
        }
    });
    return activityPath;

}




function getColorIcon(relativeStudentTime){
    if(relativeStudentTime === -1){
        return "red";
    }
    else {
        return "#006633";
    }
}


/*
 * returns the icon code for drawing the graph icons
 */
function getCorrectFontAwesomeIcon(objectDefinition, verbId){
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/video"){
        return '\uf008';
    }
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/task" && verbId === "http://activitystrea.ms/schema/1.0/access"){
        return '\uf0ae';
    }
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/task" && verbId === "http://activitystrea.ms/schema/1.0/submit"){
        return '\uf1d9';
    }
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/article"){
        return '\uf0f6';
    }
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/forum" && (verbId === "http://activitystrea.ms/schema/1.0/author" || verbId === "http://activitystrea.ms/schema/1.0/comment") ){
        return '\uf0c0';
    }
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/assessment" && verbId === "http://activitystrea.ms/schema/1.0/access"){
        return '\uf14b';
    }
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/assessment" && verbId === "http://activitystrea.ms/schema/1.0/submit"){
        return '\uf1d8';
    }
    if(objectDefinition === "http://activitystrea.ms/schema/1.0/slide-deck" && verbId === "http://activitystrea.ms/schema/1.0/access"){
        return '\uf1c4';
    }
}




/*
 * Transforms the JSON in data that draws all the users on the graph
 * @returns {Array|MOOCpath.getAllStudentsPathGraph.studentTLActivities}

 this.getAllStudentsPathGraph = function(){
 var studentTLActivities=[];
 for(var h = 0; h < this.studentTimeLinesActivities.length;h++){
 var studentActivity = this.getNewStudentPathGraph(this.studentTimeLinesActivities[h]);
 studentTLActivities.push(studentActivity);
 }
 return studentTLActivities;
 };*/
/*
 * This is the function to add a row to the graphModel
 * @param {type} studentActivity:

 * @returns {undefined}

 this.addStudentTimeLineActivity = function(studentActivity){
 this.studentTimeLinesActivities.push(studentActivity);
 };
 */
/*
 * transforms the json in actorobject with each an array of activities
 * @returns {Array|MOOCpath.makeStudentTimeLines.studentList}
 */
// this.makeStudentTimeLines = function(){
// var studentList = [];
//GETTING UNIQUE STUDENTS FROM LIST
// if (this.studentTimeLinesActivities.length == 0) return 10;
// studentList.push({actorId:this.studentTimeLinesActivities[0].actorId,timeLines:[]});
// for(var i = 1; i<this.studentTimeLinesActivities.length; i++){
// var flag = false;
// for(var j = 0; j < studentList.length; j++){
// if(this.studentTimeLinesActivities[i].actorId === studentList[j].actorId){
// flag = true;
// }
// }
// if(flag === false){
// studentList.push({actorId: this.studentTimeLinesActivities[i].actorId,timeLines:[]});
// }
// }
// for(var i = 0; i < this.studentTimeLinesActivities.length; i++){
// for(var j = 0; j < studentList.length; j++){
// if(this.studentTimeLinesActivities[i].actorId === studentList[j].actorId){
// studentList[j].timeLines.push({objectId:this.studentTimeLinesActivities[i].objectId,objectDefinition:this.studentTimeLinesActivities[i].objectDefinition,verbId:this.studentTimeLinesActivities[i].verbId, relativeTime:this.studentTimeLinesActivities[i].RelativeTime});
// };
// }
// }
// return studentList;
// } ;




