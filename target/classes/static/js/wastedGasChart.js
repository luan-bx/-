var lineChartData = {
        labels : ["","","","","","",""],
        datasets : [
            {
                fillColor : "rgba(151,187,205,0.5)",
                strokeColor : "rgba(151,187,205,1)",
                pointColor : "rgba(151,187,205,1)",
                pointStrokeColor : "#fff",
                data : [28,48,40,19,96,27,100]
            }
        ]

    };
$(document).ready(function() { 
    new Chart(document.getElementById("line").getContext("2d")).Line(lineChartData);


});
/*废气图日期更新*/
function updateDate(){
    let startTime =document.getElementById("startTime").value;
    let endTime =document.getElementById("endTime").value;
    if(startTime==""){
        alert("请输入开始时间");
    }
    else if(endTime==""){
        alert("请输入结束时间");
    }
    else if(endTime<=startTime){
        alert("开始时间必须早于结束时间");
    }
    else{
        startTime = new Date(startTime);
        endTime = new Date(endTime);
        let diff = dateDifference(startTime,endTime);
        if(diff>30){
            alert("开始和结束日期相差不得大于30天")
        }
        else{
            let label = new Array();
            let data = new Array();
            document.getElementById("line").width=diff*40+100;
            for(;startTime<=endTime;nextDay(startTime)){
                label.push(startTime.toLocaleDateString().replace(startTime.getFullYear().toString()+"/",""));
                data.push(Math.floor(Math.random()*10+90));
            }
            lineChartData.labels=label;
            lineChartData.datasets[0].data=data;
            if(lineChartData.datasets.length==2){
                let thData = new Array(diff+1);
                for(var i=0;i<label.length;i++)
                {
                    let threshold = lineChartData.datasets[1].data[0]
                    thData[i]=threshold;
                }
                lineChartData.datasets[1].data=thData;
            }
            new Chart(document.getElementById("line").getContext("2d")).Line(lineChartData);
        }
    }
    
}
/*废气图阈值更新*/
function updateThreshold(){
    var th = document.getElementById("threshold").value;
    thData = new Array(lineChartData.labels.length);
    for(var i=0;i<lineChartData.labels.length;i++){
        thData[i]=th;
    }
    if(lineChartData.datasets.length==1){
        lineChartData.datasets.push({
                fillColor : "rgba(220,220,220,0.5)",
                strokeColor : "rgba(220,220,220,1)",
                pointColor : "rgba(220,220,220,1)",
                pointStrokeColor : "#fff",
                data : thData
            })
    }
    else{
        lineChartData.datasets[1].data=thData;
    }
    new Chart(document.getElementById("line").getContext("2d")).Line(lineChartData);
}
/*日期型对象date进入下一日*/
function nextDay(date){
    /*读取年月日*/
    let year = date.getFullYear();
    let month = date.getMonth();
    let day = date.getDate();
    /*把大月放进数组*/
    let big = [0,2,4,6,7,9,11];
    /*先判断是否是2月*/
    if(month!=2){
        /*不是二月*/
        /*不到30号，日期直接加一*/
        if(day<30){
            date.setDate(day+1);
        }
        /*若是30号，判断是否是大月*/
        else if(day==30){
            /*若是大月，加到31号*/
            if(big.indexOf(month)!=-1){
                date.setDate(day+1);
            }
            /*不是大月，进入下个月*/
            else{
                date.setDate(1);;
                nextMonth(date);
            }
        }
        /*31号*/
        else{
            date.setDate(1);
            nextMonth(date);
        }
    }
    /*二月*/
    else{
        if(day==28){
            /*闰年判断*/
            if(year%4==0){
                date.setDate(day+1);
            }
            else{
                date.setDate(1);
                nextMonth(date);
            }
        }
        else if(day==29){
            date.setDate(1);
            nextMonth(date);
        }
        else{
            date.setDate(day+1);
        }
    }
}
/*日期型对象date进入下一个月*/
function nextMonth(date){
    let year = date.getFullYear();
    let month = date.getMonth();
    year = month==12?year+1:year;
    month = (month+1)%12;
    date.setFullYear(year);
    date.setMonth(month);
}

/*计算endTime和startTime差了多少天*/
function dateDifference(startTime, endTime){
    let start = Date.UTC(startTime.getFullYear(),startTime.getMonth(),startTime.getDate());
    let end = Date.UTC(endTime.getFullYear(),endTime.getMonth(),endTime.getDate());
    return (end-start)/1000/60/60/24;
}