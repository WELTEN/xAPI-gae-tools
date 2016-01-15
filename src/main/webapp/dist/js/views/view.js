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
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title }));
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

window.ColumnChartView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title }));
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
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title }));
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
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title }));
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
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title }));
        var chart = new Backbone.GoogleChart({
            chartType: 'BubbleChart',
            dataTable: this.model,
            options: {
                title: 'DropOutMonitor: Correlation between number of course launches, number of activities and number of users for all Moocs',
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

window.ResourcesConsumedView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-10 connectedSortable ui-sortable",
    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title }));
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
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);
        $(this.el).html(this.template({ title: this.title }));
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

        $(this.el).html(this.template({ title: this.title }));
        this.container = this.$('.box-body')[0];

        $(this.container).html("");

        this.margin = {top: 50, bottom: 50, left:150, right: 40};
        if($( window ).width() < 760){
            var width = $( window ).width()- 50 - this.margin.left - this.margin.right;
        }else{
            var width = $( window ).width() - 300 - this.margin.left - this.margin.right;
        }
        this.height = 450 - this.margin.top - this.margin.bottom;

        this.xScale = d3.scale.linear().range([0, width]);
        this.yScale = d3.scale.ordinal().rangeRoundBands([0, this.height], 1.8,0);



        var numTicks = 5;
        var xAxis = d3.svg.axis().scale(this.xScale)
            .orient("top")
            .tickSize((-this.height))
            .ticks(numTicks);

        var svg = d3.select(this.container).append("svg")
            .attr("width", width+this.margin.left+this.margin.right)
            .attr("height", this.height+this.margin.top+this.margin.bottom)
            .attr("class", "base-svg");

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


window.SandboxView2 = Backbone.View.extend({
    tagName:  "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){

        this.template = _.template(tpl.get('widget'));
        this.title = options.title;

        $(this.el).html(this.template({ title: this.title }));

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