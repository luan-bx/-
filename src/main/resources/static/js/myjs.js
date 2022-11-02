function change(dateid,valueid){
    time=document.getElementById(dateid).value;
    console.log(time);
    time=time.replace("T"," ");
    time=time+":00.0";
    document.getElementById(valueid).value=time;
    console.log(document.getElementById(valueid).value);
}