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

window.CalendarView = Backbone.View.extend({
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
            chartType: 'Calendar',
            dataTable: this.data,
            backgroundColor: { fill:'#76a7fa' },
            options: {
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

window.SandboxView1 = Backbone.View.extend({
    tagName:  "section",
    className: "col-lg-12 connectedSortable ui-sortable",
    initialize: function(options){

        this.template = _.template(tpl.get('widget'));
        this.title = options.title;

        $(this.el).html(this.template({ title: this.title }));

        var margin = {top: 50, bottom: 50, left:150, right: 40};
        if($( window ).width() < 760){
            var width = $( window ).width()- 50 - margin.left - margin.right;
        }else{
            var width = $( window ).width() - 300 - margin.left - margin.right;
        }
        var height = 450 - margin.top - margin.bottom;

        var xScale = d3.scale.linear().range([0, width]);
        var yScale = d3.scale.ordinal().rangeRoundBands([0, height], 1.8,0);

        var numTicks = 5;
        var xAxis = d3.svg.axis().scale(xScale)
            .orient("top")
            .tickSize((-height))
            .ticks(numTicks);

        this.container = this.$('.box-body')[0];

        var svg = d3.select(this.container).append("svg")
            .attr("width", width+margin.left+margin.right)
            .attr("height", height+margin.top+margin.bottom)
            .attr("class", "base-svg");

        var barSvg = svg.append("g")
            .attr("transform", "translate("+margin.left+","+margin.top+")")
            .attr("class", "bar-svg");

        var x = barSvg.append("g").attr("class", "x-axis");

        var data = JSON.parse(this.model);

        var xMax = d3.max(data, function(d) { return d.rate; } );
        var xMin = 0;


        xScale.domain([xMin, xMax]);
        yScale.domain(data.map(function(d) { return d.country; }));

        d3.select(".base-svg").append("text")
            .attr("x", margin.left)
            .attr("y", (margin.top)/2)
            .attr("text-anchor", "start")
            .text("Narrowly defined unemployment rates: top 20 countries (2010)")
            .attr("class", "title");

        var groups = barSvg.append("g")
            .attr("class", "labels")
            .selectAll("text")
            .data(data)
            .enter()
            .append("g");

        groups.append("text")
            .attr("x", "0")
            .attr("y", function(d) { return yScale(d.country); })
            .text(function(d) { return d.country; })
            .attr("text-anchor", "end")
            .attr("dy", ".9em")
            .attr("dx", "-.32em")
            .attr("id", function(d,i) { return "label"+i; });

        var bars = groups
            .attr("class", "bars")
            .append("rect")
            .attr("width", function(d) { return xScale(d.rate); })
            .attr("height", height/20)
            .attr("x", xScale(xMin))
            .attr("y", function(d) { return yScale(d.country); })
            .attr("id", function(d,i) { return "bar"+i; });

        groups.append("text")
            .attr("x", function(d) { return xScale(d.rate); })
            .attr("y", function(d) { return yScale(d.country); })
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

        x.call(xAxis);
        var grid = xScale.ticks(numTicks);
        barSvg.append("g").attr("class", "grid")
            .selectAll("line")
            .data(grid, function(d) { return d; })
            .enter().append("line")
            .attr("y1", 0)
            .attr("y2", height+margin.bottom)
            .attr("x1", function(d) { return xScale(d); })
            .attr("x2", function(d) { return xScale(d); })
            .attr("stroke", "white");


    },
    render: function(){
        console.log(this.template)
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

        var margin = {top: 20, right: 20, bottom: 30, left: 50},
            width = 960 - margin.left - margin.right,
            height = 500 - margin.top - margin.bottom;

        var parseDate = d3.time.format("%d-%b-%y").parse;

        var x = d3.time.scale()
            .range([0, width]);

        var y = d3.scale.linear()
            .range([height, 0]);

        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");

        var line = d3.svg.line()
            .x(function(d) { return x(d.date); })
            .y(function(d) { return y(d.close); });

        this.container = this.$('.box-body')[0];

        var svg = d3.select(this.container).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var data = JSON.parse(this.model);

        data.forEach(function(d) {
            d.date = d.date;
            //d.date = parseDate(d.date);
            d.close = +d.close;
        });

        x.domain(d3.extent(data, function(d) { return d.date; }));
        y.domain(d3.extent(data, function(d) { return d.close; }));

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

        svg.append("path")
            .datum(data)
            .attr("class", "line")
            .attr("d", line);


    },
    render: function(){
        console.log(this.template)
        return this;
    }
});
