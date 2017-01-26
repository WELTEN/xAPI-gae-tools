
window.TeacherStudentActivityView = Backbone.View.extend({
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
    width:null,
    interactivityClustering:null,



    initialize: function(options){
        this.template = _.template(tpl.get('widget'));
        this.title = options.title;

        this.csv =options.csv;
        $(this.el).html(this.template({ title: this.title , csv:this.csv}));
        this.container = this.$('.box-body')[0];

        $(this.container).html("");


        this.margin = {top: 25, right: 20, bottom: 40, left: 40};
        this.width = $('.content').width() - this.margin.left - this.margin.right;
        this.height = 300 - this.margin.top - this.margin.bottom;


        x = d3.scale.ordinal()
            .rangeBands([0, this.width], .1);

        y = d3.scale.linear()
            .range([this.height, 0]);


        this.xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom")
            .ticks(100,".");

        this.yAxis = d3.svg.axis()
            .scale(y)
            .orient("left")
            .ticks(15, ".");


        this.xatt = x;
        this.yatt = y;


        svg = d3.select(this.container).append("svg")
            .attr("width", this.width + this.margin.left + this.margin.right)
            .attr("height", this.height + this.margin.top + this.margin.bottom)
            .append("g")
            .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

        svg = d3.select(this.container).append("svg")
            .attr("viewBox", "0 0 "+(this.width + this.margin.left + this.margin.right)+" "+(this.height + this.margin.top + this.margin.bottom))
            .classed("svg-container", true)
            .classed("svg-content-responsive", true)
            .append("g")
            .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

        $('input[name=clusteringRadioButton][value='+localStorage.getItem("clusterType")+']').prop("checked",true);





    },



    update: function(options){
        if(this.interactivityClustering === null){
            this.interactivityClustering = new InteractivityClustering();
            this.interactivityClustering.transform(options.rows);//create object with students with the right variabels names
        }
        var x = this.xatt;
        var y = this.yatt;
        data = this.interactivityClustering.interactivityData;
        x.domain(data.map(function(d) { return d.actorId; }));
        y.domain([0, d3.max(data, function(d) { return d.interactivityLevel; })]);
        this.xAxis.tickValues(d3.range(0, this.interactivityClustering.interactivityData.length, 30));

        svg.append("g")
            .attr("class", "xAxis")
            .attr("id","IandCxaxis")
            .attr("transform", "translate(0," + this.height + ")")
            .call(this.xAxis)
            .append("text")
            .attr("x",this.width)
            .attr("y",30)
            .style("text-anchor","end")
            .text("Student Id");

        svg.append("g")
            .attr("class", "yAxis")
            .attr("id","IandCyaxis")
            .call(this.yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 2)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Level of interactivity");

        h = this.height;
        svg.selectAll(".bar")
            .data(data)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("id",function(d){return d.actorId;})
            .attr("alt",function(d){return Math.round(d.interactivityLevel*1000)/1000;})
            .attr("x", function(d) { return x(d.actorId); })
            .attr("width", x.rangeBand())
            .attr("y", function(d) { return y(d.interactivityLevel); })
            .attr("height", function(d) { return h - y(parseInt(d.interactivityLevel)); })
            .on("mouseover", function(d) {  $("#IenCInfo").html('I-Level: ' + Math.round(d.interactivityLevel*100)/100);});

        this.makeClusters(this.interactivityClustering.getClusterLimits(parseInt(localStorage.getItem("clusterType"))));
        this.interactivityClustering.makeClusterInformationBoxes();



    },
    //MAKE LINE OF USER
    makeUserInteractivityLevelLine: function(UIL){
        var h = this.height;
        var u = 0;

        setTimeout(function(){
            $(".bar").each(function(p){
                if($(this).attr("alt") === UIL+""){
                    u = $(this).attr("x");
                }
              }
                );

            svg.append("line")
                .attr("class","userInteractivityLevel")
                .attr("x1",u)
                .attr("y1",0)
                .attr("x2",u)
                .attr("y2",h)
                .attr("stroke-width", 4)
                .attr("stroke", "red");
            svg.append("text")
                .style("fill", "red")
                .style("text-anchor", "middle")
                .attr("x",u)
                .attr("y",-5)
                .text("YOU");
        }, 1000);
    },
    //MAKE CLUSTERS

    changeCluster: function(){

    },

    makeClusters: function(clusterLimits){
        if(clusterLimits != null){
            var xValuesClusterLimits = this.interactivityClustering.get_X_ValuesForClusterLimits(clusterLimits);
            var clusterRanges = this.interactivityClustering.makeClusterRanges(xValuesClusterLimits);
            var defs = svg.append("defs");

            defs.append("marker")
                .attr({
                    "id":"arrow",
                    "viewBox": "0 -5 10 10",
                    "refX":5,
                    "refY":0,
                    "markerWidth":4,
                    "markerHeight":4,
                    "orient":"auto"})
                .append("path")
                .attr("d", "M0,-5L10,0L0,5")
                .attr("class","arrowHead");

            defs.append("marker")
                .attr({
                    "id":"arrowBegin",
                    "viewBox": "0 -5 10 10",
                    "refX":5,
                    "refY":0,
                    "markerWidth":4,
                    "markerHeight":4,
                    "orient":"auto"})
                .append("path")
                .attr("d", "M0,0L10,-5L10,5L0,0")
                .attr("class","arrowHead");


            svg.selectAll(".clusterLimit")
                .data(xValuesClusterLimits)
                .enter().append("line")
                .attr("class", "clusterLimit")
                .attr("x1",function(d){return  d.clusterlimit;})
                .attr("y1",0)
                .attr("x2",function(d){return d.clusterlimit;})
                .attr("y2",this.height)
                .attr("stroke-width", 2)
                .attr("stroke", "green");

            svg.selectAll(".clusterRange")
                .data(clusterRanges)
                .enter().append("line")
                .attr("class","arrow clusterRange")
                .attr("marker-end","url(#arrow)")
                .attr("marker-start","url(#arrowBegin)")
                .attr("x1",function(d){return d.begin;})
                .attr("y1",60)
                .attr("x2",function(d){return d.end;})
                .attr("y2",60)
                .attr("stroke-width", 2)
                .attr("stroke", "black");

            if(clusterRanges.length > 2 && clusterRanges[2].name !== "MEDIAN Q2"){
                svg.selectAll("clusterRangeText")
                    .data(clusterRanges)
                    .enter().append("text")
                    .attr("class","clusterRangeText")
                    .attr("x",function(d){return d.end+30;})
                    .attr("y",-5)
                    .attr("dy", ".2em")
                    .style("text-anchor", "end")
                    .style("fill", "green")
                    .text(function(d){return d.name;});
            }
            else{
                svg.selectAll("clusterRangeText")
                    .data(clusterRanges)
                    .enter().append("text")
                    .attr("class","clusterRangeText")
                    .attr("x",0)
                    .attr("y",function(d){return d.end;})
                    .attr("transform", "rotate(-90)")
                    .attr("dy", ".2em")
                    .style("text-anchor", "end")
                    .style("fill", "green")
                    .text(function(d){return d.name;});
            }

        }
    }

});






function InteractivityClustering(){
//ATTRIBUTES
    this.interactivityData = [];
    this.clusters;


//METHODS

    this.transform = function(json){

        for(var i = 0; i < json.length; i++){
            var student = {};
            student.actorId = json[i].c[0].v;
            student.interactivityLevel = parseInt(json[i].c[1].v);
            this.interactivityData.push(student);
        }
    },

        this.get_X_ValuesForClusterLimits = function(clusterLimits){
            var X_Values = [];
            for(var i = 0; i < clusterLimits.length; i++){
                $("body").find(".bar").each(function(){
                    if(parseFloat($(this).attr("alt"))< clusterLimits[i].clusterlimit){
                        X_Values[i] = {clusterName: clusterLimits[i].clusterid, clusterlimit: parseInt($(this).attr("x"))+ parseInt($(this).attr("width"))};
                    }
                });
            }
            return X_Values;
        },

        this.makeMedianClusterLimits = function(){
            var clusterLimits = [];
            var q1;
            var q2;
            var q3;
            q1 ={clusterid:"Quartile 1",clusterlimit:this.interactivityData[parseInt(Math.round(this.interactivityData.length*0.25))+1].interactivityLevel};
            q2 = {clusterid:"MEDIAN Q2",clusterlimit:this.interactivityData[parseInt(Math.round(this.interactivityData.length*0.50))+1].interactivityLevel};
            q3 = {clusterid:"Quartile 3",clusterlimit:this.interactivityData[parseInt(Math.round(this.interactivityData.length*0.75))+1].interactivityLevel};
            clusterLimits = [q1,q2,q3];
            return clusterLimits;
        },




        this.makeKClusterLimits = function(){
            var clusterLimits = [];
            var centroids = [];
            var clusterData = this.interactivityData;
            var kInputValues = [];
            for(var i = 0; i<clusterData.length;i++){
                kInputValues[i]=clusterData[i].interactivityLevel;
            }
            centroids = KClusterAlgoritm(3,kInputValues);


            centroids.centroids.sort(function(a, b){return a-b;});

            for(var i = 0; i<centroids.centroids.length; i++){
                clusterLimits[i] = {clusterid:"Cluster"+(i+1),clusterlimit:centroids.centroids[i]};
            }
            return clusterLimits;
        },


        this.getClusterLimits = function(clusterId){
            switch(clusterId){
                case 0:
                    return  this.makeMedianClusterLimits();
                    break;
                case 1:
                    return  this.makeKClusterLimits();
                    break;
                default:
                    return  this.makeMedianClusterLimits();
            }
        },



        this.makeClusterRanges = function(clusterLimits){
            var clusterRanges = [];
            if(clusterLimits.length > 0){
                clusterRanges[0] = {begin:0+1,end:clusterLimits[0].clusterlimit-3,name:clusterLimits[0].clusterName};

                for(var i = 0; i<clusterLimits.length-1; i++){
                    clusterRanges[i+1] = {begin : clusterLimits[i].clusterlimit+3 , end : clusterLimits[i+1].clusterlimit-3, name:clusterLimits[i+1].clusterName};

                }

                clusterRanges[clusterLimits.length] = {begin : clusterLimits[clusterLimits.length-1].clusterlimit+3, end : $("body").find(".bar").last().attr("x"), name:"Cluster "+(clusterLimits.length+1)};
                if(clusterRanges.length > 2 && clusterRanges[2].name !== "MEDIAN Q2"){
                    clusterRanges[clusterLimits.length].name="";
                }
            };
            return clusterRanges;
        };


    this.makeClusterInformationBoxes = function(){
        switch(parseInt(localStorage.getItem("clusterType"))){
            case 0:
                $("#clusterInformationTitle").html("MEDIAN");
                $("#clusterInformationContent").html("The median(Me and second green line) is the centre of a distribution when the the data is ranked in orde of magnitude. The first and second line on the graph are giving an idea about the distrubution.");
                $("#clusterInformationLink").html('<a href="http://blog.surveymethods.com/when-is-it-generally-better-to-use-median-over-mean/" target="_blank">Link for more information about median and quartiles</a>');
                break;
            case 1:
                $("#clusterInformationTitle").html("K-CLUSTERING AI");
                $("#clusterInformationContent").html("This clustering algoritm autonoumesly will create a given  amount of clusters (3)");
                $("#clusterInformationLink").html('<a href="http://nl.mathworks.com/help/stats/kmeans.html?requestedDomain=www.mathworks.com" target="_blank")>Link for more information about k-means clustering on mathworks.com</a>');
                break;
            case 2:
                $("#clusterInformationTitle").html("STANDARD DEVIATION");
                $("#clusterInformationContent").html("This visualisation does not give actual information. It is just meant as a test for the visualisation of this tool");
                break;
            default:
                $("#clusterInformationTitle").html("No Clustering Selected");
                $("#clusterInformationContent").html("");
                break;
        }
    };

};

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * amountOfClusters: Amount of clusters that have to be made
 * values: a array of values that are to be clustered
 * return a 2D array of clusters with their values
 */


function KClusterAlgoritm(amountOfClusters, values){
    var KClusters = initClusters(amountOfClusters, values);
    var newCentroids = calculateNewCentroids(KClusters.clusters, amountOfClusters);
    var oldCentroids = KClusters.centroids;
    var chCluster = checkClusters(newCentroids,oldCentroids);
    var teller = 0;
    while(!chCluster && teller <= 3000){

        console.log(teller);
        var newKClusters = makeNewClusters(newCentroids,values);

        oldCentroids = newCentroids;

        newCentroids = calculateNewCentroids(newKClusters.clusters, amountOfClusters);

        chCluster = checkClusters(newCentroids,oldCentroids);
        teller++;
    }
    if(teller === 3000){
        KClusterAlgoritm(amountOfClusters,values);
    }
    else{
        KClusters.centroids = newCentroids;
        return KClusters;
    }

}

/*
 * return true if both arrays contains clusters with the same values
 */
function checkClusters(newCentroids,oldCentroids){
    var flag = true;
    for(var i = 0; i < newCentroids.length; i++){
        if(newCentroids[i] !== oldCentroids[i]){
            flag = false;
        }
    }
    return flag;
}

function makeNewClusters(cc, values){
    //create a 2D array
    var clusters = [];
    for(var i = 0; i < cc.length; i++){
        clusters[i] = new Array();
    }
    for( var i = 0; i < values.length; i++){
        var closestCentroids = cc[0];
        var KCluster={};
        var index = 0;
        for(var j = 0; j<cc.length; j++){
            if(Math.abs(cc[j]-values[i])< Math.abs(closestCentroids-values[i])){
                closestCentroids = cc[j];
                index = j;
            }
        }

        clusters[index].push(values[i]);
    }

    KCluster.centroids = cc;
    KCluster.clusters = clusters;
    return KCluster;
}


function calculateNewCentroids(clusters, amountOfClusters){
    var centroids = [];
    for( var i =0; i < amountOfClusters; i++){
        var sum = 0;
        for(var j = 0; j < clusters[i].length; j++){
            sum = sum + clusters[i][j];
        }
        centroids[i]= parseInt(Math.round(sum / clusters[i].length));
    }
    return centroids;
}


function initClusters(amountOfClusters, values){
    //Create Centroids
    var cc = createRandomCentroids(amountOfClusters, values);
    //create a 2D array
    var clusters = [];
    for(var i = 0; i < amountOfClusters; i++){
        clusters[i] = new Array();
    }
    //2D with centroids as cols with values assigned
    //if value between two centroids it is added to the highest one.
    for( var i = 0; i < values.length; i++){
        var closestCentroids = cc[0];
        var KCluster={};
        var index = 0;
        for(var j = 0; j<amountOfClusters; j++){
            if(Math.abs(cc[j]-values[i])< Math.abs(closestCentroids-values[i])){
                closestCentroids = cc[j];
                index = j;
            }
        }

        clusters[index].push(values[i]);
    }
    KCluster.centroids = cc;
    KCluster.clusters = clusters;
    return KCluster;
}

/*
 *
 * @param {type} amountOfClusters
 * @param {type} values
 * @returns {Array}of different values
 */
function createRandomCentroids(amountOfClusters, values){
    var cc = [];
    var i = 0;
    while(i<amountOfClusters){
        var t = parseInt((Math.random() * getMaximumOfValues(values))+ getMinimumOfValues(values));
        if(cc === null || !containing(cc,t)){
            cc[i] =  t;
            i++;
        }
    }
    return cc;
}

function containing(row, t){
    var flag = false;
    if(row!==null){
        for(var i = 0; i < row.length; i++){
            if(row[i]=== t){
                flag = true;
            }
        }
    }
    return flag;
}

/*
 * return the minimum value of an array of integers
 */
function getMinimumOfValues(values){
    var min = values[0];
    for(var i = 0 ; i<values.length; i++){
        if (values[i] < min) {
            min = values[i];
        }
    }
    return min;
}

/*
 * return the maximum of an array of integers
 */
function getMaximumOfValues(values){
    var max = values[0];
    for(var i = 0 ; i < values.length; i++){
        if (values[i] > max) {
            max = values[i];
        }
    }
    return max;
}


function calculateCentroids(){

}