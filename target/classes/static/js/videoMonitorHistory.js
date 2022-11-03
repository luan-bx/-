/*厂区类，name属性是厂区名称，workshop属性是一个数组，存储该厂区所有的车间*/
class factory {
    constructor(name, workshop){
        this.name = name;
        this.workshop = workshop;
    }
}
/*车间类，name属性是车间名称，monitor属性是一个数组，存储该车间所有的监控点名称*/
class workshop {
    constructor(name, monitor){
        this.name = name;
        this.monitor = monitor;
    }
}
var workshop1 = new workshop("1月",["1日","2日"]);
var workshop2 = new workshop("12月",["30日","31日"]);
var factory1 = new factory("2021年", [workshop1]);
var factory2 = new factory("2022年", [workshop2]);
var allFactory = [factory1, factory2];
var li = ["<li><a data-toggle=\"tab\" href=\"#","\"><i class=\"icon-home\"></i>","</a></li>"];
var di = ["<div id=\"","\" class=\"tab-pane \"><div class=\"panel panel-success\"><div class=\"panel-heading\">","</div><div class=\"panel-content\">","</div></div></div>"]
var link = "";
$(document).ready(function() { 
    for(var i=0; i<allFactory.length; i++){
        if(i==0){
            $("#factory").append("<li class=\"active\"><a data-toggle=\"tab\" href=\"#" + allFactory[i].name + li[1] + allFactory[i].name + li[2]);
        }
        else{
            $("#factory").append(li[0] + allFactory[i].name + li[1] + allFactory[i].name + li[2]);
        }
        for(var j=0; j<allFactory[i].workshop.length; j++){
            for(var k=0; k<allFactory[i].workshop[j].monitor.length; k++){
                link = link + "<a href=\"javascript:;\">" + allFactory[i].workshop[j].monitor[k] + "</a><br>";
            }
            if(i==0){
                $("#factoryContent").append(di[0] + allFactory[i].name + "\" class=\"tab-pane active\"><div class=\"panel panel-success\"><div class=\"panel-heading\">" + allFactory[i].workshop[j].name + di[2] + link + di[3]);
            }
            else{
               $("#factoryContent").append(di[0] + allFactory[i].name + di[1] + allFactory[i].workshop[j].name + di[2] + link + di[3]); 
            }
            link="";
        }
    }


});