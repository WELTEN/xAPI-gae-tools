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
    initialize: function(){
        this.template = _.template(tpl.get('widget'));
        console.log(this.model);
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);

        $(this.el).html(this.template({ title: "Calendar" }));

        var chart = new Backbone.GoogleChart({
            chartType: 'Calendar',
            dataTable: this.data,
            backgroundColor: { fill:'red' },
            options: {'title': 'agenda'}
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
    initialize: function(){
        this.template = _.template(tpl.get('widget'));
        console.log(this.model);
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);

        $(this.el).html(this.template({ title: "Bubble" }));

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

window.PerfomanceView = Backbone.View.extend({
    tagName: "section",
    className: "col-lg-5 connectedSortable ui-sortable",
    initialize: function(){
        this.template = _.template(tpl.get('widget'));
        console.log(this.model);
    },
    drawVisualization: function () {
        this.data = new google.visualization.DataTable(this.model);

        $(this.el).html(this.template({ title: "Perfomance" }));

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
