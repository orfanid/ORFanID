$(document).ready(function() {
   $('select').formSelect();

    let organismData = {};
    $.getJSON('assets/data/organism_list.json', function(data) {
        $.each( data, function( key, val ) {
            organismData[key] =  val ;
        });
    }).done(function() {
        let organismElement = $('#organismName');
        organismElement.autocomplete({
            data: organismData
        });
    });

    function unixTimestampToDate(UNIX_timestamp){
        var a = new Date(UNIX_timestamp * 1000);
        var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        var year = a.getFullYear();
        var month = months[a.getMonth()];
        var date = a.getDate();
        var time = date + ' ' + month + ' ' + year;
        return time;
    }
    $.ajax({
        type: "POST",
        contentType: 'application/json',
        dataType: "text",
        url: "/orfanbase-genes",
        success: function (results) {
            var genes = JSON.parse(results);
            genes.forEach(function (result) {
                // Converting UNIX timestamp to date
                var timestamp = result.analysisDate;
                result.analysisDate = unixTimestampToDate(timestamp/1000);
            });
            var table = $('#GenesViewTable').DataTable({
                "data":genes,
                "columns": [
                    {"data" : "analysisDate"},
                    {"data" : "organism"},
                    {"data" : "geneId"},
                    {"data" : "description"},
                    {"data" : "orfanLevel"},
                    {"data" : "analysisId"},
                    {"data" : "view"}
                ],
                "oLanguage": {
                    "sStripClasses": "",
                    "sSearch": "",
                    "sSearchPlaceholder": "Enter Search Term Here",
                    "sLengthMenu": '<span>Rows per page:</span>'+
                        '<select class="browser-default">' +
                        '<option value="5">5</option>' +
                        '<option value="10">10</option>' +
                        '<option value="20">20</option>' +
                        '<option value="50">50</option>' +
                        '<option value="100">100</option>' +
                        '<option value="-1">All</option>' +
                        '</select></div>'
                },
                dom: 'frt',
                "aaSorting": [],
                "columnDefs": [
                    {
                    "targets": 5,
                    "visible": false
                    },
                    {
                    "targets": -1,
                    "data": null,
                    "defaultContent": "<button class=\"btn btn-small\"><i class=\"large material-icons\">insert_chart</i></button>"
                } ]
            });

            $('#GenesViewTable tbody').on( 'click', 'button', function () {
                var data = table.row( $(this).parents('tr') ).data();
                var sessionid = data.analysisId;
                window.location.href = "/result?sessionid=" + sessionid;
            });
        },
        error: function (error) {
            console.log("Error occurred while fetching results: "+ error);
        }
    });



    var BioCircosGenome = [
        ["1" , 249250621],
        ["2" , 243199373],
        ["3" , 198022430],
        ["4" , 191154276],
        ["5" , 180915260],
        ["6" , 171115067],
        ["7" , 159138663],
        ["8" , 146364022],
        ["9" , 141213431],
        ["10" , 135534747],
        ["11" , 135006516],
        ["12" , 133851895],
        ["13" , 115169878],
        ["14" , 107349540],
        ["15" , 102531392],
        ["16" , 90354753],
        ["17" , 81195210],
        ["18" , 78077248],
        ["19" , 59128983],
        ["20" , 63025520],
        ["21" , 48129895],
        ["22" , 51304566],
        ["X" , 155270560],
        ["Y" , 59373566]
    ];
    BioCircos01 = new BioCircos(SCATTER01,SCATTER02,SCATTER03,BioCircosGenome,{
        target : "biocircos",
        svgWidth : 600,
        svgHeight : 600,
        chrPad : 0.04,
        innerRadius: 246,
        outerRadius: 270,
        zoom : true,
        genomeFillColor: ["#FFFFCC", "#CCFFFF", "#FFCCCC", "#CCCC99","#0099CC", "#996699", "#336699", "#FFCC33","#66CC00"],
        SCATTERMouseOverDisplay : true,
        SCATTERMouseOverTooltipsHtml01 : "chr : ",
        SCATTERMouseOverTooltipsHtml02 : "<br>start : ",
        SCATTERMouseOverTooltipsHtml03 : "<br>end : ",
        SCATTERMouseOverTooltipsHtml04 : "<br>name : ",
        SCATTERMouseOverTooltipsHtml05 : "<br>cancer : ",
        SCATTERMouseOverTooltipsHtml06 : "",
        SCATTERMouseOverTooltipsPosition : "absolute",
        SCATTERMouseOverTooltipsBackgroundColor : "#D1EEEE",
        SCATTERMouseOverTooltipsBorderStyle : "solid",
        SCATTERMouseOverTooltipsBorderWidth : 0,
        SCATTERMouseOverTooltipsPadding : "3px",
        SCATTERMouseOverTooltipsBorderRadius : "5px",
        SCATTERMouseOverTooltipsOpacity : 0.7,
        SCATTERMouseOutDisplay : true,
        SCATTERMouseOutAnimationTime : 800,
        SCATTERMouseOutColor : "none",
        SCATTERMouseOutCircleSize : "none",
        SCATTERMouseOutCircleStrokeWidth : 0,
    });
    BioCircos01.draw_genome(BioCircos01.genomeLength);

    // based on prepared DOM, initialize echarts instance
    var myChart = echarts.init(document.getElementById('main'));

    setTimeout(function () {
        option = {
            legend: {},
            tooltip: {
                trigger: 'axis',
                showContent: false
            },
            dataset: {
                source: [
                    ['product', '2020-04', '2020-05', '2020-06', '2020-07', '2020-08', '2020-09'],
                    ['Strict ORFan', 41.1, 30.4, 65.1, 53.3, 83.8, 98.7],
                    ['domain restricted gene', 86.5, 92.1, 85.7, 83.1, 73.4, 55.1],
                    ['kingdom restricted gene', 24.1, 67.2, 79.5, 86.4, 65.2, 82.5],
                    ['phylum restricted gene', 55.2, 67.1, 69.2, 72.4, 53.9, 39.1]
                ]
            },
            xAxis: {type: 'category'},
            yAxis: {gridIndex: 0},
            grid: {top: '55%'},
            series: [
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {type: 'line', smooth: true, seriesLayoutBy: 'row'},
                {
                    type: 'pie',
                    id: 'pie',
                    radius: '30%',
                    center: ['50%', '25%'],
                    label: {
                        formatter: '{b}: {@2012} ({d}%)'
                    },
                    encode: {
                        itemName: 'product',
                        value: '2012',
                        tooltip: '2012'
                    }
                }
            ]
        };

        myChart.on('updateAxisPointer', function (event) {
            var xAxisInfo = event.axesInfo[0];
            if (xAxisInfo) {
                var dimension = xAxisInfo.value + 1;
                myChart.setOption({
                    series: {
                        id: 'pie',
                        label: {
                            formatter: '{b}: {@[' + dimension + ']} ({d}%)'
                        },
                        encode: {
                            value: dimension,
                            tooltip: dimension
                        }
                    }
                });
            }
        });

        myChart.setOption(option);
    });
});