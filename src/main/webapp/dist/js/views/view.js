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

///////////
// Calendar
///////////
//window.CalendarCourseView = Backbone.View.extend({
//    el: $("#calendar_basic"),
//    initialize:function () {
//        //this.template = _.template(tpl.get('user_sidebar'));
//        console.log(this.model);
//
//
//        var data = new google.visualization.DataTable(this.model);
//        //var chart = new google.visualization.Calendar(document.getElementById('calendar_basic'));
//        //chart.draw(data);
//
//    },
//
//    render:function () {
//        $(this.el).html(this.model);
//        return this;
//    }
//});


window.CalendarCourseView = new Backbone.GoogleChart({
    beforeDraw: function( chart, options) {
        console.log(this.model, chart, options);
    },
    chartType: 'ColumnChart',
    dataTable: [['Germany', 'USA', 'Brazil', 'Canada', 'France', 'RU'],
        [700, 300, 400, 500, 600, 800]],
    options: {'title': 'Countries'},

    render:function () {
        $(this.el).html(this.model);
        return this;
    }
});
